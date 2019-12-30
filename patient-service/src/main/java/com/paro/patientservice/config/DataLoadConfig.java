package com.paro.patientservice.config;

import com.paro.patientservice.model.Patient;
import com.paro.patientservice.repository.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.stream.Stream;

@Profile("!production")
@Configuration
public class DataLoadConfig {
    private PatientRepository patientRepository;


    @Bean
    public CommandLineRunner dataLoader(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
        return new CommandLineRunner() {
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
        };
    }
}




