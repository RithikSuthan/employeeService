package com.example.employeeService.Models;

import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Employee {

    @JsonProperty("employeeName")
    public String employeeName;
    @JsonProperty("phoneNumber")
    public String phoneNumber;
    @JsonProperty("email")
    public String email;
    @JsonProperty("reportsTo")
    public String reportsTo;
    @JsonProperty("profileImage")
    public String profileImage;
    @JsonProperty("uuid")
    public String uuid;

}
