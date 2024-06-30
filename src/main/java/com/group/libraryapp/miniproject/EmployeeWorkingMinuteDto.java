package com.group.libraryapp.miniproject;

import lombok.Getter;

@Getter
public class EmployeeWorkingMinuteDto {
    private final Long id;
    private final String name;
    private int overtimeMinutes;

    public EmployeeWorkingMinuteDto(Long id, String name, Long sumOfWorkingMinutes) {
        this.id = id;
        this.name = name;
        this.overtimeMinutes = sumOfWorkingMinutes.intValue();
    }

    public void changeOvertimeMinutes(int standardWorkingMinute) {
        if (overtimeMinutes > standardWorkingMinute) {
            overtimeMinutes -= standardWorkingMinute;
        } else {
            overtimeMinutes = 0;
        }
    }
}
