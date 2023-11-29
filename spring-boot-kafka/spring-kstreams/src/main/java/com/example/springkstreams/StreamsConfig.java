package com.example.springkstreams;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.function.Consumer;

@Configuration
@EnableConfigurationProperties(StreamsConfig.StoreProps.class)
public class StreamsConfig {

    private static final String TRANSFORMER_NAME = "order_transformer";

    @Bean
    public Consumer<KStream<String, byte[]>> inputTopic(OrderTransformerSupplier transformerSupplier) {
        return packageData -> packageData.transform(transformerSupplier, Named.as(TRANSFORMER_NAME));
    }

    @Data
    @Validated
    @ConfigurationProperties("app.kafka.store")
    public static class StoreProps {
        @NotNull
        private String bufferName;
    }
}
