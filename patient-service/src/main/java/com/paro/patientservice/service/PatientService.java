package com.paro.patientservice.service;

import com.paro.patientservice.model.Patient;
import com.paro.patientservice.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PatientService {


    private PatientRepository patientRepository;
    @Autowired
    public PatientService(PatientRepository patientRepository){
        this.patientRepository=patientRepository;
    }

    public Flux<Patient> getAll() {
        return patientRepository.findAll();
    }

    public Mono<Patient> getByPatientId(Long patientId) {
        return patientRepository.findByPatientId(patientId);
    }
    //1) Receiving and saving Patient object
/*        public Mono<Patient> add(Patient patient) {
        Mono<Patient> patientAdded= patientRepository.save(patient);
        return patientAdded;
    }*/

    //2) Saving Mono<Patient>
    public Mono<Patient> add(Mono<Patient> patient) {
        Mono<Patient> patientAdded= patientRepository.saveAll(patient).next();
        return patientAdded;
    }

    public Flux<Patient> getByDepartmentId(Long departmentId) {
        return patientRepository.findByDepartmentId(departmentId);
    }

    public Flux<Patient> getByHospitalId(Long hospitalId) {
        return patientRepository.findByHospitalId(hospitalId);
    }
}
