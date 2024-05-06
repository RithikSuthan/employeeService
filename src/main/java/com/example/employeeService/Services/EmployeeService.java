package com.example.employeeService.Services;

import com.example.employeeService.Models.EmailRequest;
import com.example.employeeService.Models.Employee;
import com.example.employeeService.Models.UserLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultLifecycleProcessor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;

import java.util.*;

@Service
public class EmployeeService {

    private MongoTemplate mongoTemplate;

    private EmployeeService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public ResponseEntity<String> addEmployee(Employee employee) {
        if (employee.getEmployeeName() == null || employee.getEmployeeName().isEmpty() == true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Name not found");
        } else if (employee.getPhoneNumber() == null || employee.getPhoneNumber().isEmpty() == true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phone Number not found");
        } else if (employee.getEmail() == null || employee.getEmail().isEmpty() == true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email not found");
        } else if (employee.getProfileImage() == null || employee.getProfileImage().isEmpty() == true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Profile Photo not found");
        }

        String employeeId = UUID.randomUUID().toString();
        employee.setUuid(employeeId);
        Query query1=new Query(Criteria.where("uuid").is(employee.getReportsTo()));
        Employee checkManager=mongoTemplate.findOne(query1,Employee.class);
        if(checkManager!=null && checkManager.employeeName!=null)
        {
            employee.setManager(checkManager.employeeName);
        }
        mongoTemplate.save(employee);

        String managerID=employee.getReportsTo();
        if(managerID!=null)
        {
            Query query=new Query(Criteria.where("uuid").is(managerID));
            List<Employee> manager=new ArrayList<>();
            manager=mongoTemplate.find(query,Employee.class);

            if(manager!=null)
            {
                String managerEmail=manager.get(0).email;

                EmailRequest emailRequest = new EmailRequest();
                emailRequest.setName(employee.employeeName);
                emailRequest.setMail(employee.email);
                emailRequest.setMobile(employee.phoneNumber);
                emailRequest.setSubject("New employee added");
                emailRequest.setMessage(employee.employeeName+" will now work under you");
                sendEmail(emailRequest);
            }
        }

        String response = "{\"message\":\"Employee Added Successfully\", \"UUID\":\"" + employee.getUuid() + "\"}";
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public void sendEmail(EmailRequest emailRequest) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://helper-api-vignu.el.r.appspot.com/mail_merchant/sendmail/663263cb5fee3ae2701d0c97";

        try {
            restTemplate.postForObject(url, emailRequest, String.class);
            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }


    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = mongoTemplate.findAll(Employee.class);
        return ResponseEntity.status(HttpStatus.OK).body(employees);
    }


    public ResponseEntity<?> getEmployees(int pageNumber,int pageSize,String sort)
    {
        List<Employee> employees=new ArrayList<>();
        employees=mongoTemplate.findAll(Employee.class);

        List<List<Employee>> dashboard=new ArrayList<>();
        int count=0;
        int n=0;
        List<Employee> page=new ArrayList<>();
        while (n< employees.size()) {
            if (count<pageSize)
            {
                count=count+1;
                page.add(employees.get(n));
            }
            else
            {
                dashboard.add(page);
                page=new ArrayList<>();
                count=0;
                count=count+1;
                page.add(employees.get(n));
            }
            n=n+1;
        }
        dashboard.add(page);
        List<Employee> requiredPage=new ArrayList<>();
        requiredPage=dashboard.get(pageNumber-1);

        if(sort.equals("email"))
        {
            requiredPage.sort(Comparator.comparing(Employee::getEmail));
        }
        else if(sort.equals("employeeName"))
        {
            requiredPage.sort(Comparator.comparing(Employee::getEmployeeName));
        }
        return ResponseEntity.status(HttpStatus.FOUND).body(requiredPage);
    }

