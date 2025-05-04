package com.voghbum.db.tenant.repository;

import com.voghbum.db.tenant.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
