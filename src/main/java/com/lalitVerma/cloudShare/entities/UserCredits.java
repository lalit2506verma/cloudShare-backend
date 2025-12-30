package com.lalitVerma.cloudShare.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_credits")
@Data
@Builder
public class UserCredits {

    @Id
    private String id;
    private String userId;
    private Integer credits;
    private String subscriptionPlan; // BASIC, PREMIUM, ULTIMATE

    public UserCredits() {
    }

    public UserCredits(String id, String userId, Integer credits, String subscriptionPlan) {
        this.id = id;
        this.userId = userId;
        this.credits = credits;
        this.subscriptionPlan = subscriptionPlan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(String subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }
}
