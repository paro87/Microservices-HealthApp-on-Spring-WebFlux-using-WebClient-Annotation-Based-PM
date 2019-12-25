package com.paro.departmentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paro.departmentservice.model.Department;
import com.paro.departmentservice.model.Patient;
import com.paro.departmentservice.service.DepartmentService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class DepartmentControllerTest {
    //For testing
    public static MockWebServer mockWebServer;
    private ObjectMapper MAPPER = new ObjectMapper();

    @MockBean
    private DepartmentService departmentService;

    private WebTestClient testClient;

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

    @BeforeEach
    void setUp() {
        //        For testing
//        mockWebServer=new MockWebServer();
//        mockWebServer.start();
//        String baseUrl=String.format("http://localhost:%s", mockWebServer.getPort());
//        departmentService=new DepartmentService(WebClient.create(mockWebServer.url("/").toString()));

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


        departmentMono=Mono.just(department1);
        departmentFlux=Flux.just(department1, department2, department3);

        departmentService= Mockito.mock(DepartmentService.class);
        testClient= WebTestClient.bindToController(new DepartmentController(departmentService)).build();
    }

    @AfterEach
    void tearDown() throws IOException {
        //mockWebServer.shutdown();
    }

    @Test
    void getAll() {
        given(departmentService.getAll()).willReturn(departmentFlux);
        testClient.get().uri("/")
                .exchange()             //submits the request, which will be handled by the controller that WebTestClient is bound to the PatientController
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$[0].id").isEqualTo(department1.getId())
                .jsonPath("$[0].departmentId").isEqualTo(department1.getDepartmentId())
                .jsonPath("$[1].name").isEqualTo(department2.getName())
                .jsonPath("$[1].id").isEqualTo(department2.getId())
                .jsonPath("$[2].departmentId").isEqualTo(department3.getDepartmentId())
                .jsonPath("$[2].hospitalId").isEqualTo(department3.getHospitalId());
        verify(departmentService).getAll();
    }

    @Test
    void getById() {
        given(departmentService.getByDepartmentId(11L)).willReturn(departmentMono);
        testClient.get().uri("/11")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(department1.getId())
                .jsonPath("$.departmentId").isEqualTo(department1.getDepartmentId());
        verify(departmentService).getByDepartmentId(11L);
    }

    @Test
    void add() {
        given(departmentService.add(any())).willReturn(departmentMono);

        String departmentInJSON="{\"id\":null,\"departmentId\":11,\"name\":\"Cardiology\",\"hospitalId\":1,\"patientList\":null}";

        testClient.post().uri("/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(departmentMono, Department.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(departmentInJSON);
        verify(departmentService).add(any());
    }

    @Test
    void getByHospital() {
        given(departmentService.getByHospitalId(1L)).willReturn(departmentFlux);
        testClient.get().uri("/hospital/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$[0].id").isEqualTo(department1.getId())
                .jsonPath("$[1].departmentId").isEqualTo(department2.getDepartmentId());
        verify(departmentService).getByHospitalId(1L);
    }

    @Test
    void getByHospitalWithPatients() {
        //For testing
/*        mockWebServer.enqueue(new MockResponse().setBody(MAPPER.writeValueAsString(departmentFlux)));
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        //.setBody(MAPPER.writeValueAsString(departmentFlux))
                        .setBody("{\"y\": \"value for y\", \"z\": 789}"));*/
    }
}