    public ResponseEntity<?> deleteEmployee(String uuid) {
        List<Employee> existingEmployee = new ArrayList<>();
        Query query=new Query(Criteria.where("uuid").is(uuid));
        existingEmployee=mongoTemplate.find(query,Employee.class);
        String message="";
        if (existingEmployee.isEmpty())
        {
            message="User not found";
        }
        else
        {
            message="User Deleted Successfully";
            mongoTemplate.remove(query,Employee.class);
        }
        String response ="{\"message\":\""+message+"\"}";
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public  ResponseEntity<?> updateEmployee(@RequestBody(required = false) Employee employee)
    {
        List<Employee> existingEmployee=new ArrayList<>();
        Query query=new Query(Criteria.where("uuid").is(employee.uuid));
        existingEmployee=mongoTemplate.find(query,Employee.class);
        String message="";
        if (existingEmployee.isEmpty())
        {
            message="User not found";
        }
        else
        {
            message="User found";
            if(employee.getEmployeeName()!=null)
            {
                mongoTemplate.findAndModify(query,new Update().set("employeeName",employee.getEmployeeName()),Employee.class);
            }
            if (employee.getProfileImage()!=null)
            {
                mongoTemplate.findAndModify(query,new Update().set("profileImage",employee.getProfileImage()),Employee.class);
            }
            if(employee.getReportsTo()!=null)
            {
                mongoTemplate.findAndModify(query,new Update().set("reportsTo",employee.getReportsTo()),Employee.class);
            }
            if (employee.getEmail()!=null)
            {
                mongoTemplate.findAndModify(query,new Update().set("email",employee.getEmail()),Employee.class);
            }
            if(employee.getPhoneNumber()!=null)
            {
                mongoTemplate.findAndModify(query,new Update().set("phoneNumber",employee.getPhoneNumber()),Employee.class);
            }
            if(employee.getPosition()!=null)
            {
                mongoTemplate.findAndModify(query,new Update().set("position",employee.getPosition()),Employee.class);
            }
            if(employee.getManager()!=null)
            {
                mongoTemplate.findAndModify(query,new Update().set("manager",employee.getManager()),Employee.class);
            }
        }
        String response="{\"message\":\""+message+"\"}";
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<?> employeeReportsTo(String uuid,int level)
    {
        String message="";
        while(level!=0){
            List<Employee> employee=new ArrayList<>();
            Query query=new Query(Criteria.where("uuid").is(uuid));
            employee=mongoTemplate.find(query,Employee.class);
            if(employee.isEmpty())
            {
                message="User not found";
                break;
            }
            uuid=employee.get(0).reportsTo;
            level=level-1;
        }
        List<Employee> manager=new ArrayList<>();
        if(message=="")
        {
            Query query=new Query(Criteria.where("uuid").is(uuid));
            manager=mongoTemplate.find(query,Employee.class);
            return ResponseEntity.status(HttpStatus.OK).body(manager);
        }
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    public ResponseEntity<?> login(UserLogin user)
    {
        List<UserLogin> existing=new ArrayList<>();
        List<UserLogin> existingUser=new ArrayList<>();
        Query query1=new Query(Criteria.where("userName").is(user.userName));
        Query query=new Query(Criteria.where("userName").is(user.userName).and("password").is(user.password));
        existing=mongoTemplate.find(query,UserLogin.class);
        existingUser=mongoTemplate.find(query1,UserLogin.class);
        String message="";
        if (existingUser.isEmpty())
        {
            message="User not Found";
        }
        if (!existingUser.isEmpty() && existing.isEmpty())
        {
            message="Incorrect Password";
        }
        else
        {
            message="Login Successful";
        }
//        String response= "{\"message\":"+"\""+message+"\""+"}";
        System.out.println(existingUser.get(0).getName());
        String response= "{\"message\":"+"\""+message+"\",\"name\":\""+existingUser.get(0).getName()+"\"}";
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<?> fetchManagers()
    {
        ArrayList<String> posi=new ArrayList<>();
        posi.add("Manager");
        posi.add("Team Leader");
        posi.add("CEO");
        Query query=new Query(Criteria.where("position").in(posi));

        List<Employee> mainMembers=mongoTemplate.find(query,Employee.class);
        return ResponseEntity.status(HttpStatus.OK).body(mainMembers);
    }

    public ResponseEntity<?> findEmployee(String uuid)
    {
        Query query=new Query(Criteria.where("uuid").is(uuid));
        Employee employee=mongoTemplate.findOne(query, Employee.class);
        return ResponseEntity.status(HttpStatus.OK).body(employee);
    }
}