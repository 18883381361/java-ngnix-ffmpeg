package com.example.ota;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class Demo1Application {
    public static void main(String[] args) {

        SpringApplication.run(Demo1Application.class, args);
    }



}
