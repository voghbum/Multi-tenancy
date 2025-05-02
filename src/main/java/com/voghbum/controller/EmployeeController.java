package com.voghbum.controller;

import com.voghbum.db.entity.Employee;
import com.voghbum.db.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping(path = "/create")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeRepository.save(employee));
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<Employee>> findAllEmployees() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

}
