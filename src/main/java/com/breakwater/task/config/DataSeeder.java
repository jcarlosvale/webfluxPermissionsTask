package com.breakwater.task.config;

import com.breakwater.task.department.model.Department;
import com.breakwater.task.department.repository.DepartmentRepository;
import com.breakwater.task.user.model.User;
import com.breakwater.task.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private static final UUID JOHN_ID = UUID.fromString("b05beb44-b7f9-4ec8-ba41-5d0f5844a4ad");
    private static final UUID JOE_ID  = UUID.fromString("47c1a9b6-ea34-44f0-a157-411880c67003");

    private static final UUID COMPANY_ID             = UUID.fromString("5a0bbdbe-c872-4467-9070-b5284d7b658f");
    private static final UUID FINANCE_ID             = UUID.fromString("57633250-8e65-4b2a-b814-5838a1b8d3ff");
    private static final UUID ACCOUNTS_RECEIVABLE_ID = UUID.fromString("751edf8c-e1da-4d2a-9f1a-d1a5509ebdbb");
    private static final UUID ACCOUNTS_PAYABLE_ID    = UUID.fromString("816c100d-c4fa-49fd-ae79-c1faad385fad");
    private static final UUID HUMAN_RESOURCES_ID     = UUID.fromString("92ad705f-91ae-43ce-81ea-e5a1ca992a9e");
    private static final UUID EMPLOYEE_RELATIONS_ID  = UUID.fromString("f99c7a37-388b-4e63-a2b0-d898a2e345ea");
    private static final UUID RECRUITING_ID          = UUID.fromString("5439aee5-4f19-451c-865e-c9cc5e588751");

    private final UserRepository       userRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public void run(ApplicationArguments args) {
        var john = new User(JOHN_ID, "John");
        var joe = new User(JOE_ID, "Joe");
        var users = List.of(john, joe);

        var company = new Department(COMPANY_ID, "Company", null);
        var finance = new Department(FINANCE_ID, "Finance", COMPANY_ID);
        var accountsReceivable = new Department(ACCOUNTS_RECEIVABLE_ID, "Accounts Receivable", FINANCE_ID);
        var accountsPayable = new Department(ACCOUNTS_PAYABLE_ID, "Accounts Payable", FINANCE_ID);
        var humanResources = new Department(HUMAN_RESOURCES_ID, "Human Resources", COMPANY_ID);
        var employeeRelations = new Department(EMPLOYEE_RELATIONS_ID, "Employee Relations", HUMAN_RESOURCES_ID);
        var recruiting = new Department(RECRUITING_ID, "Recruiting", HUMAN_RESOURCES_ID);
        var departments = List.of(company, finance, accountsReceivable, accountsPayable, humanResources, employeeRelations, recruiting);

        userRepository.insert(users)
                      .thenMany(departmentRepository.insert(departments))
                      .subscribe();
    }
}
