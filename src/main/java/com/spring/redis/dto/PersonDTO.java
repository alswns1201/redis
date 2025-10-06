package com.spring.redis.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.service.annotation.GetExchange;

import java.io.Serializable;

@Getter
@Setter
public class PersonDTO implements Serializable {
    private String id;
    private String name;
    private int age;
}
