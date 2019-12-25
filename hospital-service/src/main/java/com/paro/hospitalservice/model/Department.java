package com.paro.hospitalservice.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor

@Getter
@Setter
@ToString

public class Department {
    private String id;
    private Long departmentId;
    private String name;
    private Long hospitalId;
    private List<Patient> patientList=new ArrayList<>();

    public Department(Long departmentId, String name, Long hospitalId) {
        this.departmentId = departmentId;
        this.name = name;
        this.hospitalId = hospitalId;
    }
}
