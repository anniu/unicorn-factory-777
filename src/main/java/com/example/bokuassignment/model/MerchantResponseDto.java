package com.example.bokuassignment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MerchantResponseDto(
        @JsonProperty("reply_message")
        String replyMessage
) {
}
