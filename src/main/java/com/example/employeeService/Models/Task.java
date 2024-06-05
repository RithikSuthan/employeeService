package com.example.employeeService.Models;

import com.couchbase.client.core.deps.com.google.type.DateTime;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class Task {
        public String deadline;
        public String description;
        public String email;
        public String reportTo;
        public String uuid;
        public String status;
}
