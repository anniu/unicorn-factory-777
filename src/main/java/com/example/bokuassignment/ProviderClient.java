package com.example.bokuassignment;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.UUID;

@HttpExchange(
        accept = MediaType.APPLICATION_JSON_VALUE
)
public interface ProviderClient {

    @GetExchange("/sms/send")
    ResponseEntity<Void> replyProvider(@RequestParam String message,
                                 @RequestParam("mo_message_id") UUID moMessageId,
                                 @RequestParam String receiver,
                                 @RequestParam String operator
    );

}
