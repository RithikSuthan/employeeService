package com.example.employeeService.Models;

import lombok.Data;

@Data
public class EmailRequest {
    public String name;
    public String mail;
    public String subject;
    public String message;
    public String mobile;
}
