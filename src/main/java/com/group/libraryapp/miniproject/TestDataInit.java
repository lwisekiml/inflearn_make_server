package com.group.libraryapp.miniproject;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final EmployeeRepository employeeRepository;
    private final TeamRepository teamRepository;
    private final AttendanceRepository attendanceRepository;

    @PostConstruct
    public void init() {

        Team teamA = new Team("teamA", "manager1");
        Team teamB = new Team("teamB", "manager2");
        Team teamC = new Team("teamC", "");

        teamRepository.save(teamA);
        teamRepository.save(teamB);
        teamRepository.save(teamC);

        Employee employee1 = new Employee("kim", EmployeeType.MEMBER, LocalDate.of(1989, 1, 1), LocalDate.of(2022, 1, 1), teamA);
        Employee employee2 = new Employee("lee", EmployeeType.MEMBER, LocalDate.of(1989, 1, 2), LocalDate.of(2023, 1, 2), teamB);
        Employee employee3 = new Employee("park", EmployeeType.MANAGER, LocalDate.of(1989, 1, 3), LocalDate.of(2024, 1, 3), teamA);
        Employee employee4 = new Employee("sin", EmployeeType.MANAGER, LocalDate.of(1989, 1, 4), LocalDate.of(2024, 1, 4), teamB);
        Employee employee5 = new Employee("cha", EmployeeType.MEMBER, LocalDate.of(1989, 1, 5), LocalDate.of(2024, 1, 5), teamC);

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
        employeeRepository.save(employee3);
        employeeRepository.save(employee4);
        employeeRepository.save(employee5);

        YearMonth yearMonth = YearMonth.from(LocalDate.now());
//        yearMonth.lengthOfMonth();
        int day = LocalDate.now().getDayOfMonth();

        for (int i = 1; i <= LocalDate.now().getDayOfMonth(); i++) {
            attendanceRepository.save(new Attendance(
                            employee1,
                            LocalDateTime.of(2024, 6, i, 9, 0),
                            LocalDateTime.of(2024, 6, i, 9, 0).plusHours(5),
                            300,
                            false
                    )
            );

            attendanceRepository.save(new Attendance(
                            employee1,
                            LocalDateTime.of(2024, 5, i, 9, 0),
                            LocalDateTime.of(2024, 5, i, 9, 0).plusHours(5),
                            310,
                            false
                    )
            );
        }

        for (int i = 1; i <= LocalDate.now().getDayOfMonth(); i++) {
            attendanceRepository.save(new Attendance(
                            employee2,
                            LocalDateTime.of(2024, 6, i, 9, 0),
                            LocalDateTime.of(2024, 6, i, 9, 0).plusHours(7),
                            420,
                            false
                    )
            );
        }
    }
}
