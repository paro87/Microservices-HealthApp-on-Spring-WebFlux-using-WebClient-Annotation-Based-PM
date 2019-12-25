package com.paro.departmentservice.repository;

import com.paro.departmentservice.model.Department;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface DepartmentRepository extends ReactiveCrudRepository<Department, Long> {
    Flux<Department> findByHospitalId(Long hospitalId);

    Mono<Department> findByDepartmentId(Long departmentId);
}
