package com.example.employeeService.Controllers;

import com.example.employeeService.Models.Employee;
import com.example.employeeService.Models.LeaveRequest;
import com.example.employeeService.Models.Task;
import com.example.employeeService.Models.UserLogin;
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
    @GetMapping("/fetch")
    public ResponseEntity<?> fetchEmployee(@RequestParam String company,@RequestParam String creator)
    {
        return employeeService.getAllEmployees(company,creator);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteEmployee(@RequestParam String uuid)
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLogin user)
    {
        return employeeService.login(user);
    }

    @GetMapping("/fetchManagers")
    public ResponseEntity<?> fetchManagers(@RequestParam String company,@RequestParam String creator)
    {
        return employeeService.fetchManagers(company,creator);
    }

    @GetMapping("/find")
    public ResponseEntity<?> findEmployee(@RequestParam String uuid)
    {
        return employeeService.findEmployee(uuid);
    }

    @PutMapping("/register")
    public  ResponseEntity<?> registerEmployee(@RequestBody UserLogin user)
    {
        return employeeService.registerEmployee(user);
    }
    @GetMapping("/existemail")
    public ResponseEntity<?> checkExistEmail(@RequestParam String email)
    {
        return employeeService.checkExistEmail(email);
    }

    @GetMapping("/existemployee")
    public ResponseEntity<?> checkExistEmployee(@RequestParam String email)
    {
        return employeeService.checkExistEmployee(email);
    }
    @PostMapping("/leave")
    public ResponseEntity<?> leaveRequest(@RequestBody LeaveRequest leave)
    {
        return  employeeService.leaveRequest(leave);
    }

    @PostMapping("/addTask")
    public ResponseEntity<?> addTask(@RequestBody Task task)
    {
        return employeeService.addTask(task);
    }

    @GetMapping("/fetchTask")
    public ResponseEntity<?> fetchTask(@RequestParam String uuid)
    {
        return employeeService.fetchTask(uuid);
    }

    @GetMapping("/updateStatus")
    public ResponseEntity<?> updateStatus(@RequestParam String uuid,@RequestParam String taskId,@RequestParam String status )
    {
        return employeeService.updateStatus(uuid,taskId,status);
    }
    @GetMapping("/updateWorkStatus")
    public ResponseEntity<?> updateWorkStatus(@RequestParam String uuid,@RequestParam String taskId,@RequestParam String workStatus )
    {
        return employeeService.updateWorkStatus(uuid,taskId,workStatus);
    }

    @DeleteMapping("/deleteTask")
    public ResponseEntity<?> deleteTask(@RequestParam String uuid,@RequestParam String taskId)
    {
        return employeeService.deleteTask(uuid,taskId);
    }

    @GetMapping("/managerEmployee")
    public ResponseEntity<?> managerEmployee(@RequestParam String uuid)
    {
        return employeeService.managerEmployee(uuid);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody UserLogin user)
    {
        return  employeeService.changePassword(user);
    }
}
