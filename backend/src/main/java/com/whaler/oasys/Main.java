package com.whaler.oasys;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Main {
    public static ConcurrentHashMap<String, String> sharedData = new ConcurrentHashMap<>();
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}