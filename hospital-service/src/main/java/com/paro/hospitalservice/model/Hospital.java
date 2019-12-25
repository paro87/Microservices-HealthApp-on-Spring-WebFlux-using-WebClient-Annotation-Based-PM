package com.paro.hospitalservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@ToString
@Getter
@Setter
@Document
public class Hospital {
    @Id
    private String id;
    private Long hospitalId;
    private String name;
    private String address;
    @Transient
    private List<Department> departmentList=new ArrayList<>();
    @Transient
    private List<Patient> patientList=new ArrayList<>();

    public Hospital(Long hospitalId, String name, String address) {
        this.hospitalId = hospitalId;
        this.name = name;
        this.address = address;
    }
}
