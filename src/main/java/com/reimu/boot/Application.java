package com.reimu.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan("com.reimu")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
