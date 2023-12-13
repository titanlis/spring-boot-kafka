package com.example.springkafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.cloud.stream.schema.registry.client.EnableSchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Consumer;

@Slf4j
@RestController
@RequestMapping("/api")
@SpringBootApplication
@EnableSchemaRegistryClient
public class SpringKafkaApplication {
    @Autowired
    private StreamBridge streamBridge;
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public static void main(String[] args) {
        SpringApplication.run(SpringKafkaApplication.class, args);
    }

    /**
     * Запись данных в топик order-output
     */
    @PostMapping("write")
    public ResponseEntity<String> writeOrder(@RequestBody Order order) {
        Order ord = new Order(order.getId(), order.getAmount());
        ProducerRecord<String, Order> producerRecord = new ProducerRecord<>("order-output", ord);
        // Запись в Kafka с учетом схемы avro
//        kafkaTemplate.send(producerRecord);
//        return ResponseEntity.ok("SUCCESS");

        String body = "SUCCESS";
        // Запись в Kafka с учетом схемы avro
        try{
            log.info("Send: {}", ord);
            kafkaTemplate.send(producerRecord);
            return ResponseEntity.ok(body);
        }catch (Exception e){
            e.printStackTrace();
            body = e.getMessage();

        }
        return ResponseEntity.badRequest().body(body);

    }

    /**
     * Получение записей из топика order-output и дальнейшая их запись в топик order-output-without-schema
     */
    @Bean
    public Consumer<Order> process() {
        return input -> {
            Order order = new Order(input.getId(), input.getAmount());
            log.info("Get: {}", order);
            streamBridge.send("output", order);
        };
    }
}
