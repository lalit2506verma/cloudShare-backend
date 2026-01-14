# ☁️ CloudShare-backend

CloudShare Backend is a **Spring Boot–based backend service** that powers the CloudShare file-sharing application.  
It provides **secure authentication, file management, and payment-based credit handling** using RESTful APIs.

## 🔗 Related Repository

This backend service is used by the CloudShare frontend application:

➡️ **Frontend Repository:**  
[https://github.com/lalit2506verma/cloudShare-frontend](https://github.com/lalit2506verma/CloudShare)

---

## 🚀 Features

- 🔐 JWT-based Authentication & Authorization
- 👤 Role-based Access Control
- 📁 Secure File Upload & Download APIs
- 🔗 File Sharing with Ownership Validation
- 💳 Credit-based Upload System (Razorpay)
- 🛡️ Spring Security with Custom JWT Filter
- 🗄️ MongoDB for scalable data storage

---

## 🛠️ Tech Stack

- **Java**
- **Spring Boot**
- **Spring Security**
- **JWT**
- **MongoDB**
- **Razorpay Payment Gateway**
- **Maven**
- **REST APIs**

---

## 📂 Project Structure

```text
src/main/java
 ├── config        # Security & configuration
 ├── controller    # REST controllers
 ├── service       # Business logic
 ├── repository    # MongoDB repositories
 ├── model         # Domain models
 └── dto           # Request & response DTOs
