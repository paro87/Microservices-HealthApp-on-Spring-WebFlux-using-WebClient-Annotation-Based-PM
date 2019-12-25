package com.paro.hospitalservice.service;

import com.paro.hospitalservice.model.Department;
import com.paro.hospitalservice.model.Hospital;
import com.paro.hospitalservice.model.Patient;
import com.paro.hospitalservice.repository.HospitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class HospitalServiceTest {

    @Mock
    HospitalRepository hospitalRepository;

    @InjectMocks
    HospitalService hospitalService;

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

    private List<Department> departmentList;
    private List<Patient> patientList;
    private List<Hospital> hospitalList;

    @BeforeEach
    void setUp() {
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

        hospital1=new Hospital(1L, "Mayo Clinic", "Rochester");
        hospital2=new Hospital(2L, "Massachusetts", "Boston");
        hospital3=new Hospital(3L, "Johns Hopkins", "Baltimore");

        hospitalList= new ArrayList<>();
        hospitalList.add(hospital1);
        hospitalList.add(hospital2);
        hospitalList.add(hospital3);
        hospital1.setDepartmentList(departmentList);
        hospital1.setPatientList(patientList);
        hospitalMono=Mono.just(hospital1);
        hospitalFlux=Flux.just(hospital1, hospital2, hospital3);
    }

    @Test
    void getAll() {
        given(hospitalRepository.findAll()).willReturn(hospitalFlux);
        Flux<Hospital> hospitalFound=hospitalService.getAll();
        StepVerifier.create(hospitalFound)
                .expectNext(hospital1)
                .expectNext(hospital2)
                .expectNext(hospital3)
                .verifyComplete();
        verify(hospitalRepository).findAll();
    }

    @Test
    void getByHospitalId() {
        given(hospitalRepository.findByHospitalId(1L)).willReturn(hospitalMono);
        Mono<Hospital> hospitalFound=hospitalService.getByHospitalId(1L);
        StepVerifier.create(hospitalFound).consumeNextWith(hospital -> {
            assertThat(hospital.getHospitalId()).isEqualTo(hospital1.getHospitalId());
        }).verifyComplete();
        verify(hospitalRepository).findByHospitalId(1L);
    }

    @Test
    void add() {
        given(hospitalRepository.saveAll(hospitalMono)).willReturn(hospitalMono.flux());
        given(hospitalRepository.findByHospitalId(hospital1.getHospitalId())).willReturn(hospitalMono);
        Mono<Hospital> hospitalFound=hospitalService.add(hospitalMono);
        StepVerifier.create(hospitalFound).expectNext(hospital1);

        verify(hospitalRepository).saveAll(hospitalMono);
    }

    @Test
    void getHospitalWithDepartments() {
        given(hospitalRepository.findByHospitalId(1L)).willReturn(hospitalMono);
        Mono<Hospital> hospitalFound=hospitalService.getByHospitalId(1L);

        StepVerifier.create(hospitalFound)
                .assertNext(hospital -> hospital.getDepartmentList().get(0).getName().equals(department1.getName()))
                .verifyComplete();

        verify(hospitalRepository).findByHospitalId(1L);

    }

    @Test
    void getHospitalWithDepartmentsAndPatients() {
        given(hospitalRepository.findByHospitalId(1L)).willReturn(hospitalMono);
        Mono<Hospital> hospitalFound=hospitalService.getByHospitalId(1L);
        StepVerifier.create(hospitalFound)
                .thenConsumeWhile(hospital -> {
                    assertThat(hospital).isEqualTo(hospital1);
                    assertThat(hospital.getDepartmentList().get(0).getName()).isEqualTo(department1.getName());
                    assertThat(hospital.getPatientList().get(0).getFirstname()).isEqualTo(patient1.getFirstname());
                    return true;
                })
                .verifyComplete();

        verify(hospitalRepository).findByHospitalId(1L);
    }

    @Test
    void getHospitalWithPatients() {
        given(hospitalRepository.findByHospitalId(1L)).willReturn(hospitalMono);
        Mono<Hospital> hospitalFound=hospitalService.getByHospitalId(1L);

        StepVerifier.create(hospitalFound)
                .assertNext(hospital -> hospital.getPatientList().get(0).getFirstname().equals(patient1.getFirstname()))
                .verifyComplete();

        verify(hospitalRepository).findByHospitalId(1L);
    }
}