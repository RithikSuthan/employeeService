package com.example.employeeService.Models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("LeaveRequest")
public class LeaveRequest {
    private String comments;
    private  String email;
    private String fromDate;
    private String fromTime;
    private String leaveType;
    private String toDate;
    private String toTime;
}
