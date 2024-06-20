package com.group.libraryapp.miniproject;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ResponseAttendance {
    private List<AttendanceDto> detail;
    private int sum;

    @Builder
    public ResponseAttendance(List<AttendanceDto> detail, int sum) {
        this.detail = detail;
        this.sum = sum;
    }
}
