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
@RequestMapping(path = "/service", produces = "application/json")   //Will only handle requests if the request’s Accept header includes “application/json”
@CrossOrigin(origins="*")                                           //Allows clients from any domain to consume the API, especially for frontend
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
    @PostMapping(value = "/", consumes = "application/json")    //Will only handle requests whose Content-type matches application/json
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<Patient> add(@RequestBody Mono<Patient> patient){
        Mono<Patient> patientAdded=patientService.add(patient);
        //LOGGER.info("Patient added with id={}", patientAdded.subscribe(patient1 -> System.out.println(patient1.getPatientId())));
        return patientAdded;
    }

    @PutMapping(value = "/{id}")
    public Mono<Patient> putById(@PathVariable("id") Long patientId, @RequestBody Mono<Patient> patient){
        Mono<Patient> patientPut=patientService.put(patientId, patient);
        return patientPut;
    }
    //1-Patch: Patient object
    /*
    @ResponseStatus(HttpStatus.CREATED)
    @PatchMapping(value = "/{id}", consumes = "application/json")
    public Mono<Patient> patchById(@PathVariable("id") Long patientId, @RequestBody Patient patient){
        Mono<Patient> patientPatched=patientService.patch(patientId, patient);
        patientPatched.subscribe(System.out::println);
        return patientPatched;
    }
    */

    //2-Patch: Mono<Patient> object
    @ResponseStatus(HttpStatus.CREATED)
    @PatchMapping(value = "/{id}", consumes = "application/json")
    public Mono<Patient> patchById(@PathVariable("id") Long patientId, @RequestBody Mono<Patient> patient){
        Mono<Patient> patientPatched=patientService.patch(patientId, patient);

        return patientPatched;

    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)   // 204
    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable("id") Long patientId) {
        patientService.deleteById(patientId);
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