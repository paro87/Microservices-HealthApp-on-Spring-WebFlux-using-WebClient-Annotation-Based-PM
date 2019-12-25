package com.paro.departmentservice;

import com.paro.departmentservice.model.Department;
import com.paro.departmentservice.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

import java.util.stream.Stream;

@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrix
public class DepartmentServiceApplication implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentServiceApplication(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(DepartmentServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        departmentRepository.deleteAll()
                .subscribe(null, null, ()-> {
                    Stream.of(new Department(11L, "Cardiology", 1L),
                            new Department(12L, "Neurology", 1L),
                            new Department(13L, "Oncology", 1L))
                            .forEach(department -> departmentRepository.save(department).subscribe(System.out::println));
                });
    }


}
