package com.paro.departmentservice.service;

import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.paro.departmentservice.model.Department;
import com.paro.departmentservice.model.Patient;
import com.paro.departmentservice.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.HystrixCommands;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentService.class);
    private DepartmentRepository departmentRepository;

    private String patientClient="http://patient-service";          //The host address will be fetched from Eureka
    private static final String RESOURCE_PATH="/department/";
    private String REQUEST_URI=patientClient+RESOURCE_PATH;
/*
    //Encountered a problem during fetching a host name from Eureka
    @Autowired
    WebClient webClient;

    @LoadBalanced
    @Bean
    public WebClient webClient(){
        return WebClient.create();
    }*/

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Autowired
    private WebClient.Builder webClientBuilder;

    //For testing
//    private WebClient webClient;
//    DepartmentService(WebClient webClient){
//        this.webClient=webClient;
//    }

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository=departmentRepository;

    }

    public Flux<Department> getAll() {
        Flux<Department> departmentsFound=departmentRepository.findAll();
        LOGGER.info("Departments found");
        return departmentsFound;
    }


    public Mono<Department> getByDepartmentId(Long departmentId){
        Mono<Department> departmentFound=departmentRepository.findByDepartmentId(departmentId);
        LOGGER.info("Department found with id={}: ", departmentId);
        return departmentFound;
    }

    public Mono<Department> add(Mono<Department> department){
        Mono<Department> departmentSaved=departmentRepository.saveAll(department).next();
        LOGGER.info("Department added with id={}", departmentSaved.subscribe(d-> System.out.println(d.getDepartmentId())));
        return departmentSaved;
    }

    public Flux<Department> getByHospitalId(Long hospitalId){
        Flux<Department> departmentsFound=departmentRepository.findByHospitalId(hospitalId);
        LOGGER.info("Departments found for the hospital with an id={}", hospitalId);
        return departmentsFound;
    }


    public Flux<Department> getByHospitalWithPatients(Long hospitalId) {

        Flux<Department> departmentList = departmentRepository.findByHospitalId(hospitalId);
        //1 - Returning value without using Hystrix circuit breaker
/*
        return departmentList.flatMap(department -> {
            System.out.println("Here");
            //Flux<Patient> patientFlux = webClient.get().uri(REQUEST_URI + department.getDepartmentId()).exchange().flatMapMany(response -> response.bodyToFlux(Patient.class));
            Flux<Patient> patientFlux = webClientBuilder.build().get().uri(REQUEST_URI + department.getDepartmentId()).exchange().flatMapMany(response -> response.bodyToFlux(Patient.class));
            return patientFlux.collectList().map(list -> {
                department.setPatientList(list);
                return department;
            });
        });
*/


        //2 - Using Hystrix: Returns all department entities as null
/*
        Flux<Department> departmentFlux= departmentList.flatMap(department -> {
            System.out.println("Here");
            //Flux<Patient> patientFlux = webClient.get().uri(REQUEST_URI + department.getDepartmentId()).exchange().flatMapMany(response -> response.bodyToFlux(Patient.class));
            //Flux<Patient> patientFlux = webClientBuilder.build().get().uri(REQUEST_URI + department.getDepartmentId()).exchange().flatMapMany(response -> response.bodyToFlux(Patient.class));
            Mono<Department> departmentMono= webClientBuilder.build()
                    .get()
                    .uri(REQUEST_URI + department.getDepartmentId())
                    .exchange()
                    .flatMapMany(response -> response.bodyToFlux(Patient.class))
                    .collectList()
                    .map(list -> {
                        department.setPatientList(list);
                        return department;
                    });

            return HystrixCommands
                    .from(departmentMono)
                    //.fallback(Mono.just(new Department(department.getDepartmentId(), department.getName(), department.getHospitalId(), null)))
                    .fallback(Mono.just(new Department(0L, "UNKNOWN", 0L, null)))
                    .commandName("getByHospitalWithPatients_Fallback")
                    .toMono();

        });
        return departmentFlux;
*/

        //3 Using Hystrix: Returns only List<Patient> as null
        Flux<Department> departmentFlux= departmentList.flatMap(department -> {
            Flux<Patient> patientFlux = webClientBuilder.build().get().uri(REQUEST_URI + department.getDepartmentId()).exchange().flatMapMany(response -> response.bodyToFlux(Patient.class));
            Mono<Department>departmentMono= patientFlux.collectList()
                    .map(list -> {
                        department.setPatientList(list);
                        return department;
                    });


            return HystrixCommands
                    .from(departmentMono)
                    .fallback(d->{
                        List<Patient> patientListNotFound=new ArrayList<>();
                        Patient patientNotFound=new Patient(0L, "UNKNOWN", "UNKNOWN", hospitalId, department.getDepartmentId());
                        patientListNotFound.add(patientNotFound);
                        System.out.println(d.getClass());
                        department.setPatientList(patientListNotFound);
                        return Mono.just(department);
                    })
                    .commandName("getByHospitalWithPatients_Fallback")
                    .toMono();
        });
        return departmentFlux;






        //return departmentFlux;
        //Circuit Breaker
//        List<Patient> patientListNotFound=new ArrayList<>();
//        Flux<Department> departmentFluxFallback=departmentList.map(department -> {
//            Patient patientNotFound=new Patient(0L, "UNKNOWN", "UNKNOWN", hospitalId, department.getDepartmentId());
//            patientListNotFound.add(patientNotFound);
//            department.setPatientList(patientListNotFound);
//            return department;
//        });
//        //Flux<Department> departmentFluxFallback=Flux.just(new Department(0L, "UNKNOWN", 0L));
//
//        Flux<Department> departmentFallback= HystrixCommands.from(departmentFlux)
//                .fallback(departmentFluxFallback)
//                .commandName("getByHospitalWithPatients_Fallback")
//                .commandProperties(HystrixCommandProperties.defaultSetter()
//                .withExecutionTimeoutInMilliseconds(10000))
//                .toFlux();
//
//        return departmentFallback;
    }
}
