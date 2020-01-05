package com.paro.hospitalservice.controller;

import com.paro.hospitalservice.model.Hospital;
import com.paro.hospitalservice.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping(path = "/service", produces = "application/json")   //Will only handle requests if the request’s Accept header includes “application/json”
@CrossOrigin(origins="*")                                           //Allows clients from any domain to consume the API, especially for frontend
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
    public Mono<Hospital> getByHospitalId(@PathVariable("id") Long hospitalId){
        Mono<Hospital> hospitalFound=hospitalService.getByHospitalId(hospitalId);
        return hospitalFound;
    }

    @PostMapping(value = "/", consumes = "application/json")    //Will only handle requests whose Content-type matches application/json
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<Hospital> add(@RequestBody Mono<Hospital> hospital){
        Mono<Hospital> hospitalAdded=hospitalService.add(hospital);
        return hospitalAdded;
    }

    @PutMapping(value = "/{id}")
    public Mono<Hospital> putById(@PathVariable("id") Long hospitalId, @RequestBody Mono<Hospital> hospital){
        Mono<Hospital> hospitalPut=hospitalService.put(hospitalId, hospital);
        return hospitalPut;
    }
    //1-Patch: Hospital object
    /*
    @ResponseStatus(HttpStatus.CREATED)
    @PatchMapping(value = "/{id}", consumes = "application/json")
    public Mono<Hospital> patchById(@PathVariable("id") Long hospitalId, @RequestBody Hospital hospital){
        Mono<Hospital> hospitalPatched=hospitalService.patch(hospitalId, hospital);
        hospitalPatched.subscribe(System.out::println);
        return hospitalPatched;
    }
    */

    //2-Patch: Mono<Hospital> object
    @ResponseStatus(HttpStatus.CREATED)
    @PatchMapping(value = "/{id}", consumes = "application/json")
    public Mono<Hospital> patchById(@PathVariable("id") Long hospitalId, @RequestBody Mono<Hospital> hospital){
        Mono<Hospital> hospitalPatched=hospitalService.patch(hospitalId, hospital);

        return hospitalPatched;

    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)   // 204
    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable("id") Long hospitalId) {
        hospitalService.deleteById(hospitalId);
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
