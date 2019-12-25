package com.paro.gatewayservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class HystrixController {

    @GetMapping("/hospital")
    public String hospitalServiceFallback(){
        return "Hospital service is overloaded! Please try after some time.";
    }

    @GetMapping("/department")
    public String departmentServiceFallback(){
        return "Department service is overloaded! Please try after some time.";
    }

    @GetMapping("/patient")
    public String patientServiceFallback(){
        return "Patient service is overloaded! Please try after some time.";
    }
}
