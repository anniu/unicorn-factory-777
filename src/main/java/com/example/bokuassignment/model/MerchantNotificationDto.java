package com.example.bokuassignment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record MerchantNotificationDto(
        String shortcode,
        Keyword keyword,
        String message,
        String operator,
        String sender,
        @JsonProperty("transaction_id")
        UUID transactionId
) {
}
