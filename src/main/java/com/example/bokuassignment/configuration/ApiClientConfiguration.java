package com.example.bokuassignment.configuration;

import com.example.bokuassignment.MerchantClient;
import com.example.bokuassignment.ProviderClient;
import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.time.Duration;

@Slf4j
@Configuration
public class ApiClientConfiguration {

    public static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    // would use a conf file or whatever is suitable in a real life situation
    public static final String PROVIDER_API_USERNAME = "fortumo";
    public static final String PROVIDER_API_PASSWORD = "topsecret";

    @Bean
    public MerchantClient merchantApiClient() {
//        HttpClient httpClient = HttpClient.create()
//                .wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
//        ClientHttpConnector conn = new ReactorClientHttpConnector(httpClient);

        var webClient = WebClient.builder()// .clientConnector(conn)
                .baseUrl("https://testmerchant.fortumo.mobi/api")
                .filter(logRequest())
                .build();
        var proxy = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient))
                .blockTimeout(REQUEST_TIMEOUT)
                .build();
        return proxy.createClient(MerchantClient.class);
    }

    @Bean
    public ProviderClient providerApiClient() {
//        HttpClient httpClient = HttpClient.create()
//                .wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
//        ClientHttpConnector conn = new ReactorClientHttpConnector(httpClient);

        var webClient = WebClient.builder()
//                .clientConnector(conn)
                .defaultHeaders(httpHeaders -> httpHeaders
                        .setBasicAuth(PROVIDER_API_USERNAME, PROVIDER_API_PASSWORD)
                )
                .baseUrl("https://testprovider.fortumo.mobi")
                .filter(logRequest())
                .filter(logResponse())
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
