package com.example.employeeService.Models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("UserLogin")
public class UserLogin {
    public String name;
    public String userName;
    public String password;
    public String company;
}
