package com.paro.patientservice.service;

import com.paro.patientservice.model.Patient;
import com.paro.patientservice.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class PatientServiceTest {

    @Mock
    PatientRepository patientRepository;

    @InjectMocks
    PatientService patientService;

    private Patient patient1;
    private Patient patient2;
    private Patient patient3;
    private Patient patient4;

    private Mono<Patient> patientMono;
    private Flux<Patient> patientFlux;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        patient1=new Patient(1L, "John", "Grisham", 1L, 11L);
        patient2=new Patient(2L, "Mary", "Adams", 1L, 11L);
        patient3=new Patient(3L, "Adam", "Anniston", 1L, 11L);
        patient4=new Patient(4L, "Brad", "Richards", 1L, 11L);
        patientMono=Mono.just(patient1);
        patientFlux=Flux.just(patient1, patient2, patient3, patient4);

    }


    @Test
    void getAll() {
        given(patientRepository.findAll()).willReturn(patientFlux);
        Flux<Patient> patientsFound=patientService.getAll();
        StepVerifier.create(patientsFound)
                .expectNext(patient1)
                .expectNext(patient2)
                .expectNext(patient3)
                .expectNext(patient4)
                .verifyComplete();
        verify(patientRepository).findAll();

    }

    @Test
    void getByPatientId() {
        given(patientRepository.findByPatientId(1L)).willReturn(patientMono);
        Mono<Patient> patientFound=patientService.getByPatientId(1L);
        StepVerifier.create(patientFound).consumeNextWith(patient -> {
            assertThat(patient.getPatientId()).isEqualTo(patient1.getPatientId());
        }).verifyComplete();
        verify(patientRepository).findByPatientId(1L);
    }
    //1) Testing of Receiving and saving Patient object
/*
    @Test
    void add() {
        given(patientRepository.save(any(Patient.class))).willReturn(patientMono);
        given(patientRepository.findByPatientId(patient1.getPatientId())).willReturn(patientMono);
        Mono<Patient> patientFound=patientService.add(patient1);
        StepVerifier.create(patientFound).expectNext(patient1);

        verify(patientRepository).save(any(Patient.class));
    }
*/
    //2) Testing of Receiving and saving Mono<Patient> object
    @Test
    void add() {
        given(patientRepository.saveAll(patientMono)).willReturn(patientMono.flux());
        given(patientRepository.findByPatientId(patient1.getPatientId())).willReturn(patientMono);
        Mono<Patient> patientFound=patientService.add(patientMono);
        StepVerifier.create(patientFound).expectNext(patient1);

        verify(patientRepository).saveAll(patientMono);
    }

    @Test
    void getByDepartmentId() {
        given(patientRepository.findByDepartmentId(23L)).willReturn(patientFlux);
        Flux<Patient> patientsFound=patientService.getByDepartmentId(23L);
        StepVerifier.create(patientsFound)
                .expectNext(patient1)
                .expectNext(patient2)
                .expectNext(patient3)
                .expectNext(patient4)
                .verifyComplete();

        verify(patientRepository).findByDepartmentId(23L);
    }

    @Test
    void getByHospitalId() {
        given(patientRepository.findByHospitalId(2L)).willReturn(patientFlux);
        Flux<Patient> patientsFound=patientService.getByHospitalId(2L);
        StepVerifier.create(patientsFound)
                .expectNext(patient1)
                .expectNext(patient2)
                .expectNext(patient3)
                .expectNext(patient4)
                .verifyComplete();

        verify(patientRepository).findByHospitalId(2L);
    }
}