package com.paro.hospitalservice;

import com.paro.hospitalservice.model.Hospital;
import com.paro.hospitalservice.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

import java.util.stream.Stream;

@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrix
public class HospitalServiceApplication implements CommandLineRunner {

    private final HospitalRepository hospitalRepository;

    @Autowired
    public HospitalServiceApplication(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(HospitalServiceApplication.class, args);
    }

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

}
