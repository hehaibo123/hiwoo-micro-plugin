package com.hiwoo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan(basePackages = {"com.hiwoo.*"})
@SpringBootApplication
public class HiWooEquipmentServer {

    public static void main(String[] args) {
        SpringApplication.run(HiWooEquipmentServer.class, args);
    }

}
