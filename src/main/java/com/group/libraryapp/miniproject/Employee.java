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
    private LocalDate workStartDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "employee")
    private List<Attendance> attendances = new ArrayList<>();

    public Employee(GetEmployeeDTO.RegEmployeeDTO regEmployeeDTO, Team team) {
        this.setTeam(team);
        this.name = regEmployeeDTO.getName();
        this.role = regEmployeeDTO.getRole();
        this.birthday = regEmployeeDTO.getBirthday();
        this.workStartDate = regEmployeeDTO.getWorkStartDate();
    }

    // TestDataInit
    public Employee(String name, EmployeeType role, LocalDate birthday, LocalDate workStartDate, Team team) {
        this.name = name;
        this.role = role;
        this.birthday = birthday;
        this.workStartDate = workStartDate;
        this.team = team;
    }
}
