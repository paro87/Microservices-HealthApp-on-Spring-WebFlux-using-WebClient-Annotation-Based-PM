package com.paro.departmentservice.controller;
import com.paro.departmentservice.model.Department;
import com.paro.departmentservice.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
//@RequestMapping("/v1/")     //For Swagger UI: http://localhost:8091/swagger-ui.html
public class DepartmentController {
    private DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService){
        this.departmentService=departmentService;
    }

    @GetMapping(value = "/")
    public Flux<Department> getAll() {
        Flux<Department> departmentList=departmentService.getAll();
        return departmentList;
    }

    @GetMapping(value = "/{id}")
    public Mono<Department> getByDepartmentId(@PathVariable("id") Long departmentId){
        Mono<Department> department=departmentService.getByDepartmentId(departmentId);
        return department;
    }
    @PostMapping(value = "/")
    public Mono<Department> add(@RequestBody Mono<Department> department){
        Mono<Department> departmentAdded=departmentService.add(department);
        return departmentAdded;
    }
    @GetMapping(value = "/hospital/{hospitalId}")
    public Flux<Department> getByHospitalId(@PathVariable("hospitalId") Long hospitalId){
        Flux<Department> departmentList=departmentService.getByHospitalId(hospitalId);
        return departmentList;
    }
    @GetMapping(value = "/hospital/{hospitalId}/with-patients")
    public Flux<Department> getByHospitalWithPatients(@PathVariable("hospitalId") Long hospitalId){
        Flux<Department> departmentList=departmentService.getByHospitalWithPatients(hospitalId);
        departmentList.subscribe(d-> System.out.println("In controller: "+d.toString()));
        return departmentList;
    }
}
