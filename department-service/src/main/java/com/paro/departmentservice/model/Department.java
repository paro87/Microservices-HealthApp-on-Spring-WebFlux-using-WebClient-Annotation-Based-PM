package com.paro.departmentservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Flux;

import java.util.List;

@NoArgsConstructor
@ToString
@Getter
@Setter
@Document
public class Department {
    @Id
    private String id;
    private Long departmentId;
    private String name;
    private Long hospitalId;
    @Transient
    private List<Patient> patientList;

    public Department(Long departmentId, String name, Long hospitalId) {
        this.departmentId = departmentId;
        this.name = name;
        this.hospitalId = hospitalId;
    }

}
