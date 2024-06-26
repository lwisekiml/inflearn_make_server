package com.group.libraryapp.miniproject;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class AttendanceDto {
    private String date; // 2024-06-01
    private int workingMinutes;
    private boolean usingDayoff; // 연차 사용 유무

    public AttendanceDto(String date, int workingMinutes, boolean usingDayoff) {
        this.date = date;
        this.workingMinutes = workingMinutes;
        this.usingDayoff = usingDayoff;
    }

    public static AttendanceDto toAttendanceDto(Attendance attendance) {
        return AttendanceDto.builder()
                .date(LocalDate.from(attendance.getWorkStartDateTime()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .workingMinutes(attendance.getWorkingMinutes())
                .usingDayoff(attendance.isUsingDayoff())
                .build();
    }

    public static AttendanceDto create(String date, int workingMinutes, boolean usingDayoff) {
        return AttendanceDto.builder()
                .date(date)
                .workingMinutes(workingMinutes)
                .usingDayoff(usingDayoff)
                .build();
    }
}
