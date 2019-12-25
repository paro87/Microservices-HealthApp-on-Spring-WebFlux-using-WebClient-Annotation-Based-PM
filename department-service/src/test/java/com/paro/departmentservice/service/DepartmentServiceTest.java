package com.paro.departmentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paro.departmentservice.model.Department;
import com.paro.departmentservice.model.Patient;
import com.paro.departmentservice.repository.DepartmentRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


class DepartmentServiceTest {

    @Mock
    DepartmentRepository departmentRepository;


    @InjectMocks
    DepartmentService departmentService;

    private Patient patient1;
    private Patient patient2;
    private Patient patient3;

    private Department department1;
    private Department department2;
    private Department department3;

    private Mono<Patient> patientMono;
    private Flux<Patient> patientFlux;

    private Mono<Department> departmentMono;
    private Flux<Department> departmentFlux;

    private List<Department> departmentList;
    private List<Patient> patientList;

    //private WebTestClient testClient=WebTestClient.bindToController(new DepartmentService(departmentRepository)).build();;
    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        patient1=new Patient(1L, "John", "Grisham", 1L, 11L);
        patient2=new Patient(2L, "Mary", "Adams", 1L, 11L);
        patient3=new Patient(3L, "Adam", "Anniston", 1L, 11L);

        patientList=new ArrayList<>();
        patientList.add(patient1);
        patientList.add(patient2);
        patientList.add(patient3);
        patientMono=Mono.just(patient1);
        patientFlux=Flux.just(patient1, patient2, patient3);

        department1=new Department(11L, "Cardiology", 1L);
        department2=new Department(12L, "Neurology", 1L);
        department3=new Department(13L, "Oncology", 1L);
        departmentList= new ArrayList<>();
        departmentList.add(department1);
        departmentList.add(department2);
        departmentList.add(department3);
        department1.setPatientList(patientList);

        departmentMono=Mono.just(department1);
        departmentFlux=Flux.just(department1, department2, department3);

    }

    @Test
    void getByDepartmentId() {
        given(departmentRepository.findByDepartmentId(11L)).willReturn(departmentMono);
        Mono<Department> departmentFound=departmentService.getByDepartmentId(11L);
        StepVerifier.create(departmentFound).consumeNextWith(department -> {
            assertThat(department.getDepartmentId()).isEqualTo(department1.getDepartmentId());
        }).verifyComplete();
        verify(departmentRepository).findByDepartmentId(11L);
    }

    @Test
    void getAll() {
        given(departmentRepository.findAll()).willReturn(departmentFlux);
        Flux<Department> departmentsFound=departmentService.getAll();
        StepVerifier.create(departmentsFound)
                .expectNext(department1)
                .expectNext(department2)
                .expectNext(department3)
                .verifyComplete();
        verify(departmentRepository).findAll();
    }

    @Test
    void add() {
        given(departmentRepository.saveAll(departmentMono)).willReturn(departmentMono.flux());
        given(departmentRepository.findByDepartmentId(department1.getDepartmentId())).willReturn(departmentMono);
        Mono<Department> departmentFound=departmentService.add(departmentMono);
        StepVerifier.create(departmentFound).expectNext(department1);

        verify(departmentRepository).saveAll(departmentMono);
    }

    @Test
    void getByHospitalId() {
        given(departmentRepository.findByHospitalId(1L)).willReturn(departmentFlux);
        Flux<Department> departmentsFound=departmentService.getByHospitalId(1L);
        StepVerifier.create(departmentsFound)
                .expectNext(department1)
                .expectNext(department2)
                .expectNext(department3)
                .verifyComplete();

        verify(departmentRepository).findByHospitalId(1L);
    }

    @Test
    void getByHospitalWithPatients() {
        given(departmentRepository.findByHospitalId(1L)).willReturn(departmentFlux);
        Flux<Department> departmentsFound=departmentService.getByHospitalId(1L);

        StepVerifier.create(departmentsFound)
                .assertNext(department1 -> department1.getPatientList().get(0).getFirstname().equals(patient1.getFirstname()))
                .assertNext(department2 -> department2.getDepartmentId().equals(department2.getDepartmentId()))
                .expectNext(department3)
                .verifyComplete();

        verify(departmentRepository).findByHospitalId(1L);


    }

}