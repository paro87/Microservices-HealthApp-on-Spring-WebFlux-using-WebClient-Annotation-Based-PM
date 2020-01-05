package com.paro.patientservice.service;

import com.paro.patientservice.model.Patient;
import com.paro.patientservice.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PatientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientService.class);

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

    public Mono<Patient> put(Long patientId, Mono<Patient> patientToPut) {
        return patientRepository.findByPatientId(patientId)
                .flatMap(patient ->patientRepository.delete(patient))
                .then(patientRepository.saveAll(patientToPut).next());

        // Possible 2nd solution
        /*
        Mono<Patient> patientFound=patientRepository.findByPatientId(patientId);
        Mono<Patient> patientPut=patientFound.map(patientFromRepo -> {
            patientToPut.map(patientBeingPut -> {
                if (patientBeingPut.getFirstname()!=null) {
                    patientFromRepo.setFirstname(patientBeingPut.getFirstname());
                }
                if (patientBeingPut.getSurname()!=null) {
                    patientFromRepo.setSurname(patientBeingPut.getSurname());
                }
                if (patientBeingPut.getDepartmentId()!=null) {
                    patientFromRepo.setDepartmentId(patientBeingPut.getDepartmentId());
                }
                if (patientBeingPut.getHospitalId()!=null) {
                    patientFromRepo.setHospitalId(patientBeingPut.getHospitalId());
                }
                return patientFromRepo;
            }).subscribe();
            return patientFromRepo;
        });
        return patientRepository.saveAll(patientPut).next();*/
    }

    //1-Patch: Patient object
    /*
    public Mono<Patient> patch(Long patientId, Patient patientToPatch) {
        Mono<Patient> patientFound=patientRepository.findByPatientId(patientId);
        Mono<Patient> patientPatched=patientFound.map(patient -> {
            if (patientToPatch.getPatientId()!=null) {
                patient.setPatientId(patientToPatch.getPatientId());
            }
            if (patientToPatch.getFirstname()!=null) {
                patient.setFirstname(patientToPatch.getFirstname());
            }
            if (patientToPatch.getSurname()!=null) {
                patient.setSurname(patientToPatch.getSurname());
            }
            if (patientToPatch.getDepartmentId()!=null) {
                patient.setDepartmentId(patientToPatch.getDepartmentId());
            }
            if (patientToPatch.getHospitalId()!=null) {
                patient.setHospitalId(patientToPatch.getHospitalId());
            }
            return patient;
        });
        return patientRepository.saveAll(patientPatched).next();
    }
    */


    //2-Patch: Mono<Patient> object
    public Mono<Patient> patch(Long patientId, Mono<Patient> patientToPatch) {
        Mono<Patient> patientFound=patientRepository.findByPatientId(patientId);
        Mono<Patient> patientPatched=patientFound.map(patientFromRepo -> {

           patientToPatch.map(patientBeingPatched -> {

               if (patientBeingPatched.getPatientId()!=null) {
                   patientFromRepo.setPatientId(patientBeingPatched.getPatientId());
               }
               if (patientBeingPatched.getFirstname()!=null) {
                   patientFromRepo.setFirstname(patientBeingPatched.getFirstname());
               }
               if (patientBeingPatched.getSurname()!=null) {
                   patientFromRepo.setSurname(patientBeingPatched.getSurname());
               }
               if (patientBeingPatched.getDepartmentId()!=null) {
                   patientFromRepo.setDepartmentId(patientBeingPatched.getDepartmentId());
               }
               if (patientBeingPatched.getHospitalId()!=null) {
                   patientFromRepo.setHospitalId(patientBeingPatched.getHospitalId());
               }
               return patientFromRepo;
           }).subscribe();
            return patientFromRepo;
        });
        return patientRepository.saveAll(patientPatched).next();
    }

    public void deleteById(Long patientId) {
        Mono<Patient> patientToBeDeleted=patientRepository.findByPatientId(patientId);
        patientToBeDeleted.flatMap(patient ->patientRepository.delete(patient)).subscribe();
    }

    public Flux<Patient> getByDepartmentId(Long departmentId) {
        return patientRepository.findByDepartmentId(departmentId);
    }

    public Flux<Patient> getByHospitalId(Long hospitalId) {
        return patientRepository.findByHospitalId(hospitalId);
    }
}
