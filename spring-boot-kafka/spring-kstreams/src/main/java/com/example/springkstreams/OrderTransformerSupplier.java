package com.example.springkstreams;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class OrderTransformerSupplier implements TransformerSupplier<String, byte[], KeyValue<String, byte[]>> {

    private final StreamsConfig.StoreProps storeProps;
    private final ObjectProvider<Transformer<String, byte[], KeyValue<String, byte[]>>> objectProvider;

    @Override
    public Transformer<String, byte[], KeyValue<String, byte[]>> get() {
        return objectProvider.getObject();
    }

    @Override
    public Set<StoreBuilder<?>> stores() {
        return Set.of(Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(storeProps.getBufferName()),
                Serdes.String(),
                Serdes.ByteArray()));
    }
}
