package com.example.springkafka;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Order(Integer id, Integer amount) {
}