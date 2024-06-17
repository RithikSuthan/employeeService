package com.example.employeeService.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;

public class WebController {
    @RequestMapping(value = "/{path:[^\\.]*}")
    public String redirect() {
        // Forward to index.html so that Angular routing can handle the route
        return "forward:/index.html";
    }
}
