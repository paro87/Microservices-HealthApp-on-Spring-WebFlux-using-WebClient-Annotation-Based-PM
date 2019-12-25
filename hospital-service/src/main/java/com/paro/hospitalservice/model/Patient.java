package com.paro.hospitalservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor

@Getter
@Setter
@ToString
public class Patient {
    public Patient(Long patientId, String firstname, String surname, Long hospitalId, Long departmentId) {
        this.patientId = patientId;
        this.firstname = firstname;
        this.surname = surname;
        this.hospitalId = hospitalId;
        this.departmentId = departmentId;
    }

    private String id;
    private Long patientId;
    private String firstname;
    private String surname;
    private Long hospitalId;
    private Long departmentId;


}
