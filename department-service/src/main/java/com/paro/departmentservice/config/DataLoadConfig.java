package com.paro.departmentservice.config;

import com.paro.departmentservice.model.Department;
import com.paro.departmentservice.repository.DepartmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.stream.Stream;

@Profile("!production")
@Configuration
public class DataLoadConfig {
    private DepartmentRepository departmentRepository;


    @Bean
    public CommandLineRunner dataLoader(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
        return new CommandLineRunner() {
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
        };
    }
}




