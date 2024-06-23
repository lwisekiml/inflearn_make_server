package com.group.libraryapp.miniproject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Attendance findFirstByEmployeeIdOrderByWorkStartDateTimeDesc(Long memberId);

    // localDate와 같은 날짜가 있는지
    @Query("select a from Attendance a " +
            "where a.employee.id = :employeeId " +
            "and date_format(a.workStartDateTime , '%Y-%m-%d') = :localDate")
    Optional<Attendance> findAllByWorkStartDateTime(@Param("employeeId") Long employeeId, @Param("localDate") String localDate);

    @Query("select a from Attendance a left join a.employee " +
            "where a.employee.id = :employeeId " +
            "and date_format(a.workStartDateTime , '%Y-%m') = :yearMonth")
    List<Attendance> findAllByWorkStartDate(@Param("employeeId") Long employeeId, @Param("yearMonth") String yearMonth);

    List<Attendance> findAllByEmployeeIdAndWorkStartDateTimeBetween(Long memberId, LocalDateTime start, LocalDateTime end);
}
