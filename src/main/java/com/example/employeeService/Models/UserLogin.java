package com.example.employeeService.Models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("UserLogin")
public class UserLogin {
    public String name;
    public String userName;
    public String password;
    public String company;
    public List<LeaveRequest> leaveRequests;
}
