package com.example.springkstreams;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BufferStoreService {
    private final ObjectMapper objectMapper;
    private final StreamsConfig.StoreProps storeProps;
    private final InteractiveQueryService interactiveQueryService;

    @SneakyThrows
    public Order get(String key) {
        ReadOnlyKeyValueStore<Object, Object> queryableStore = interactiveQueryService.getQueryableStore(storeProps.getBufferName(), QueryableStoreTypes.keyValueStore());
        byte[] bytes = (byte[]) queryableStore.get(key);
        return objectMapper.readValue(new String(bytes), Order.class);
    }

    @SneakyThrows
    public List<Order> getAll() {
        List<Order> orders = new ArrayList<>();
        ReadOnlyKeyValueStore<Object, Object> queryableStore = interactiveQueryService.getQueryableStore(storeProps.getBufferName(), QueryableStoreTypes.keyValueStore());
        KeyValueIterator<Object, Object> iterator = queryableStore.all();
        while (iterator.hasNext()) {
            KeyValue<Object, Object> keyValue = iterator.next();
            byte[] bytes = (byte[]) keyValue.value;
            Order order = objectMapper.readValue(new String(bytes), Order.class);
            orders.add(order);
        }
        return orders;
    }
}
