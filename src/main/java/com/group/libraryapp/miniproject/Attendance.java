package com.group.libraryapp.miniproject;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private LocalDateTime workStartDateTime;
    private LocalDateTime workEndDateTime;
    private int workingMinutes;

    public Attendance(Employee employee, LocalDateTime workStartDateTime) {
        this.employee = employee;
        this.workStartDateTime = workStartDateTime;
    }
}
