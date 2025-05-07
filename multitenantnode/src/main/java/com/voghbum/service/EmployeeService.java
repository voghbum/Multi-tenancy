package com.voghbum.service;

import com.voghbum.app.TenantContext;
import com.voghbum.db.master.entity.Tenant;
import com.voghbum.db.master.repository.TenantRepository;
import com.voghbum.db.tenant.entity.Employee;
import com.voghbum.db.tenant.repository.EmployeeRepository;
import com.voghbum.dto.EmployeeResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class EmployeeService {
    private final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private final EmployeeRepository employeeRepository;
    private final TenantRepository tenantRepository;

    public EmployeeService(EmployeeRepository employeeRepository, TenantRepository tenantRepository) {
        this.employeeRepository = employeeRepository;
        this.tenantRepository = tenantRepository;
    }

    public ResponseEntity<EmployeeResponseDto> prepareEmployee(String employeeName) {
        String tenantId = TenantContext.getCurrentTenant();
        Optional<Tenant> tenantOpt = tenantRepository.findByTenantId(tenantId);
        String endpoint = tenantOpt.get().getSingleNodeEndpoint();
        if (endpoint == null || endpoint.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Employee employee = employeeRepository.findByName(employeeName);
        RestTemplate restTemplate = new RestTemplate();
        Integer performance;
        try {
            performance = restTemplate.postForObject(endpoint, employee, Integer.class);
        } catch (Exception e) {
            logger.error("Error calling single node endpoint: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
        EmployeeResponseDto responseDto = new EmployeeResponseDto(employee.getId(), employee.getName(), performance);
        return ResponseEntity.ok(responseDto);
    }
}
