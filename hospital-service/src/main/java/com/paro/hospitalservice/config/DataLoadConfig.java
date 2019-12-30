package com.paro.hospitalservice.config;

import com.paro.hospitalservice.model.Hospital;
import com.paro.hospitalservice.repository.HospitalRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.stream.Stream;

@Profile("!production")
@Configuration
public class DataLoadConfig {
    private HospitalRepository hospitalRepository;


    @Bean
    public CommandLineRunner dataLoader(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                hospitalRepository.deleteAll()
                        .subscribe(null, null, ()-> {
                            Stream.of(new Hospital(1L, "Mayo Clinic", "Rochester"),
                                    new Hospital(2L, "Massachusetts", "Boston"),
                                    new Hospital(3L, "Johns Hopkins", "Baltimore"))
                                    .forEach(hospital -> hospitalRepository.save(hospital).subscribe(System.out::println));
                        });
            }
        };
    }
}




