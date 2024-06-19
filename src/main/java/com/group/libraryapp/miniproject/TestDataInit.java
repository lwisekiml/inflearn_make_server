package com.group.libraryapp.miniproject;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final EmployeeRepository employeeRepository;
    private final TeamRepository teamRepository;

    @PostConstruct
    public void init() {

//        Team teamA = new Team("teamA", "manager1");
//        Team teamB = new Team("teamB", "manager2");
//        Team teamC = new Team("teamC", "");
//
//        teamRepository.save(teamA);
//        teamRepository.save(teamB);
//        teamRepository.save(teamC);
//
//        Employee employee1 = new Employee("kim",  EmployeeType.MEMBER,  "1989-01-01", "2024-01-01", teamA);
//        Employee employee2 = new Employee("lee",  EmployeeType.MEMBER,  "1989-01-02", "2024-01-02", teamB);
//        Employee employee3 = new Employee("park", EmployeeType.MANAGER, "1989-01-03", "2024-01-03", teamA);
//        Employee employee4 = new Employee("sin",  EmployeeType.MANAGER, "1989-01-04", "2024-01-04", teamB);
//        Employee employee5 = new Employee("cha",  EmployeeType.MEMBER,  "1989-01-05", "2024-01-05", teamC);
//
//        employeeRepository.save(employee1);
//        employeeRepository.save(employee2);
//        employeeRepository.save(employee3);
//        employeeRepository.save(employee4);
//        employeeRepository.save(employee5);
    }
}
