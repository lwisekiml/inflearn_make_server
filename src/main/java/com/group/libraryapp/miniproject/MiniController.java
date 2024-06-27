package com.group.libraryapp.miniproject;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;

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

        // 1
        int year = workingEmployeeDTO.getYearMonth().getYear();
        int month = workingEmployeeDTO.getYearMonth().getMonth().getValue();

        String yearMonth = workingEmployeeDTO.getYearMonth().toString();

        List<Attendance> attendanceList = attendanceRepository.findAllByWorkStartDate(workingEmployeeDTO.getId(), yearMonth);
        List<AttendanceDto> attendanceDtoList = new ArrayList<>();

        for (int date = 1, attendanceListIndex = 0; date <= workingEmployeeDTO.getYearMonth().lengthOfMonth(); date++) {
            if (attendanceListIndex < attendanceList.size()
                    && LocalDate.of(year, month, date).equals(LocalDate.from(attendanceList.get(attendanceListIndex).getWorkEndDateTime()))) {
                // 실제 상황에서는 밑에 세 줄은 필요없는 코드
                LocalDateTime workStartDateTime = attendanceList.get(attendanceListIndex).getWorkStartDateTime();
                LocalDateTime workEndDateTime = attendanceList.get(attendanceListIndex).getWorkEndDateTime();
                attendanceList.get(attendanceListIndex).setWorkingMinutes((int)ChronoUnit.MINUTES.between(workStartDateTime, workEndDateTime));

                attendanceDtoList.add(AttendanceDto.toAttendanceDto(attendanceList.get(attendanceListIndex++)));
            } else {
                attendanceDtoList.add(new AttendanceDto(LocalDate.of(year, month, date).toString(), 0, false));
            }
        }

        // 2. 실제 상황으로 하려면 1번과 비슷해져서 실제 상황은 구현 안함
//        LocalDate startDate = workingEmployeeDTO.getYearMonth().atDay(1);
//        LocalDate endDate = workingEmployeeDTO.getYearMonth().atEndOfMonth();
//
//        List<Attendance> attendanceList = attendanceRepository.findAllByEmployeeIdAndWorkStartDateTimeBetween(
//                workingEmployeeDTO.getId(), startDate.atStartOfDay(), endDate.atStartOfDay());
//        Map<LocalDate, Integer> attendanceMap = new HashMap<>();
//        Map<LocalDate, Boolean> usingDayoffMap = new HashMap<>();
//
//        for (Attendance attendance : attendanceList) {
//            if (attendance.getWorkEndDateTime() != null) {
//                LocalDate date = attendance.getWorkStartDateTime().toLocalDate();
//                int workingMinutes = (int) Duration.between(attendance.getWorkStartDateTime(), attendance.getWorkEndDateTime()).toMinutes();
//                attendanceMap.put(date, workingMinutes);
//
//                usingDayoffMap.put(date, attendance.isUsingDayoff());
//            }
//        }
//
//        List<AttendanceDto> attendanceDtoList = new ArrayList<>();
//        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
//            int workingMinutes = attendanceMap.getOrDefault(date, 0);
//            boolean usingDayoff = usingDayoffMap.getOrDefault(date, false);
//
//            attendanceDtoList.add(AttendanceDto.create(date.toString(), workingMinutes, usingDayoff));
//        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    @GetMapping("/overtime")
    public String overtime(@RequestParam("id") Long employeeId) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getAnniversaryInfo"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "="); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("1", StandardCharsets.UTF_8)); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("10", StandardCharsets.UTF_8)); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("solYear", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("2019", StandardCharsets.UTF_8)); /*연*/
        urlBuilder.append("&" + URLEncoder.encode("solMonth", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("02", StandardCharsets.UTF_8)); /*월*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        System.out.println(sb.toString());

        return "ok";
    }
}

