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

    // sum() 함수의 결과는 Long 타입으로 반환됨
    @Query("select new com.group.libraryapp.miniproject.EmployeeWorkingMinuteDto(a.employee.id, a.employee.name, sum(a.workingMinutes)) " +
            "from Attendance a " +
            "where date_format(a.workStartDateTime, '%Y-%m') = :yearMonth " +
            "group by a.employee")
    List<EmployeeWorkingMinuteDto> findSumOfWorkingMinutesGroupByEmployee(@Param("yearMonth") String yearMonth);

    /* 아래와 같은 방식으로 쿼리 리턴할 때 초과 근무 시간도 계산이 되도록 하려 했으나 안된다.
    @Query("select new com.group.libraryapp.miniproject.EmployeeWorkingMinuteDto(a.employee.id, a.employee.name, (sum(a.workingMinutes) - :standardWorkingMinute)) " +
            "from Attendance a " +
            "where date_format(a.workStartDateTime, '%Y-%m') = :yearMonth " +
            "group by a.employee")
    List<EmployeeWorkingMinuteDto> findSumOfWorkingMinutesGroupByEmployee(@Param("yearMonth") String yearMonth, @Param("standardWorkingMinute") int standardWorkingMinute);
     */

    List<Attendance> findAllByEmployeeIdAndWorkStartDateTimeBetween(Long memberId, LocalDateTime start, LocalDateTime end);
}
