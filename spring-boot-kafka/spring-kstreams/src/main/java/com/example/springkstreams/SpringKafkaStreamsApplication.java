package com.example.springkstreams;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@SpringBootApplication
public class SpringKafkaStreamsApplication {
    @Autowired
    private BufferStoreService bufferStoreService;

    public static void main(String[] args) {
        SpringApplication.run(SpringKafkaStreamsApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @GetMapping(value = "get/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Order> getOrder(@PathVariable(value = "orderId") String id) {
        log.info("Get: {}", id);
        Order order = bufferStoreService.get(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping(value = "getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Order>> getAllOrder() {
        List<Order> orders = bufferStoreService.getAll();
        return ResponseEntity.ok(orders);
    }
}
