package com.paro.hospitalservice.service;

import com.paro.hospitalservice.model.Department;
import com.paro.hospitalservice.model.Hospital;
import com.paro.hospitalservice.model.Patient;
import com.paro.hospitalservice.repository.HospitalRepository;
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
public class HospitalService {
    private static final Logger LOGGER= LoggerFactory.getLogger(HospitalService.class);

    private HospitalRepository hospitalRepository;

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    public void HospitalService (HospitalRepository hospitalRepository){
        this.hospitalRepository=hospitalRepository;

    }

    public Flux<Hospital> getAll(){
        LOGGER.info("Hospitals found");
        return hospitalRepository.findAll();
    }

    public Mono<Hospital> getByHospitalId(Long hospitalId){
        Mono<Hospital> hospitalFound=hospitalRepository.findByHospitalId(hospitalId);
        LOGGER.info("Hospital found with id={}", hospitalId);
        return hospitalFound;
    }

    public Mono<Hospital> add(Mono<Hospital> hospital){
        Mono<Hospital> hospitalSaved=hospitalRepository.saveAll(hospital).next();
        return hospitalSaved;
    }

    private String departmentClient="http://department-service";
    private String patientClient="http://patient-service";
    private static final String RESOURCE_PATH="/hospital/";
    private String REQUEST_URI_Department=departmentClient+RESOURCE_PATH;
    private String REQUEST_URI_Patient=patientClient+RESOURCE_PATH;

    public Mono<Hospital> getHospitalWithDepartments(Long hospitalId){
        Mono<Hospital> hospitalList=hospitalRepository.findByHospitalId(hospitalId);
        LOGGER.info("Departments found with hospital id={}", hospitalId);
        //1-Returning value without usage of Hystrix
/*
        return hospitalMono.flatMap(hospital ->{
            Flux<Department> departmentFlux = webClientBuilder.build().get().uri(REQUEST_URI_Department + hospital.getHospitalId()).exchange().flatMapMany(response -> response.bodyToFlux(Department.class));
            return departmentFlux.collectList()
                    .map(list->{
                        hospital.setDepartmentList(list);
                        return hospital;
                    });
        });
*/
        //2-Using Hystrix: Returns only List<Department> as null
         Mono<Hospital> hospitalMono=hospitalList.flatMap(hospital ->{
            Flux<Department> departmentFlux = webClientBuilder.build().get().uri(REQUEST_URI_Department + hospital.getHospitalId()).exchange().flatMapMany(response -> response.bodyToFlux(Department.class));
            Mono<Hospital> hospitalMono2= departmentFlux.collectList()
                    .map(list->{
                        hospital.setDepartmentList(list);
                        return hospital;
                    });
            return HystrixCommands
                    .from(hospitalMono2)
                    .fallback(d->{
                        List<Department> departmentListNotFound=new ArrayList<>();
                        Department departmentNotFound=new Department(0L, "UNKNOWN", hospitalId);
                        departmentListNotFound.add(departmentNotFound);
                        hospital.setDepartmentList(departmentListNotFound);
                        return Mono.just(hospital);
                    })
                    .commandName("getHospitalWithDepartments_Fallback")
                    .toMono();
        });
        return hospitalMono;

    }


    public Mono<Hospital> getHospitalWithDepartmentsAndPatients(Long hospitalId){
        Mono<Hospital> hospitalList=hospitalRepository.findByHospitalId(hospitalId);
        LOGGER.info("Departments and patients found with hospital id={}", hospitalId);
        //1-1-Returning value without usage of Hystrix
/*
        return hospitalMono.flatMap(hospital ->{
            Flux<Department> departmentFlux = webClientBuilder.build().get().uri(REQUEST_URI_Department + hospital.getHospitalId()+"/with-patients").exchange().flatMapMany(response -> response.bodyToFlux(Department.class));
            return departmentFlux.collectList()
                    .map(list->{
                        hospital.setDepartmentList(list);
                        return hospital;
                    });
        });
  */
        //2-Using Hystrix: Returns only List<Department> as null
        Mono<Hospital> hospitalMono=hospitalList.flatMap(hospital ->{
            Flux<Department> departmentFlux = webClientBuilder.build().get().uri(REQUEST_URI_Department + hospital.getHospitalId()).exchange().flatMapMany(response -> response.bodyToFlux(Department.class));
            Mono<Hospital> hospitalMono2= departmentFlux.collectList()
                    .map(list->{
                        hospital.setDepartmentList(list);
                        return hospital;
                    });
            return HystrixCommands
                    .from(hospitalMono2)
                    .fallback(d->{
                        List<Department> departmentListNotFound=new ArrayList<>();
                        Department departmentNotFound=new Department(0L, "UNKNOWN", hospitalId);
                        departmentListNotFound.add(departmentNotFound);
                        hospital.setDepartmentList(departmentListNotFound);
                        return Mono.just(hospital);
                    })
                    .commandName("getHospitalWithDepartmentsAndPatients_Fallback")
                    .toMono();
        });
        return hospitalMono;
    }


    public Mono<Hospital> getHospitalWithPatients(Long hospitalId){
        Mono<Hospital> hospitalList=hospitalRepository.findByHospitalId(hospitalId);
        LOGGER.info("Departments found with hospital id={}", hospitalId);
        //1-1-Returning value without usage of Hystrix
/*
        return hospitalMono.flatMap(hospital ->{
            Flux<Patient> patientFlux = webClientBuilder.build().get().uri(REQUEST_URI_Patient + hospital.getHospitalId()).exchange().flatMapMany(response -> response.bodyToFlux(Patient.class));
            return patientFlux.collectList()
                    .map(list->{
                        hospital.setPatientList(list);
                        return hospital;
                    });
        });
*/
        //2-Using Hystrix: Returns only List<Patient> as null
        Mono<Hospital> hospitalMono=hospitalList.flatMap(hospital ->{
            Flux<Patient> patientFlux = webClientBuilder.build().get().uri(REQUEST_URI_Patient + hospital.getHospitalId()).exchange().flatMapMany(response -> response.bodyToFlux(Patient.class));
            Mono<Hospital> hospitalMono2= patientFlux.collectList()
                    .map(list->{
                        hospital.setPatientList(list);
                        return hospital;
                    });
            return HystrixCommands
                    .from(hospitalMono2)
                    .fallback(d->{
                        List<Patient> patientListNotFound=new ArrayList<>();
                        Patient patientNotFound=new Patient(0L, "UNKNOWN", "UNKNOWN", hospitalId, 0L);
                        patientListNotFound.add(patientNotFound);
                        hospital.setPatientList(patientListNotFound);
                        return Mono.just(hospital);
                    })
                    .commandName("getHospitalWithPatients_Fallback")
                    .toMono();
        });
        return hospitalMono;
    }
}