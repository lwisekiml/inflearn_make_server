package com.group.libraryapp.miniproject;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

        // 연차를 쓴 날 이라면
        if (attendance.get().isUsingDayoff()) {
            return "오늘은 연차를 사용한 날 입니다.";
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

        // 오늘 출근했는지
        if (attendance.getWorkStartDateTime().toLocalDate().isEqual(LocalDate.now())) {
            attendance.setWorkEndDateTime(LocalDateTime.now());
            // 근무 시간 저장(실제 상황시 필요)
//            attendance.setWorkingMinutes((int)ChronoUnit.MINUTES.between(attendance.getWorkStartDateTime(), attendance.getWorkEndDateTime()));
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

//        String localMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        int year = workingEmployeeDTO.getYearMonth().getYear();
        int month = workingEmployeeDTO.getYearMonth().getMonth().getValue();

        String yearMonth = workingEmployeeDTO.getYearMonth().toString();

        List<Attendance> attendanceList = attendanceRepository.findAllByWorkStartDate(workingEmployeeDTO.getId(), yearMonth);
        List<AttendanceDto> attendanceDtoList = new ArrayList<>();

        for (int i = 1, j = 0; i <= workingEmployeeDTO.getYearMonth().lengthOfMonth(); i++) {
                if (j < attendanceList.size() && LocalDate.of(year, month, i).equals(LocalDate.from(attendanceList.get(j).getWorkEndDateTime()))) {
                    // 실제 상황에서는 필요없는 코드
                    LocalDateTime workStartDateTime = attendanceList.get(j).getWorkStartDateTime();
                    LocalDateTime workEndDateTime = attendanceList.get(j).getWorkEndDateTime();
                    attendanceList.get(j).setWorkingMinutes((int)ChronoUnit.MINUTES.between(workStartDateTime, workEndDateTime));

                    attendanceDtoList.add(AttendanceDto.toAttendanceDto(attendanceList.get(j++)));
                } else {
                    attendanceDtoList.add(new AttendanceDto(LocalDate.of(year, month, i).toString(), 0, false));
                }
        }

//        for (AttendanceDto attendanceDto : attendanceDtoList) {
//            for (Attendance attendance : attendanceList) {
//                LocalDateTime workStartDateTime = attendance.getWorkStartDateTime();
//                LocalDateTime workEndDateTime = attendance.getWorkEndDateTime();
//                attendance.setWorkingMinutes((int)ChronoUnit.MINUTES.between(workStartDateTime, workEndDateTime));
//                if (attendanceDto.getDate().equals(attendance.getWorkStartDateTime().toLocalDate().toString())) {
//                    attendanceDto = AttendanceDto.toAttendanceDto(attendance);
//                    break;
//                }
//            }
//        }

//        for (Attendance attendance : attendanceList) {
//            LocalDateTime workStartDateTime = attendance.getWorkStartDateTime();
//            LocalDateTime workEndDateTime = attendance.getWorkEndDateTime();
//            attendance.setWorkingMinutes((int)ChronoUnit.MINUTES.between(workStartDateTime, workEndDateTime));
//        }
//        attendanceRepository.flush();

        int sum = attendanceList.stream()
                .map(Attendance::getWorkingMinutes)
                .mapToInt(Integer::intValue)
                .sum();


        return new ResponseAttendance(attendanceDtoList, sum);
    }

    // 남은 연차 확인
    @GetMapping("/annual")
    public String annual(@RequestParam("id") Long employeeId) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        return employee.map(value -> String.valueOf(value.getAnnual())).orElse("없는 직원입니다.");
    }

    // 연차 신청
    @PostMapping("/annual")
    public String annualReg(@RequestBody GetEmployeeDTO.AnnualEmployeeDTO annualEmployeeDTO) {

        // employee 추가시 입사 년도에 따라 연차 차등 지급

        // 올해 입사 : 11 , 그 외 : 15
        // Attendance table 에 usingDayOff 추가
        // employee에 연차 개수 추가(올해 입사 : 11개, 그 외 : 15개)
        // 연차 신청시 employee 연차 개수 확인 후, 갯수가 남아 있으면 사용(연차 개수 - 1), 없으면 연차 사용 불가 -> "사용할 수 있는 연차가 없습니다."
        Optional<Employee> findEmployee = employeeRepository.findById(annualEmployeeDTO.getId());
        if (findEmployee.isEmpty()) {
            return "없는 직원 입니다.";
        }
        Employee employee = findEmployee.get();
        if (employee.getAnnual() > 0) {
            employee.setAnnual(employee.getAnnual() - 1);
            LocalDateTime localDateTime = annualEmployeeDTO.getAnnual().atTime(0, 0);
            // 연차 사용시 Attendance의 usingdayOff를 true로 수정
            attendanceRepository.save(new Attendance(employee, localDateTime, localDateTime, 0, true));
        } else {
            return "남은 연차가 없습니다.";
        }

        return "ok";
    }
}

