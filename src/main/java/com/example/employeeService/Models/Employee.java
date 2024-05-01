package com.example.employeeService.Models;

import lombok.Data;

@Data
public class Employee {

    public String employeeName;
    public String phoneNumber;
    public String email;
    public String reportsTo;
    public String profileImage;
    public String uuid;

}
