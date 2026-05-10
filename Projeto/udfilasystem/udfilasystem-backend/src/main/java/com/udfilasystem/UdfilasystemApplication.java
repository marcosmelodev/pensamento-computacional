package com.udfilasystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UdfilasystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(UdfilasystemApplication.class, args);
    }
}
