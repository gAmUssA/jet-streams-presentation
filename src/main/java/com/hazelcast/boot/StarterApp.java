package com.hazelcast.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StarterApp {
    public static void main(String[] args) {
        System.getProperties().put("server.port", 9091);
        SpringApplication
                .run(StarterApp.class, args);
    }
}
