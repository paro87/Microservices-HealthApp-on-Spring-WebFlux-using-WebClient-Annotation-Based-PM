package com.paro.patientservice.controller;

import com.paro.patientservice.model.Patient;
import com.paro.patientservice.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PatientService patientService;

    private Patient patient1;
    private Patient patient2;
    private Patient patient3;

    private Mono<Patient> patientMono;
    private Flux<Patient> patientFlux;

    private WebTestClient testClient;

    @BeforeEach
    void setUp() {
        patient1=new Patient(1L, "John", "Grisham", 1L, 11L);
        patient2=new Patient(2L, "Mary", "Adams", 1L, 11L);
        patient3=new Patient(3L, "Adam", "Anniston", 1L, 11L);

        patientMono=Mono.just(patient1);
        patientFlux=Flux.just(patient1, patient2, patient3);

        patientService= Mockito.mock(PatientService.class);
        testClient=WebTestClient.bindToController(new PatientController(patientService)).build();
    }



    @Test
    void getAll() {
        given(patientService.getAll()).willReturn(patientFlux);
        testClient.get().uri("/")
                .exchange()             //submits the request, which will be handled by the controller that WebTestClient is bound to the PatientController
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$[0].id").isEqualTo(patient1.getId())
                .jsonPath("$[0].patientId").isEqualTo(patient1.getPatientId())
                .jsonPath("$[1].firstname").isEqualTo(patient2.getFirstname())
                .jsonPath("$[1].surname").isEqualTo(patient2.getSurname())
                .jsonPath("$[2].departmentId").isEqualTo(patient3.getDepartmentId())
                .jsonPath("$[2].hospitalId").isEqualTo(patient3.getHospitalId());
        verify(patientService).getAll();
    }

    @Test
    void getById() {
        given(patientService.getByPatientId(1L)).willReturn(patientMono);
        testClient.get().uri("/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(patient1.getId())
                .jsonPath("$.patientId").isEqualTo(patient1.getPatientId());
        verify(patientService).getByPatientId(1L);
    }

    @Test
    void add() {

        given(patientService.add(any())).willReturn(patientMono);

        String patientInJSON="{\"id\":null,\"patientId\":1,\"firstname\":\"John\",\"surname\":\"Grisham\",\"hospitalId\":1,\"departmentId\":11}";

        //2-Testing of "Receiving and saving of Mono<Patient> object"
        testClient.post().uri("/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(patientMono, Patient.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .isEqualTo(patientInJSON);


        //1-Testing of "Receiving and saving of Patient object"
/*        testClient.post().uri("/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(patientMono, Patient.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Patient.class)      //Expected body passes the test
                .isEqualTo(patient1);*/         //Expected and received values are same but the test fails?! Should return and research this!
        verify(patientService).add(any());
    }

    @Test
    void getByDepartment() {
        given(patientService.getByDepartmentId(11L)).willReturn(patientFlux);
        testClient.get().uri("/department/11")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$[0].id").isEqualTo(patient1.getId())
                .jsonPath("$[1].patientId").isEqualTo(patient2.getPatientId());
        verify(patientService).getByDepartmentId(11L);
    }

    @Test
    void getByHospital() {
        given(patientService.getByHospitalId(1L)).willReturn(patientFlux);
        testClient.get().uri("/hospital/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$[0].id").isEqualTo(patient1.getId())
                .jsonPath("$[1].patientId").isEqualTo(patient2.getPatientId());
        verify(patientService).getByHospitalId(1L);
    }
}