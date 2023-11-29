### Основное

Сборка проекта: mvn clean install
Запуск сервисов в Docker: docker-compose up -d --build

## Spring-kafka

Сервис Spring-kafka предназначен для отправки сообщений в Kafka, учетом схемы сообщения, которая указана в
**/resources/avro/order.avsc**

Cервис имеет свой API, через который отправлется сообщение, пример:

```http request
POST /api/write HTTP/1.1
Host: localhost:8089
Content-Type: application/json

{
"id" : 123,
"amount": 4444
}
```

## Spring-kstreams

Сервис Spring-kstreams предназначен для получения сообщений из топика Kafka и записи его во внутреннее хранилище для
кэша

Слушатель сообщений настраивается посредством Spring Cloud Stream, а именно указанием функциального интерфейса

```java
   @Bean
public Consumer<KStream<String, byte[]>>inputTopic(OrderTransformerSupplier transformerSupplier){
        return packageData->packageData.transform(transformerSupplier,Named.as(TRANSFORMER_NAME));
        }
```

В конфигурации указывается то же самое наименование метода **inputTopic**

```yml
      bindings:
        inputTopic-in-0:
          destination: ${app.kafka.topic.input}
      function:
        definition: inputTopic
```

Конфигурация хранимой структуры во внутреннем хранилище в классе OrderTransformerSupplier

```java
@Override
public Set<StoreBuilder<?>>stores(){
        return Set.of(Stores.keyValueStoreBuilder(
        Stores.persistentKeyValueStore(storeProps.getBufferName()),
        Serdes.String(),
        Serdes.ByteArray()));
        }
```

Запись сообщения во внутреннее хранилище происходит в классе OrderTransformer

```java
    @Override
@SneakyThrows
public KeyValue<String, byte[]>transform(String key,byte[]value){
        log.info("Entering transform -- order {}",objectMapper.readValue(value,Order.class));
        bufferStore.put(UUID.randomUUID().toString(),value);
        return null;
        }
```

## Настройка Sink Connector

При запуске Kafka Connect в Docker, требуется указать настройки коннектора:

```http request
curl http://localhost:8083/connectors -X POST -H'Content-type: application/json' -H'Accept: application/json' -d'{
"name": "postgres-sink",
"config": {
"topics": "order-output",
"connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
"key.converter": "org.apache.kafka.connect.storage.StringConverter",
"value.converter": "io.confluent.connect.avro.AvroConverter",
"value.converter.schemas.enable": "true",
"key.converter.schemas.enable": "false",
"tasks.max": 1,
"connection.url": "jdbc:postgresql://postgres:5432/postgres?user=postgres&password=postgres",
"value.converter.schema.registry.url": "http://schema-registry:8081",
"table.name.format": "orders",
"delete.enabled": "false",
"pk.mode": "none"
}
}'
```

Для получения всех доступных настроек можно вызвать запрос:

```http request
curl -X GET "http://localhost:8083/connectors" -H "Content-Type: application/json" 
```

Для получения настроек определенного коннектора можно вызвать запрос:

```http request
curl -X GET "http://localhost:8083/connectors/postgres-sink" -H "Content-Type: application/json" 
```

Для удаления настроек определенного коннектора можно вызвать запрос:

```http request
curl -X DELETE "http://localhost:8083/connectors/postgres-sink" -H "Content-Type: application/json" 
```
