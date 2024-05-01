package com.example.employeeService.Controllers;

import com.example.employeeService.Models.Employee;
import com.example.employeeService.Services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;
    @PostMapping("/add")
    public ResponseEntity<String> addEmployee(@RequestBody (required = true)Employee employee) {
        return employeeService.addEmployee(employee);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllEmployee()
    {
        return employeeService.getEmployees();
    }
}
