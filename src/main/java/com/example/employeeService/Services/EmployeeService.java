package com.example.employeeService.Services;

import com.example.employeeService.Models.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.UUID;

@Service
public class EmployeeService {

    private MongoTemplate mongoTemplate;

    private EmployeeService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public ResponseEntity<String> addEmployee(Employee employee) {
        if (employee.getEmployeeName() == null || employee.getEmployeeName().isEmpty() == true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Name not found");
        } else if (employee.getPhoneNumber() == null || employee.getPhoneNumber().isEmpty() == true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phone Number not found");
        } else if (employee.getEmail() == null || employee.getEmail().isEmpty() == true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email not found");
        } else if (employee.getProfileImage() == null || employee.getProfileImage().isEmpty() == true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Profile Photo not found");
        }

        String employeeId = UUID.randomUUID().toString();
        employee.setUUID(employeeId);
        mongoTemplate.save(employee);
        String response = "{\"message\":\"Employee Added Successfully\", \"UUID\":\"" + employee.getUUID() + "\"}";
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}