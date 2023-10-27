package com.example.bokuassignment;

import com.example.bokuassignment.model.MerchantNotificationDto;
import com.example.bokuassignment.model.MerchantResponseDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(
        accept = MediaType.APPLICATION_JSON_VALUE
)
public interface MerchantClient {

    @PostExchange("/sms/txt")
    ResponseEntity<MerchantResponseDto> notifyMerchantTxt(@RequestBody MerchantNotificationDto notification);

    @PostExchange("/sms/for")
    ResponseEntity<MerchantResponseDto> notifyMerchantFor(@RequestBody MerchantNotificationDto notification);

}
