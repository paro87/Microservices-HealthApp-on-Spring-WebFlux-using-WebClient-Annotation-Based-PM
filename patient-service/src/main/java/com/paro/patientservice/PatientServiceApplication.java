package com.paro.patientservice;

import com.paro.patientservice.model.Patient;
import com.paro.patientservice.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.stream.Stream;

@SpringBootApplication
@EnableDiscoveryClient
public class PatientServiceApplication implements CommandLineRunner {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientServiceApplication(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(PatientServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        patientRepository.deleteAll()
                .subscribe(null, null, ()-> {
                    Stream.of(new Patient(1L, "John", "Graham", 1L, 11L),
                            new Patient(2L, "Mary", "Adams", 1L, 11L),
                            new Patient(3L, "Adam", "Allison", 1L, 11L),
                            new Patient(4L, "Brad", "Richards", 1L, 12L),
                            new Patient(5L, "Alan", "Johnson", 1L, 12L),
                            new Patient(6L, "Jessica", "Alba", 1L, 13L))
                            .forEach(patient -> patientRepository.save(patient).subscribe(System.out::println));
                });
    }

}
