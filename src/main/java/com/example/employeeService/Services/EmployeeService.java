package com.example.employeeService.Services;

import com.example.employeeService.Models.*;
import org.apache.catalina.User;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        employee.setPassword(employee.getPhoneNumber());
        Query query1=new Query(Criteria.where("uuid").is(employee.getReportsTo()));
        Employee checkManager=mongoTemplate.findOne(query1,Employee.class);
        if(checkManager!=null && checkManager.employeeName!=null)
        {
            employee.setManager(checkManager.employeeName);
        }
        mongoTemplate.save(employee);

        String managerID=employee.getReportsTo();
        if(managerID!=null && managerID!="")
        {
            Query query=new Query(Criteria.where("uuid").is(managerID));
            List<Employee> manager=new ArrayList<>();
            manager=mongoTemplate.find(query,Employee.class);

            if(manager!=null && managerID!="")
            {
                String managerEmail=manager.get(0).email;

                EmailRequest emailRequest = new EmailRequest();
                emailRequest.setName(employee.employeeName);
                emailRequest.setMail(employee.email);
                emailRequest.setMobile(employee.phoneNumber);
                emailRequest.setManagerEmail(managerEmail);
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
//        String url = "https://helper-api-vignu.el.r.appspot.com/mail_merchant/sendmail/663263cb5fee3ae2701d0c97";
        String url = "https://mailboxexpress.el.r.appspot.com/reportManager";

        try {
            restTemplate.postForObject(url, emailRequest, String.class);
            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }

    public ResponseEntity<List<Employee>> getAllEmployees(String company,String creator) {
        Query query=new Query(Criteria.where("company").is(company).and("creator").is(creator));
        List<Employee> employees = mongoTemplate.find(query,Employee.class);
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
            message="Data Updated Successfully";
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
        Query query1=new Query(Criteria.where("userName").is(user.userName));
        Query query=new Query(Criteria.where("userName").is(user.userName).and("password").is(user.password));
        UserLogin existing=mongoTemplate.findOne(query,UserLogin.class);
        UserLogin existingUser=mongoTemplate.findOne(query1,UserLogin.class);
        String message="";
        String response;
        if (existing==null)
        {
            Query isEmployee=new Query(Criteria.where("email").is(user.userName).and("password").is(user.password));
            Employee existEmployee=mongoTemplate.findOne(isEmployee, Employee.class);
            if (existEmployee==null)
            {
                message="User not Found";
            }
            else
            {
                message="Employee Login Success";
            }
            response = "{\"message\":\"" + message + "\",\"name\":\"" + existEmployee.getEmployeeName() + "\"" +
                    ",\"company\":\"" + existEmployee.getCompany() + "\"" +
                    ",\"userName\":\"" + existEmployee.getEmail() + "\"" +
                    ",\"creator\":\"" + existEmployee.getCreator() + "\"" +
                    ",\"uuid\":\"" + existEmployee.getUuid() + "\"}";

        }
        else if (existingUser==null && existing==null)
        {
            message="Incorrect Password";
            response= "{\"message\":"+"\""+message+"\""+"}";
        }
        else
        {
            message="Login Successful";
            response= "{\"message\":"+"\""+message+"\",\"name\":\""+existingUser.getName()+"\"" +
                    ",\"company\":\""+existingUser.getCompany()+"\""+",\"userName\":\""+existingUser.getUserName()+"\"}";
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<?> fetchManagers(String company,String creator)
    {
        ArrayList<String> posi=new ArrayList<>();
        posi.add("Manager");
        posi.add("Team Leader");
        posi.add("CEO");
        Query query=new Query(Criteria.where("position").in(posi).and("company").is(company)
                .and("creator").is(creator));

        List<Employee> mainMembers=mongoTemplate.find(query,Employee.class);
        return ResponseEntity.status(HttpStatus.OK).body(mainMembers);
    }

    public ResponseEntity<?> findEmployee(String uuid)
    {
        Query query=new Query(Criteria.where("uuid").is(uuid));
        Employee employee=mongoTemplate.findOne(query, Employee.class);
        return ResponseEntity.status(HttpStatus.OK).body(employee);
    }

    public ResponseEntity<?> registerEmployee(UserLogin user)
    {
        String message="";
        if(user.getName()==null || user.getName()=="")
        {
            message="name is missing";
        }
        else if(user.getUserName()==null || user.getUserName()=="")
        {
            message="email id missing";
        }
        else if(user.getPassword()==null || user.getPassword()=="")
        {
            message="password missing";
        }
        else if(user.getCompany()==null || user.getCompany()=="")
        {
            message="company missing";
        }
        else
        {
            message="User saved successfully";
            mongoTemplate.save(user);
        }
        String response="{\"message\":\""+message+"\"}";
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    public ResponseEntity<?>checkExistEmail(String email)
    {
        Query query=new Query(Criteria.where("userName").is(email));
        UserLogin exist=mongoTemplate.findOne(query,UserLogin.class);
        String message;
        if(exist!=null) {
            message="This email Already exists";
        }
        else
        {
            message="New user";
        }
        String response="{\"message\":\""+message+"\"}";
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    public ResponseEntity<?>checkExistEmployee(String email)
    {
        Query query=new Query(Criteria.where("email").is(email));
        Employee exist=mongoTemplate.findOne(query,Employee.class);
        String message;
        if(exist!=null) {
            message="This email Already exists";
        }
        else
        {
            message="New user";
        }
        String response="{\"message\":\""+message+"\"}";
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    public ResponseEntity<?> leaveRequest(LeaveRequest leave)
    {
        String Message="";
        Query query=new Query(Criteria.where("email").is(leave.getEmail()));
        Employee exist=mongoTemplate.findOne(query,Employee.class);
        if(exist.getManager()!=null)
        {
            Query managerQuery=new Query(Criteria.where("employeeName").is(exist.manager));
            Employee manager=mongoTemplate.findOne(managerQuery,Employee.class);
            if(manager!=null)
            {
                if(manager.leaveRequests==null)
                {
                    manager.leaveRequests=new ArrayList<>();
                }
                manager.leaveRequests.add(leave);
            }
            mongoTemplate.findAndRemove(managerQuery, Employee.class);
            mongoTemplate.save(manager);
        }
        Query hrQuery=new Query(Criteria.where("company").is(exist.getCompany()));
        UserLogin hr=mongoTemplate.findOne(hrQuery,UserLogin.class);
        if(hr!=null)
        {
            if(hr.leaveRequests==null)
            {
                hr.leaveRequests=new ArrayList<>();
            }
            hr.leaveRequests.add(leave);
            mongoTemplate.findAndRemove(hrQuery,UserLogin.class);
            mongoTemplate.save(hr);
        }
        return  ResponseEntity.status(HttpStatus.OK).body("Request Successful");
    }

    public ResponseEntity<?> addTask(Task task)
    {
        String message="";
        Query query=new Query(Criteria.where("uuid").is(task.uuid));
        Employee exist =mongoTemplate.findOne(query,Employee.class);
        task.setStatus("assigned");
        task.setWorkStatus("Not Yet Started");
        task.setTaskId(UUID.randomUUID().toString());
        task.setDeadlineFlag(false);
        if(exist!=null)
        {
            List<Task> currentTask=exist.getTasks();
            if (currentTask ==null)
            {
                currentTask=new ArrayList<Task>();
            }
            currentTask.add(task);
            mongoTemplate.findAndModify(query,new Update().set("tasks",currentTask),Employee.class);
            message="{\"message\":\""+"Task updated Successfully"+"\"}";
        }
        else
        {
            message="{\"message\":\""+"Task updation Failed"+"\"}";
        }
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }


    public ResponseEntity<?> fetchTask(String uuid) {
        Query query = new Query(Criteria.where("uuid").is(uuid));
        Employee exist = mongoTemplate.findOne(query, Employee.class);

        if (exist != null && exist.getTasks() != null) {
            long currentTime = System.currentTimeMillis()+23400000;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

            for (Task task : exist.getTasks()) {
                try {
                    Date deadlineDate = dateFormat.parse(task.getDeadline());
                    System.out.println(task.workStatus);
                    System.out.println("Deadline Date: " + deadlineDate);
                    Date currentDate = new Date(currentTime);
                    System.out.println("Current Date: " + currentDate);
                    System.out.println("Work Status: " + task.getWorkStatus());
                    if (deadlineDate != null && deadlineDate.getTime() < currentTime && task.workStatus.equals("Completed")==false) {
                        task.setDeadlineFlag(true);

                        // Update the specific task's deadlineFlag
                        Query updateQuery = new Query(
                                Criteria.where("uuid").is(uuid).and("tasks.taskId").is(task.getTaskId())
                        );
                        Update update = new Update().set("tasks.$.deadlineFlag", true);
                            EmailRequest emailRequest = new EmailRequest();
                            emailRequest.setName(exist.employeeName);
                            emailRequest.setMail(exist.email);
                            emailRequest.setMobile(exist.phoneNumber);
                            emailRequest.setMessage("Remainder!!! DeadLine for the task "+task.description+" is exceeded."
                            +"You have set the deadline on "+deadlineDate.toString()+" .Try to complete it as soon as " +
                                            "possible . Thank You!!!"
                            );
                            emailRequest.setSubject("Deadline Exceeded for the Task");
                            sendRemainderEmail(emailRequest);

                        mongoTemplate.findAndModify(updateQuery, update, Employee.class);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(exist);
    }

    public void sendRemainderEmail(EmailRequest emailRequest) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://mailboxexpress.el.r.appspot.com/sendRemainder";

        try {
            restTemplate.postForObject(url, emailRequest, String.class);
            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }

    public ResponseEntity<?> updateStatus(String uuid,String taskId,String status)
    {
        Query query = new Query(Criteria.where("uuid").is(uuid)
                .and("tasks.taskId").is(taskId));
        mongoTemplate.findAndModify(query,new Update().set("tasks.$.status",status),Employee.class);
        if(status=="Completed")
        {
            mongoTemplate.findAndModify(query,new Update().set("tasks.$.deadlineFlag",false),Employee.class);
        }
        String message="{\"message\":\""+"Status updated Successfully"+"\"}";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
    public ResponseEntity<?> updateWorkStatus(String uuid,String taskId,String workstatus)
    {
        Query query = new Query(Criteria.where("uuid").is(uuid)
                .and("tasks.taskId").is(taskId));
        mongoTemplate.findAndModify(query,new Update().set("tasks.$.workStatus",workstatus),Employee.class);
        String message="{\"message\":\""+"Status updated Successfully"+"\"}";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
    public ResponseEntity<?> deleteTask(String uuid, String taskId) {
        Query query = new Query(Criteria.where("uuid").is(uuid).and("tasks.taskId").is(taskId));
        Update update = new Update().pull("tasks", Query.query(Criteria.where("taskId").is(taskId)));
        mongoTemplate.updateFirst(query, update, Employee.class);
        String message = "{\"message\":\"Deleted Successfully\"}";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
    public ResponseEntity<?> managerEmployee(String uuid)
    {
        Query query=new Query(Criteria.where("reportsTo").is(uuid));
        List<Employee> employees=mongoTemplate.find(query,Employee.class);
        return  ResponseEntity.status(HttpStatus.OK).body(employees);

    }
    public  ResponseEntity<?> changePassword(UserLogin user)
    {
        Query query = new Query(Criteria.where("email").is(user.getUserName()));
        mongoTemplate.findAndModify(query,new Update().set("password",user.getPassword()),Employee.class);

        String message = "{\"message\":\"Password Changed Successfully\"}";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
    public  ResponseEntity<?> forgetPassword(UserLogin user)
    {
        Query query = new Query(Criteria.where("email").is(user.getUserName()));
        Query query1 = new Query(Criteria.where("userName").is(user.getUserName()));
        Employee exist=mongoTemplate.findOne(query, Employee.class);
        UserLogin exist1=mongoTemplate.findOne(query1, UserLogin.class);
        String message="";
        if(exist!=null || exist1!=null)
        {
            message = "{\"message\":\"Check Your Email\"}";

            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setMail(user.getUserName());
            emailRequest.setMessage("This is your reset Email for Password "+
                    "https://employeeimage-latest.onrender.com/resetPassword"
            );
            emailRequest.setSubject("Reset Password");

            RestTemplate restTemplate = new RestTemplate();
            String url = "https://mailboxexpress.el.r.appspot.com/sendforgetPassword";

            try {
                restTemplate.postForObject(url, emailRequest, String.class);
                System.out.println("Email sent successfully!");
            } catch (Exception e) {
                System.out.println("Error sending email: " + e.getMessage());
            }

        }
        else
        {
            message = "{\"message\":\"Email Not Found\"}";
        }
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
    public  ResponseEntity<?> resetPassword(UserLogin user)
    {
        Query query = new Query(Criteria.where("email").is(user.getUserName()));
        Query query1 = new Query(Criteria.where("userName").is(user.getUserName()));
        Employee exist=mongoTemplate.findOne(query, Employee.class);
        UserLogin exist1=mongoTemplate.findOne(query1, UserLogin.class);
        String message="";
        if(exist!=null)
        {
            Query query2 = new Query(Criteria.where("email").is(user.getUserName()));
            mongoTemplate.findAndModify(query2,new Update().set("password",user.getPassword()),Employee.class);
            message = "{\"message\":\"Password Changed Successfully\"}";
        }
        else if(exist1!=null)
        {
            Query query2 = new Query(Criteria.where("userName").is(user.getUserName()));
            mongoTemplate.findAndModify(query2,new Update().set("password",user.getPassword()),UserLogin.class);
            message = "{\"message\":\"Password Changed Successfully\"}";
        }
        else
        {
            message = "{\"message\":\"Email Not Found\"}";
        }
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}