package com.paro.patientservice.repository;

import com.paro.patientservice.model.Patient;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PatientRepository extends ReactiveCrudRepository<Patient, String> {
    Flux<Patient> findByDepartmentId(Long departmentId);
    Flux<Patient> findByHospitalId(Long hospitalId);

    Mono<Patient> findByPatientId(Long patientId);
}
