package com.example.springkstreams;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PackageTransformer implements Transformer<String, byte[], KeyValue<String, byte[]>> {
    private final ObjectMapper objectMapper;
    private final StreamsConfig.StoreProps storeProps;

    private KeyValueStore<String, byte[]> bufferStore;

    @Override
    public void init(ProcessorContext context) {
        this.bufferStore = context.getStateStore(storeProps.getBufferName());
    }

    @Override
    @SneakyThrows
    public KeyValue<String, byte[]> transform(String key, byte[] value) {
        log.info("Entering transform -- order {}", objectMapper.readValue(value, Order.class));
        bufferStore.put(UUID.randomUUID().toString(), value);
        return null;
    }

    @Override
    public void close() {
        // not used
    }
}
