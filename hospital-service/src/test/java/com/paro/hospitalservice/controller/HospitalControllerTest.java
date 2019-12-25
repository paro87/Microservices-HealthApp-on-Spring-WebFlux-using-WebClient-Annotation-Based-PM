package com.paro.hospitalservice.controller;

import com.paro.hospitalservice.model.Department;
import com.paro.hospitalservice.model.Hospital;
import com.paro.hospitalservice.model.Patient;
import com.paro.hospitalservice.service.HospitalService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class HospitalControllerTest {

    @MockBean
    private HospitalService hospitalService;

    private WebTestClient testClient;

    private Patient patient1;
    private Patient patient2;
    private Patient patient3;

    private Department department1;
    private Department department2;
    private Department department3;

    private Hospital hospital1;
    private Hospital hospital2;
    private Hospital hospital3;

    private Mono<Patient> patientMono;
    private Flux<Patient> patientFlux;

    private Mono<Department> departmentMono;
    private Flux<Department> departmentFlux;

    private Mono<Hospital> hospitalMono;
    private Flux<Hospital> hospitalFlux;

    @BeforeEach
    void setUp() {
        patient1=new Patient(1L, "John", "Grisham", 1L, 11L);
        patient2=new Patient(2L, "Mary", "Adams", 1L, 11L);
        patient3=new Patient(3L, "Adam", "Anniston", 1L, 11L);

        List<Patient> patientList = new ArrayList<>();
        patientList.add(patient1);
        patientList.add(patient2);
        patientList.add(patient3);

        patientMono=Mono.just(patient1);
        patientFlux=Flux.just(patient1, patient2, patient3);

        department1=new Department(11L, "Cardiology", 1L);
        department2=new Department(12L, "Neurology", 1L);
        department3=new Department(13L, "Oncology", 1L);

        List<Department> departmentList = new ArrayList<>();
        departmentList.add(department1);
        departmentList.add(department2);
        departmentList.add(department3);

        departmentMono=Mono.just(department1);
        departmentFlux=Flux.just(department1, department2, department3);

        hospital1=new Hospital(1L, "Mayo Clinic", "Rochester");
        hospital2=new Hospital(2L, "Massachusetts", "Boston");
        hospital3=new Hospital(3L, "Johns Hopkins", "Baltimore");

        List<Hospital> hospitalList = new ArrayList<>();
        hospitalList.add(hospital1);
        hospitalList.add(hospital2);
        hospitalList.add(hospital3);
        hospital1.setPatientList(null);
        hospital1.setDepartmentList(null);

        hospitalMono=Mono.just(hospital1);
        hospitalFlux=Flux.just(hospital1, hospital2, hospital3);

        hospitalService= Mockito.mock(HospitalService.class);
        testClient= WebTestClient.bindToController(new HospitalController(hospitalService)).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAll() {
        given(hospitalService.getAll()).willReturn(hospitalFlux);
        testClient.get().uri("/")
                .exchange()             //submits the request, which will be handled by the controller that WebTestClient is bound to the PatientController
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$[0].id").isEqualTo(hospital1.getId())
                .jsonPath("$[0].hospitalId").isEqualTo(hospital1.getHospitalId())
                .jsonPath("$[1].name").isEqualTo(hospital2.getName())
                .jsonPath("$[1].id").isEqualTo(hospital2.getId())
                .jsonPath("$[2].name").isEqualTo(hospital3.getName())
                .jsonPath("$[2].address").isEqualTo(hospital3.getAddress());
        verify(hospitalService).getAll();
    }

    @Test
    void getById() {
        given(hospitalService.getByHospitalId(1L)).willReturn(hospitalMono);
        testClient.get().uri("/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(hospital1.getId())
                .jsonPath("$.hospitalId").isEqualTo(hospital1.getHospitalId());
        verify(hospitalService).getByHospitalId(1L);
    }

    @Test
    void add() {
        given(hospitalService.add(any())).willReturn(hospitalMono);


        //String hospitalInJSON="{\"id\":null,\"hospitalId\":1,\"name\": \"Mayo Clinic\",\"address\": \"Rochester\", \"departmentList\":[],\"patientList\":[]}";
        String hospitalInJSON="{\"id\":null,\"hospitalId\":1,\"name\": \"Mayo Clinic\",\"address\": \"Rochester\", \"departmentList\":null,\"patientList\":null}";
        testClient.post().uri("/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(hospitalMono, Hospital.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(hospitalInJSON);
        verify(hospitalService).add(any());
    }

    @Test
    void getHospitalWithDepartments() {
    }

    @Test
    void getHospitalWithDepartmentsAndPatients() {
    }

    @Test
    void getHospitalWithPatients() {
    }
}