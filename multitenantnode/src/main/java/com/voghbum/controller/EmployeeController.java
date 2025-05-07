package com.voghbum.controller;

import com.voghbum.db.tenant.entity.Employee;
import com.voghbum.db.tenant.repository.EmployeeRepository;
import com.voghbum.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.voghbum.dto.EmployeeResponseDto;
import com.voghbum.db.master.repository.TenantRepository;
import com.voghbum.db.master.entity.Tenant;
import com.voghbum.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import com.voghbum.app.TenantContext;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeRepository employeeRepository, EmployeeService employeeService) {
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
    }

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

    @GetMapping(path = "/get")
    public ResponseEntity<EmployeeResponseDto> getEmployee(@RequestParam String employeeName) {
        return employeeService.prepareEmployee(employeeName);
    }

}
