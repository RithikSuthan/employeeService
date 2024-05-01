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

//    @GetMapping("/users")
//    public ResponseEntity<?> getAllEmployee()
//    {
//        return employeeService.getEmployees();
//    }
    @GetMapping("/users")
    public ResponseEntity<?> getAllEmployee(@RequestParam int pageNumber,@RequestParam int pageSize,@RequestParam String sort)
    {
        return employeeService.getEmployees(pageNumber,pageSize,sort);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteEmployee(@RequestParam(required = false)  String uuid)
    {
        return employeeService.deleteEmployee(uuid);
    }

    @PatchMapping("/update")
    public  ResponseEntity<?> updateEmployee(@RequestBody(required = false) Employee employee)
    {
        return employeeService.updateEmployee(employee);
    }

    @GetMapping("/report")
    public ResponseEntity<?> employeeReportsTo(@RequestParam(required = true) String uuid,@RequestParam(required = true) int level)
    {
        return employeeService.employeeReportsTo(uuid,level);
    }
}
