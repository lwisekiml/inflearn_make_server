package com.group.libraryapp.miniproject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class GetEmployeeDTO {

    private String name;
    private String teamName;
    private EmployeeType role;
    private LocalDate birthday;
    private LocalDate workStartDate;

    public static GetEmployeeDTO toEmployeeDTO(Employee employee) {
        return new GetEmployeeDTO(
                employee.getName(),
                employee.getTeam().getName(),
                employee.getRole(),
                employee.getBirthday(),
                employee.getWorkStartDate()
        );
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class RegEmployeeDTO {
        private String name;
        private String teamName;
        private EmployeeType role;
        private LocalDate birthday;
        private LocalDate workStartDate;

    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class WorkEmployeeDTO {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class WorkingEmployeeDTO {
        private Long id;
        private YearMonth yearMonth;
    }
}
