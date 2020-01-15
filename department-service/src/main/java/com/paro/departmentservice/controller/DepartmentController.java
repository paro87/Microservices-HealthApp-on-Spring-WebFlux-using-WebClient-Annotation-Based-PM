package com.paro.departmentservice.controller;
import com.paro.departmentservice.model.Department;
import com.paro.departmentservice.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(path = "/service", produces = "application/json")   //Will only handle requests if the request’s Accept header includes “application/json”
@CrossOrigin(origins="*")                                           //Allows clients from any domain to consume the API, especially for frontend
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
    @PostMapping(value = "/", consumes = "application/json")    //Will only handle requests whose Content-type matches application/json
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<Department> add(@RequestBody Mono<Department> department){
        Mono<Department> departmentAdded=departmentService.add(department);
        return departmentAdded;
    }

    @PutMapping(value = "/{id}")
    public Mono<Department> putById(@PathVariable("id") Long departmentId, @RequestBody Mono<Department> department){
        Mono<Department> departmentPut=departmentService.put(departmentId, department);
        return departmentPut;
    }
    //1-Patch: Department object
    /*
    @ResponseStatus(HttpStatus.CREATED)
    @PatchMapping(value = "/{id}", consumes = "application/json")
    public Mono<Department> patchById(@PathVariable("id") Long departmentId, @RequestBody Department department){
        Mono<Department> departmentPatched=departmentService.patch(departmentId, department);
        departmentPatched.subscribe(System.out::println);
        return departmentPatched;
    }
    */

    //2-Patch: Mono<Department> object
    @ResponseStatus(HttpStatus.CREATED)
    @PatchMapping(value = "/{id}", consumes = "application/json")
    public Mono<Department> patchById(@PathVariable("id") Long departmentId, @RequestBody Mono<Department> department){
        Mono<Department> departmentPatched=departmentService.patch(departmentId, department);

        return departmentPatched;

    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)   // 204
    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable("id") Long departmentId) {
        departmentService.deleteById(departmentId);
    }

    @GetMapping(value = "/hospital/{hospitalId}")
    public Flux<Department> getByHospitalId(@PathVariable("hospitalId") Long hospitalId){
        Flux<Department> departmentList=departmentService.getByHospitalId(hospitalId);
        return departmentList;
    }
    @GetMapping(value = "/hospital/{hospitalId}/with-patients")
    public Flux<Department> getByHospitalWithPatients(@PathVariable("hospitalId") Long hospitalId){
        Flux<Department> departmentList=departmentService.getByHospitalWithPatients(hospitalId);
        //departmentList.subscribe(d-> System.out.println("In controller: "+d.toString()));
        return departmentList;
    }
}
