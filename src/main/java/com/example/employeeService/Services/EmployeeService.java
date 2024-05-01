package com.example.employeeService.Services;

import com.example.employeeService.Models.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultLifecycleProcessor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.data.mongodb.core.query.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        employee.setUuid(employeeId);
        mongoTemplate.save(employee);
        String response = "{\"message\":\"Employee Added Successfully\", \"UUID\":\"" + employee.getUuid() + "\"}";
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<?> getEmployees()
    {
        List<Employee> employees=new ArrayList<>();
        employees=mongoTemplate.findAll(Employee.class);
        return ResponseEntity.status(HttpStatus.FOUND).body(employees);

    }

    public ResponseEntity<?> deleteEmployee(String uuid) {
        List<Employee> existingEmployee = new ArrayList<>();
        Query query=new Query(Criteria.where("uuid").is(uuid));
        existingEmployee=mongoTemplate.find(query,Employee.class);
        String message="";
        if (existingEmployee.isEmpty())
        {
            message="User not found";
        }
        else
        {
            message="User Deleted Successfully";
            mongoTemplate.remove(query,Employee.class);
        }
        String response ="{\"message\":\""+message+"\"}";
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public  ResponseEntity<?> updateEmployee(@RequestBody(required = false) Employee employee)
    {
        List<Employee> existingEmployee=new ArrayList<>();
        Query query=new Query(Criteria.where("uuid").is(employee.uuid));
        existingEmployee=mongoTemplate.find(query,Employee.class);
        String message="";
        if (existingEmployee.isEmpty())
        {
            message="User not found";
        }
        else
        {
            message="User found";
            if(employee.getEmployeeName()!=null)
            {
                mongoTemplate.findAndModify(query,new Update().set("employeeName",employee.getEmployeeName()),Employee.class);
            }
            if (employee.getProfileImage()!=null)
            {
                mongoTemplate.findAndModify(query,new Update().set("profileImage",employee.getProfileImage()),Employee.class);
            }
            if(employee.getReportsTo()!=null)
            {
                mongoTemplate.findAndModify(query,new Update().set("reportsTo",employee.getReportsTo()),Employee.class);
            }
            if (employee.getEmail()!=null)
            {
                mongoTemplate.findAndModify(query,new Update().set("email",employee.getEmail()),Employee.class);
            }
            if(employee.getPhoneNumber()!=null)
            {
                mongoTemplate.findAndModify(query,new Update().set("phoneNumber",employee.getPhoneNumber()),Employee.class);
            }
        }
        String response="{\"message\":\""+message+"\"}";
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}