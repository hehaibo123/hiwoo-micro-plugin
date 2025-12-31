package com.hiwoo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan(basePackages = {"com.hiwoo.*"})
@SpringBootApplication
public class HiWooIndustryServer {

    public static void main(String[] args) {
        SpringApplication.run(HiWooIndustryServer.class, args);
    }

}
