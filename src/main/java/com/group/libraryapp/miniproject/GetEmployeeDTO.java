package com.group.libraryapp.miniproject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class GetEmployeeDTO {

    private String name;
    private String teamName;
    private EmployeeType role;
    private String birthday;
    private String workStartDate;

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
        private String birthday;
        private String workStartDate;

    }
}
