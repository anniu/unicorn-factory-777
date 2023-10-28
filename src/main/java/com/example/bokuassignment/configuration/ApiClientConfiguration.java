package com.example.bokuassignment.configuration;

import com.example.bokuassignment.MerchantClient;
import com.example.bokuassignment.ProviderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Configuration
public class ApiClientConfiguration {

    public static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);

    // would use a conf file or whatever is suitable in a real life situation
    public static final String PROVIDER_API_USERNAME = "fortumo";
    public static final String PROVIDER_API_PASSWORD = "topsecret";

    @Bean
    public MerchantClient merchantApiClient() {
        var webClient = WebClient.builder()
                .baseUrl("https://testmerchant.fortumo.mobi/api")
                .filter(logResponse())
                .filter(logRequest())
                .build();
        var proxy = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient))
                .blockTimeout(REQUEST_TIMEOUT)
                .build();
        return proxy.createClient(MerchantClient.class);
    }

    @Bean
    public ProviderClient providerApiClient() {
        var webClient = WebClient.builder()
                .defaultHeaders(httpHeaders -> httpHeaders
                        .setBasicAuth(PROVIDER_API_USERNAME, PROVIDER_API_PASSWORD)
                )
                .baseUrl("https://testprovider.fortumo.mobi")
                .filter(logResponse())
                .filter(logRequest())
                .build();
        var proxy = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient))
                .blockTimeout(REQUEST_TIMEOUT)
                .build();
        return proxy.createClient(ProviderClient.class);
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("Request: {} {}", request.method(), request.url());
            return Mono.just(request);
        });
    }

    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response status: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}
