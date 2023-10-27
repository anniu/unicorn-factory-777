package com.example.bokuassignment;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AdapterController {

    private final AdapterService adapterService;

    @GetMapping("/sms")
    public String getPaymentNotification(@RequestParam("message_id") UUID messageId,
                                         @RequestParam String sender,
                                         @RequestParam String text,
                                         @RequestParam String receiver,
                                         @RequestParam String operator,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime timestamp
    ) {
        adapterService.handlePaymentNotification(messageId, sender, text, receiver, operator, timestamp);
        return "OK";
    }
}
