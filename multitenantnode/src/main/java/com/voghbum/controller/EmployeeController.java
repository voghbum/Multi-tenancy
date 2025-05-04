package com.voghbum.controller;

import com.voghbum.db.tenant.entity.Employee;
import com.voghbum.db.tenant.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping(path = "/create")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        logger.info("/employee requested");
        return ResponseEntity.ok(employeeRepository.save(employee));
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<Employee>> findAllEmployees() {
        logger.info("/all requested");
        return ResponseEntity.ok(employeeRepository.findAll());
    }

}
