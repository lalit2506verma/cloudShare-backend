package com.lalitVerma.cloudShare.services.impl;

import com.lalitVerma.cloudShare.dto.PaymentDTO;
import com.lalitVerma.cloudShare.dto.PaymentVerificationDTO;
import com.lalitVerma.cloudShare.entities.PaymentTransaction;
import com.lalitVerma.cloudShare.entities.User;
import com.lalitVerma.cloudShare.entities.UserCredits;
import com.lalitVerma.cloudShare.exception.PaymentFailedException;
import com.lalitVerma.cloudShare.repository.PaymentTransactionRepository;
import com.lalitVerma.cloudShare.services.PaymentService;
import com.lalitVerma.cloudShare.services.UserCreditsService;
import com.lalitVerma.cloudShare.services.UserService;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final UserService userService;
    private final UserCreditsService userCreditsService;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    public PaymentDTO createOrder(PaymentDTO paymentDTO) {
        log.info("=== CREATE ORDER - START ===");
        log.info("Request - PlanId: {}, Amount: {}, Currency: {}",
                paymentDTO.getPlanId(), paymentDTO.getAmount(), paymentDTO.getCurrency());
        try{
            User currentUser = this.userService.getCurrentUser();
            String userId = currentUser.getId();
            log.info("Creating order for User ID: {}, Email: {}", userId, currentUser.getEmail());

            // Initialize Razorpay client
            log.debug("Initializing Razorpay client with key: {}",
                    razorpayKeyId.substring(0, Math.min(razorpayKeyId.length(), 15)) + "...");
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", paymentDTO.getAmount());
            orderRequest.put("currency", paymentDTO.getCurrency());
            String receipt = "order_" + System.currentTimeMillis();
            orderRequest.put("receipt", receipt);

            log.debug("Order request payload: {}", orderRequest.toString());

            Order order =  razorpayClient.orders.create(orderRequest);
            String orderId = order.get("id").toString();

            // Creating transaction record
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .orderId(orderId)
                    .amount(paymentDTO.getAmount())
                    .userId(userId)
                    .planId(paymentDTO.getPlanId())
                    .currency(paymentDTO.getCurrency())
                    .status("PENDING")
                    .transactionDate(LocalDateTime.now())
                    .userEmail(currentUser.getEmail())
                    .build();

            this.paymentTransactionRepository.save(transaction);
            log.info("Transaction record saved with status: PENDING");

            log.info("=== CREATE ORDER - SUCCESS ===");
            return PaymentDTO.builder()
                    .orderId(orderId)
                    .success(true)
                    .message("Order created successfully")
                    .build();

        } catch (RazorpayException e) {
            log.error("=== CREATE ORDER - RAZORPAY ERROR ===");
            log.error("Razorpay API error: {}", e.getMessage(), e);
            throw new PaymentFailedException("Order Creation failed");
        }
        catch (Exception e){
            log.error("=== CREATE ORDER - UNEXPECTED ERROR ===");
            log.error("Unexpected error during order creation: {}", e.getMessage(), e);
            return PaymentDTO.builder()
                    .success(false)
                    .message("Error Creating Order")
                    .build();
        }

    }

    @Override
    public PaymentDTO verifyPayment(PaymentVerificationDTO paymentRequest) {
        log.info("=== VERIFY PAYMENT - START ===");
        log.info("Order ID: {}, Payment ID: {}, Plan ID: {}",
                paymentRequest.getRazorpay_order_id(),
                paymentRequest.getRazorpay_payment_id(),
                paymentRequest.getPlanId());

        try {
            // Get current user
            User user = userService.getCurrentUser();
            String userId = user.getId();
            log.info("Verifying payment for User ID: {}, Email: {}", userId, user.getEmail());

            // Generate signature
            String signatureBase = paymentRequest.getRazorpay_order_id() + "|"
                    + paymentRequest.getRazorpay_payment_id();
            log.debug("Signature base string: {}", signatureBase);

            String generatedSignature = generateHmacSha256Signature(signatureBase, razorpayKeySecret);
            log.debug("Generated signature: {}...", generatedSignature.substring(0, 20));
            log.debug("Received signature: {}...",
                    paymentRequest.getRazorpay_signature().substring(0, 20));

            // Verify signature
            if (!generatedSignature.equals(paymentRequest.getRazorpay_signature())) {
                log.warn("=== SIGNATURE VERIFICATION FAILED ===");
                log.warn("Signature mismatch for Order ID: {}", paymentRequest.getRazorpay_order_id());
                log.warn("Expected: {}...", generatedSignature.substring(0, 20));
                log.warn("Received: {}...", paymentRequest.getRazorpay_signature().substring(0, 20));

                updateTransactionStatus(
                        paymentRequest.getRazorpay_order_id(),
                        "FAILED",
                        paymentRequest.getRazorpay_payment_id(),
                        null,
                        null
                );

                return PaymentDTO.builder()
                        .success(false)
                        .message("Payment signature verification failed")
                        .build();
            }

            log.info("Signature verification successful!");

            // Fetching the Payment method
            String paymentMethod = fetchPaymentMethod(paymentRequest.getRazorpay_payment_id());

            // Determine credits based on plan
            int creditsToBeAdded = 0;
            String selectedPlan = "BASIC";

            switch (paymentRequest.getPlanId().toLowerCase()) {
                case "premium":
                    creditsToBeAdded = 500;
                    selectedPlan = "PREMIUM";
                    log.info("Premium plan selected: {} credits", creditsToBeAdded);
                    break;

                case "ultimate":
                    creditsToBeAdded = 5000;
                    selectedPlan = "ULTIMATE";
                    log.info("Ultimate plan selected: {} credits", creditsToBeAdded);
                    break;

                default:
                    log.warn("Unknown plan ID: {}", paymentRequest.getPlanId());
                    break;
            }

            // Add credits if valid plan
            if (creditsToBeAdded > 0) {
                log.info("Adding {} credits to user {}", creditsToBeAdded, userId);

                this.userCreditsService.addCredits(userId, creditsToBeAdded, selectedPlan);
                log.info("Credits added successfully");

                updateTransactionStatus(
                        paymentRequest.getRazorpay_order_id(),
                        "SUCCESS",
                        paymentRequest.getRazorpay_payment_id(),
                        creditsToBeAdded,
                        paymentMethod
                );

                UserCredits updatedCredits = userCreditsService.getUserCredits(userId);
                log.info("User now has total credits: {}", updatedCredits.getCredits());

                log.info("=== VERIFY PAYMENT - SUCCESS ===");
                return PaymentDTO.builder()
                        .success(true)
                        .message("Payment verified and credits added successfully")
                        .credits(updatedCredits.getCredits())
                        .build();

            } else {
                log.warn("=== VERIFY PAYMENT - INVALID PLAN ===");
                log.warn("Invalid plan selected: {}", paymentRequest.getPlanId());

                updateTransactionStatus(
                        paymentRequest.getRazorpay_order_id(),
                        "FAILED",
                        paymentRequest.getRazorpay_payment_id(),
                        null,
                        null
                );

                return PaymentDTO.builder()
                        .success(false)
                        .message("Invalid plan selected")
                        .build();
            }
        }
        catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("=== VERIFY PAYMENT - CRYPTOGRAPHIC ERROR ===");
            log.error("Error generating HMAC signature: {}", e.getMessage(), e);

            updateTransactionStatus(
                    paymentRequest.getRazorpay_order_id(),
                    "ERROR",
                    paymentRequest.getRazorpay_payment_id(),
                    null,
                    null
            );

            return PaymentDTO.builder()
                    .success(false)
                    .message("Payment verification error: " + e.getMessage())
                    .build();

        } catch (Exception e) {
            log.error("=== VERIFY PAYMENT - UNEXPECTED ERROR ===");
            log.error("Unexpected error during payment verification: {}", e.getMessage(), e);

            updateTransactionStatus(
                    paymentRequest.getRazorpay_order_id(),
                    "ERROR",
                    paymentRequest.getRazorpay_payment_id(),
                    null,
                    null
            );

            return PaymentDTO.builder()
                    .success(false)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    private String fetchPaymentMethod(String paymentId) {
        log.debug("Fetching payment details for Payment ID: {}", paymentId);

        try{
            // Creating Razorpay client
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            // fetch payment details
            Payment payment = client.payments.fetch(paymentId);
            JSONObject paymentJson = payment.toJson();
            String method = paymentJson.optString("method", "Unknown");

            String paymentMethod = method;

            switch (paymentMethod.toLowerCase()) {
                case "card":
                    // Get card type (credit/debit) and network (Visa, Mastercard, etc.)
                    if(payment.has("card")){
                        JSONObject card = paymentJson.getJSONObject("card");
                        String cardType = card.optString("type", "");
                        String network = card.optString("network", "");
                        paymentMethod = String.format("Card (%s %s)", cardType, network);

                    }
                    break;

                case "upi":
                    // Get UPI VPA if available
                    String vpa = paymentJson.optString("vpa", "");
                    if (!vpa.isEmpty()) {
                        paymentMethod = String.format("UPI (%s)", vpa);
                    } else {
                        paymentMethod = "UPI";
                    }
                    break;

                case "netbanking":
                    // Get bank name
                    String bank = paymentJson.optString("bank", "");
                    if (!bank.isEmpty()) {
                        paymentMethod = String.format("NetBanking (%s)", bank);
                    } else {
                        paymentMethod = "NetBanking";
                    }
                    break;

                case "wallet":
                    // Get wallet name
                    String wallet = paymentJson.optString("wallet", "");
                    if (!wallet.isEmpty()) {
                        paymentMethod = String.format("Wallet (%s)", wallet);
                    } else {
                        paymentMethod = "Wallet";
                    }
                    break;

                case "emi":
                    paymentMethod = "EMI";
                    break;

                case "cardless_emi":
                    paymentMethod = "Cardless EMI";
                    break;

                case "paylater":
                    paymentMethod = "Pay Later";
                    break;
            }

            log.info("Payment method details: {}", paymentMethod);
            return paymentMethod;

        }
        catch (RazorpayException e) {
            log.error("Error fetching payment method details: {}", e.getMessage(), e);
            return "Unknown";
        }
        catch (Exception e) {
            log.error("Unexpected error fetching payment method: {}", e.getMessage(), e);
            return "Unknown";
        }
    }

    private void updateTransactionStatus(String razorpayOrderId, String status,
                                         String razorpayPaymentId, Integer creditsToAdd, String paymentMethod) {
        log.debug("Updating transaction status: Order ID: {}, Status: {}, Payment ID: {}, Credits: {}, PaymentMethod {}",
                razorpayOrderId, status, razorpayPaymentId, creditsToAdd, paymentMethod);

        if (razorpayOrderId == null || razorpayOrderId.isEmpty()) {
            log.warn("Cannot update transaction: Order ID is null or empty");
            return;
        }

        if (razorpayPaymentId == null || razorpayPaymentId.isEmpty()) {
            log.warn("Cannot update transaction: Payment ID is null or empty");
            return;
        }

        try {
            PaymentTransaction transaction = this.paymentTransactionRepository.findByOrderId(razorpayOrderId);

            if (transaction != null) {
                log.debug("Transaction found, updating status to: {}", status);

                transaction.setStatus(status);
                transaction.setPaymentId(razorpayPaymentId);
                transaction.setPaymentMode(paymentMethod);

                if (creditsToAdd != null) {
                    transaction.setCreditsAdded(creditsToAdd);
                    log.debug("Credits added to transaction: {}", creditsToAdd);
                }

                this.paymentTransactionRepository.save(transaction);
                log.info("Transaction status updated successfully: Order ID: {}, Status: {}",
                        razorpayOrderId, status);
            } else {
                log.warn("Transaction not found for Order ID: {}", razorpayOrderId);
            }
        } catch (Exception e) {
            log.error("Error updating transaction status for Order ID: {}", razorpayOrderId, e);
        }
    }

    /**
     * Generate HMAC-SHA256 signature for payment verification
     */
    private String generateHmacSha256Signature(String signatureBase, String razorpayKeySecret)
            throws NoSuchAlgorithmException, InvalidKeyException {

        log.debug("Generating HMAC-SHA256 signature");

        // Validate inputs
        if (signatureBase == null || razorpayKeySecret == null) {
            log.error("Cannot generate signature: signatureBase or secret is null");
            throw new IllegalArgumentException("signatureBase and razorpayKeySecret cannot be null");
        }

        try {
            // Create HMAC-SHA256 instance
            Mac mac = Mac.getInstance("HmacSHA256");

            // Create secret key spec with explicit UTF-8 encoding
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    razorpayKeySecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );

            // Initialize Mac with the secret key
            mac.init(secretKeySpec);

            // Compute the HMAC
            byte[] hmacBytes = mac.doFinal(signatureBase.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder(hmacBytes.length * 2);
            for (byte b : hmacBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');  // Pad with leading zero
                }
                hexString.append(hex);
            }

            String signature = hexString.toString();
            log.debug("Signature generated successfully: {}...", signature.substring(0, 20));

            return signature;

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error generating HMAC signature: {}", e.getMessage());
            throw e;
        }
    }
}
