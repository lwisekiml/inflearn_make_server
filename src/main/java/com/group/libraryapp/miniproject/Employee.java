package com.group.libraryapp.miniproject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private EmployeeType role;

    private LocalDate birthday;
    private LocalDate workStartDate; // 입사 날짜 : 2024-06-01
    private int annual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "employee")
    private List<Attendance> attendances = new ArrayList<>();

    // postman 직원 등록
    public Employee(GetEmployeeDTO.RegEmployeeDTO regEmployeeDTO, Team team) {
        this.setTeam(team);
        this.name = regEmployeeDTO.getName();
        this.role = regEmployeeDTO.getRole();
        this.birthday = regEmployeeDTO.getBirthday();
        this.workStartDate = regEmployeeDTO.getWorkStartDate();

        if (workStartDate.getYear() == LocalDate.now().getYear()) {
            annual = StaticVariable.THIS_YEAR_WORKING_START_ANNUAL;
        } else {
            annual = StaticVariable.ANNUAL;
        }
    }

    // TestDataInit
    public Employee(String name, EmployeeType role, LocalDate birthday, LocalDate workStartDate, Team team) {
        this.team = team;
        this.name = name;
        this.role = role;
        this.birthday = birthday;
        this.workStartDate = workStartDate;

        if (workStartDate.getYear() == LocalDate.now().getYear()) {
            annual = StaticVariable.THIS_YEAR_WORKING_START_ANNUAL;
        } else {
            annual = StaticVariable.ANNUAL;
        }
    }
}
