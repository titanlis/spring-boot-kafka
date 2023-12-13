package com.example.springkafka;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record _Order(Integer id, Integer amount) {
}