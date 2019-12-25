package com.paro.hospitalservice.repository;

import com.paro.hospitalservice.model.Hospital;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface HospitalRepository extends ReactiveCrudRepository<Hospital, Long> {
    Mono<Hospital> findByHospitalId(Long hospitalId);
}
