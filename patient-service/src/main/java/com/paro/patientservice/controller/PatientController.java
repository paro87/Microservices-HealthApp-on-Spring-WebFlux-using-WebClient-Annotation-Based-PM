package com.paro.patientservice.controller;

import com.paro.patientservice.model.Patient;
import com.paro.patientservice.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@RestController
//@RequestMapping("/v1")     //For Swagger UI: http://localhost:8092/swagger-ui.html
public class PatientController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientController.class);

    private PatientService patientService;
    @Autowired
    public PatientController(PatientService patientService){
        this.patientService=patientService;
    }

    @GetMapping(value = "/")
    public Flux<Patient> getAll(){
        LOGGER.info("Patients found");
        return patientService.getAll();
    }

    @GetMapping(value = "/{id}")
    public Mono<Patient> getByPatientId(@PathVariable("id") Long patientId){
        LOGGER.info("Patient found with id={}", patientId);
        return patientService.getByPatientId(patientId);
    }
    //1) Receiving and saving Patient object
/*    @PostMapping(value = "/", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Patient> add(@RequestBody Patient patient){
        Mono<Patient> patientAdded=patientService.add(patient);
        LOGGER.info("Patient added with id={}", patient.getPatientId());
        return patientAdded;
    }*/

    //2) Receiving and saving Mono<Patient> object
    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Patient> add(@RequestBody Mono<Patient> patient){
        Mono<Patient> patientAdded=patientService.add(patient);
        //LOGGER.info("Patient added with id={}", patientAdded.subscribe(patient1 -> System.out.println(patient1.getPatientId())));
        return patientAdded;
    }

    @GetMapping(value = "/department/{departmentId}")
    public Flux<Patient> getByDepartmentId(@PathVariable("departmentId") Long departmentId){
        LOGGER.info("Patients found in department with id={}", departmentId);
        return patientService.getByDepartmentId(departmentId);
    }

    @GetMapping(value="/hospital/{hospitalId}")
    public Flux<Patient> getByHospitalId(@PathVariable("hospitalId") Long hospitalId) {
        LOGGER.info("Patients found in hospital with id={}", hospitalId);
        return patientService.getByHospitalId(hospitalId);
    }
}