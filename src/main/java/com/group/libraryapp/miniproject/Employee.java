package com.group.libraryapp.miniproject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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

    private String birthday;
    private String workStartDate;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    public Employee(GetEmployeeDTO.RegEmployeeDTO regEmployeeDTO, Team team) {
        this.setTeam(team);
        this.name = regEmployeeDTO.getName();
        this.role = regEmployeeDTO.getRole();
        this.birthday = regEmployeeDTO.getBirthday();
        this.workStartDate = regEmployeeDTO.getWorkStartDate();
    }
}
