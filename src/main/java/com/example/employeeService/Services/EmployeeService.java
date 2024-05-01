package com.example.employeeService.Services;

import com.example.employeeService.Models.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class EmployeeService {
    public ResponseEntity<String> addEmployee(Employee employee) {
        System.out.println(employee);
        return ResponseEntity.ok("Hello World");
    }
}
