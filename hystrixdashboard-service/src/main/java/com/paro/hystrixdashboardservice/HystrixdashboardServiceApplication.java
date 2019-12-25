package com.paro.hystrixdashboardservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

//This app monitors Hystrix metrics on Gateway-service
@SpringBootApplication
@EnableHystrixDashboard
public class HystrixdashboardServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HystrixdashboardServiceApplication.class, args);
    }

}
