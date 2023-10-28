package com.example.bokuassignment;

import io.micrometer.context.ContextRegistry;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class BokuAssignmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(BokuAssignmentApplication.class, args);
        Hooks.enableAutomaticContextPropagation();
        ContextRegistry.getInstance()
                .registerThreadLocalAccessor("mdc", MDC::getCopyOfContextMap, MDC::setContextMap, MDC::clear);
    }

}
