package com.group.libraryapp.miniproject;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class AttendanceDto {
    private String date; // 2024-06-01
    private int workingMinutes;

    @Builder
    public AttendanceDto(String date, int workingMinutes) {
        this.date = date;
        this.workingMinutes = workingMinutes;
    }

    public static AttendanceDto toAttendanceDto(Attendance attendance) {
        return AttendanceDto.builder()
                .date(LocalDate.from(attendance.getWorkStartDateTime()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .workingMinutes(attendance.getWorkingMinutes())
                .build();
    }
}
