package com.group.libraryapp.miniproject;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MiniController {

    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    @GetMapping("/team")
    public List<GetTeamDTO> team() {
        return teamRepository.findAll().stream().map(GetTeamDTO::toTeamDTO).collect(Collectors.toList());
    }

    @PostMapping("/team")
    public void addTeam(@RequestBody GetTeamDTO.RegTeamDTO regTeamDTO) {
        teamRepository.save(new Team(regTeamDTO));
    }

    @GetMapping("/employee")
    public List<GetEmployeeDTO> employee() {
        return employeeRepository.findAll().stream().map(GetEmployeeDTO::toEmployeeDTO).collect(Collectors.toList());
    }

    @PostMapping("/employee")
    public String addEmployee(@RequestBody GetEmployeeDTO.RegEmployeeDTO regEmployeeDTO) {
        Team findTeam = teamRepository.findByName(regEmployeeDTO.getTeamName());
        if (findTeam == null) {
            return "잘못된 팀입니다.";
        }
        employeeRepository.save(new Employee(regEmployeeDTO, findTeam));
        return "ok";
    }

    // 출근
    @PostMapping("/goToWork")
    public String gotoWork(@RequestParam("employeeId") Long employeeId) {

        Optional<Employee> employee = employeeRepository.findById(employeeId);

        if (employee.isEmpty()) {
            return "등록되지 않은 직원입니다.";
        }

        String localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Optional<Attendance> attendance = attendanceRepository.findAllByWorkStartDateTime(employeeId, localDate);

        // 출근을 안했다면
        if (attendance.isEmpty()) {
            attendanceRepository.save(new Attendance(employee.get(), LocalDateTime.now()));
            return "ok";
        }

        // 퇴근했고, 퇴근 날짜가 오늘이라면(없어도 될거 같지만 한 걸음 더에 있는 내용이라 추가)
        if (attendance.get().getWorkEndDateTime() != null && attendance.get().getWorkEndDateTime().toLocalDate().isEqual(LocalDate.now())) {
            return "퇴근하고 다시 출근 등록할 수 없습니다.";
        }

        return "이미 출근 등록을 했습니다.";
    }

    // 퇴근
    @PostMapping("/getOffWork")
    public String gotoOffWork(@RequestParam("employeeId") Long employeeId) {
        // 출근과 같은 메소드를 만들면 되지만 다른 방시긍로 해봄
        Attendance attendance = attendanceRepository.findFirstByEmployeeIdOrderByWorkStartDateTimeDesc(employeeId);

        if (attendance.getWorkStartDateTime().toLocalDate().isEqual(LocalDate.now())) {
            attendance.setWorkEndDateTime(LocalDateTime.now());
            attendanceRepository.flush();
            return "ok";
        }

        return "출근 등록을 하지 않았습니다.";
    }

    // 특정 직원 날짜별 근무시간 조회
    @PostMapping("/workingHours")
    public ResponseAttendance workingHours(@RequestBody GetEmployeeDTO.WorkingEmployeeDTO workingEmployeeDTO) {

//        YearMonth yearMonth = YearMonth.from(LocalDate.now());
//        LocalDate startOfMonth = yearMonth.atDay(1);
//        LocalDate endOfMonth = yearMonth.atEndOfMonth();
//        List<Attendance> timeBetween = attendanceRepository.findAllByEmployeeIdAndWorkStartDateTimeBetween(workingEmployeeDTO.getId(), startOfMonth.atStartOfDay(), endOfMonth.atStartOfDay());

        String localMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        List<Attendance> attendanceList = attendanceRepository.findAllByWorkStartDate(workingEmployeeDTO.getId(), localMonth);
        for (Attendance attendance : attendanceList) {
            LocalDateTime workStartDateTime = attendance.getWorkStartDateTime();
            LocalDateTime workEndDateTime = attendance.getWorkEndDateTime();
            attendance.setWorkingMinutes((int)ChronoUnit.MINUTES.between(workStartDateTime, workEndDateTime));
        }

        int sum = attendanceList.stream()
                .map(Attendance::getWorkingMinutes)
                .mapToInt(Integer::intValue)
                .sum();

        List<AttendanceDto> attendanceDtos = attendanceList.stream().map(AttendanceDto::toAttendanceDto).collect(Collectors.toList());

        return new ResponseAttendance(attendanceDtos, sum);
    }
}

