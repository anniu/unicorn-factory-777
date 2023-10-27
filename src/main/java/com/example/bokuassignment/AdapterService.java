package com.example.bokuassignment;

import com.example.bokuassignment.model.Keyword;
import com.example.bokuassignment.model.MerchantNotificationDto;
import com.example.bokuassignment.model.MerchantResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdapterService {

    private final MerchantClient merchantClient;
    private final ProviderClient providerClient;

    public void handlePaymentNotification(UUID messageId,
                                          String sender,
                                          String text,
                                          String receiver,
                                          String operator,
                                          LocalDateTime timestamp
    ) {
        UUID transactionId = UUID.randomUUID();
        String cleanedSender = sender.toCharArray()[0] == '+' ? sender.substring(1) : sender;
        String response = handleNotificationToMerchant(cleanedSender, text, receiver, operator, transactionId);
        sendReplyToProvider(response, messageId, cleanedSender, operator);
    }

    private String handleNotificationToMerchant(
            String sender,
            String message,
            String shortcode,
            String operator,
            UUID transactionId
    ) {
        Keyword keyword;
        try {
            keyword = Keyword.valueOf(message.split(" ")[0]);
        } catch (IllegalArgumentException e) {
            throw new IllegalKeywordException(e.getMessage());
        }
        MerchantNotificationDto notification = new MerchantNotificationDto(
                shortcode,
                keyword,
                message,
                operator,
                sender,
                transactionId
        );

        ResponseEntity<MerchantResponseDto> response;
        switch (keyword) {
            case TXT -> response = merchantClient.notifyMerchantTxt(notification);
            case FOR -> response = merchantClient.notifyMerchantFor(notification);
            default -> throw new IllegalKeywordException("Unknown keyword");
        }

        // I'd rather use response.hasBody() but intellij draws squiggly lines then
        if (response.getStatusCode().value() == 200 && response.getBody() != null) {
            return response.getBody().replyMessage();
        } else {
            throw new RuntimeException("Merchant answered with status code %s".formatted(response.getStatusCode()));
        }
    }

    private void sendReplyToProvider(String message, UUID messageId, String receiver, String operator) {
        ResponseEntity<Void> response = providerClient.replyProvider(message, messageId, receiver, operator);
        if (response.getStatusCode().value() != 200) {
            throw new RuntimeException("Provider answered with status code %s".formatted(response.getStatusCode()));
        }
    }
}
