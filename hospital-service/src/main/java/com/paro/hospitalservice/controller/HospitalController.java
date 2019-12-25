package com.paro.hospitalservice.controller;

import com.paro.hospitalservice.model.Hospital;
import com.paro.hospitalservice.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
//@RequestMapping("/v1")     //For Swagger UI: http://localhost:8090/swagger-ui.html
public class HospitalController {

    private HospitalService hospitalService;

    @Autowired
    public HospitalController(HospitalService hospitalService){
        this.hospitalService=hospitalService;
    }

    @GetMapping(value = "/")
    public Flux<Hospital> getAll(){
        Flux<Hospital> hospitalsFound=hospitalService.getAll();
        return hospitalsFound;
    }
    @GetMapping(value = "/{id}")
    public Mono<Hospital> getById(@PathVariable("id") Long hospitalId){
        Mono<Hospital> hospitalFound=hospitalService.getByHospitalId(hospitalId);
        return hospitalFound;
    }

    @PostMapping(value = "/")
    public Mono<Hospital> add(@RequestBody Mono<Hospital> hospital){
        Mono<Hospital> hospitalAdded=hospitalService.add(hospital);
        return hospitalAdded;
    }

    @GetMapping(value = "/{id}/with-departments")
    public Mono<Hospital> getHospitalWithDepartments(@PathVariable("id") Long hospitalId){
        Mono<Hospital> hospital=hospitalService.getHospitalWithDepartments(hospitalId);
        hospital.subscribe();
        return hospital;
    }
    @GetMapping(value = "/{id}/with-departments-and-patients")
    public Mono<Hospital> getHospitalWithDepartmentsAndPatients(@PathVariable("id") Long hospitalId){
        Mono<Hospital> hospital=hospitalService.getHospitalWithDepartmentsAndPatients(hospitalId);
        hospital.subscribe();
        return hospital;
    }
    @GetMapping(value = "/{id}/with-patients")
    public Mono<Hospital> getHospitalWithPatients(@PathVariable("id") Long hospitalId){
        Mono<Hospital> hospital=hospitalService.getHospitalWithPatients(hospitalId);
        return hospital;
    }

}
