package com.group.libraryapp.miniproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequiredArgsConstructor
public class MiniController {

    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    @Value("${kakao.admin}")
    private String kakaoAdmin;

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
//                LocalDateTime workStartDateTime = attendanceList.get(attendanceListIndex).getWorkStartDateTime();
//                LocalDateTime workEndDateTime = attendanceList.get(attendanceListIndex).getWorkEndDateTime();
//                attendanceList.get(attendanceListIndex).setWorkingMinutes((int)ChronoUnit.MINUTES.between(workStartDateTime, workEndDateTime));

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
    public List<EmployeeWorkingMinuteDto> overtime(@RequestBody GetEmployeeDTO.OvertimeEmployeeDTO overtimeEmployeeDTO) throws IOException {

        YearMonth yearMonth = overtimeEmployeeDTO.getYearMonth();
        LocalDateTime startDateTime = LocalDateTime.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1, 0, 0, 1);
        LocalDateTime endDateTime = LocalDateTime.of(yearMonth.getYear(), yearMonth.getMonthValue(), yearMonth.lengthOfMonth(), 0, 0, 1);

        // LocalDateTime을 ZonedDateTime으로 변환 (UTC)
        ZonedDateTime zonedDateTimeStart = startDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime zonedDateTimeEnd = endDateTime.atZone(ZoneId.of("UTC"));

        // ISO 8601 형식으로 변환
        // 참고 : https://zhfvkq.tistory.com/37
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        String startFormattedString = zonedDateTimeStart.format(formatter);
        String endFormattedString = zonedDateTimeEnd.format(formatter);

        String apiUrl = "https://kapi.kakao.com";
        WebClient webClient = WebClient.create(apiUrl);

        String response = webClient
                .get()
                .uri(url -> url.path("/v2/api/calendar/holidays")
                        .queryParam("from", startFormattedString)
                        .queryParam("to", endFormattedString)
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoAdmin)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        HolidayDto holidayDto = objectMapper.readValue(response, HolidayDto.class);

        Set<LocalDate> holidays = new HashSet<>();

        for (HolidayDto.EventSpecial eventSpecial : holidayDto.events) {
            if (eventSpecial.isHoliday()) {
                String startAt = eventSpecial.getTime().getStart_at();
                ZonedDateTime startZoneDateTime = ZonedDateTime.parse(startAt, DateTimeFormatter.ISO_DATE_TIME);
                LocalDate holiday = startZoneDateTime.toLocalDate();
                holidays.add(holiday);
            }
        }

        // 주말
        int year = yearMonth.getYear();
        int month = yearMonth.getMonthValue();

        // 이번 달의 모든 날짜를 생성
        List<LocalDate> datesOfMonth = IntStream.rangeClosed(1, yearMonth.lengthOfMonth())
                .mapToObj(day -> LocalDate.of(year, month, day))
                .filter(date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)
                .collect(Collectors.toList());

        holidays.addAll(datesOfMonth);
        int holidaySize = holidays.size();

        // 기준 근무 시간(분) = (해당 월의 일 수 - holidaySize) * (8 * 60)분
        int standardWorkingMinute = (yearMonth.lengthOfMonth() - holidaySize) * (8 * 60);

        List<EmployeeWorkingMinuteDto> groupByWorkingMinutes = attendanceRepository
                .findSumOfWorkingMinutesGroupByEmployee(yearMonth.toString());

        groupByWorkingMinutes.forEach(dto -> dto.changeOvertimeMinutes(standardWorkingMinute));

        return groupByWorkingMinutes;
    }

    @GetMapping("/csv")
    public String csvMaking() throws JsonProcessingException {

        YearMonth yearMonth = YearMonth.of(2024, 6);
        LocalDateTime startDateTime = LocalDateTime.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1, 0, 0, 1);
        LocalDateTime endDateTime = LocalDateTime.of(yearMonth.getYear(), yearMonth.getMonthValue(), yearMonth.lengthOfMonth(), 0, 0, 1);

        // LocalDateTime을 ZonedDateTime으로 변환 (UTC)
        ZonedDateTime zonedDateTimeStart = startDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime zonedDateTimeEnd = endDateTime.atZone(ZoneId.of("UTC"));

        // ISO 8601 형식으로 변환
        // 참고 : https://zhfvkq.tistory.com/37
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        String startFormattedString = zonedDateTimeStart.format(formatter);
        String endFormattedString = zonedDateTimeEnd.format(formatter);

        String apiUrl = "https://kapi.kakao.com";
        WebClient webClient = WebClient.create(apiUrl);

        String response = webClient
                .get()
                .uri(url -> url.path("/v2/api/calendar/holidays")
                        .queryParam("from", startFormattedString)
                        .queryParam("to", endFormattedString)
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoAdmin)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        HolidayDto holidayDto = objectMapper.readValue(response, HolidayDto.class);

        Set<LocalDate> holidays = new HashSet<>();

        for (HolidayDto.EventSpecial eventSpecial : holidayDto.events) {
            if (eventSpecial.isHoliday()) {
                String startAt = eventSpecial.getTime().getStart_at();
                ZonedDateTime startZoneDateTime = ZonedDateTime.parse(startAt, DateTimeFormatter.ISO_DATE_TIME);
                LocalDate holiday = startZoneDateTime.toLocalDate();
                holidays.add(holiday);
            }
        }

        // 주말
        int year = yearMonth.getYear();
        int month = yearMonth.getMonthValue();

        // 이번 달의 모든 날짜를 생성
        List<LocalDate> datesOfMonth = IntStream.rangeClosed(1, yearMonth.lengthOfMonth())
                .mapToObj(day -> LocalDate.of(year, month, day))
                .filter(date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)
                .collect(Collectors.toList());

        holidays.addAll(datesOfMonth);
        int holidaySize = holidays.size();

        // 기준 근무 시간(분) = (해당 월의 일 수 - holidaySize) * (8 * 60)분
        int standardWorkingMinute = (yearMonth.lengthOfMonth() - holidaySize) * (8 * 60);

        List<EmployeeWorkingMinuteDto> groupByWorkingMinutes = attendanceRepository
                .findSumOfWorkingMinutesGroupByEmployee(yearMonth.toString());

        groupByWorkingMinutes.forEach(dto -> dto.changeOvertimeMinutes(standardWorkingMinute));

        ////////////////////////////////////////////////////////////////////////////////////////
        String filePath = "G:/test/test.csv";
        String NEWLINE = System.lineSeparator();

        try {
//            file = new File(filePath);
//            bw = new BufferedWriter(new FileWriter(file));
            BufferedWriter bw = Files.newBufferedWriter(Path.of(filePath), StandardCharsets.UTF_8);
            bw.write("\uFEFF"); // 한글 깨짐 방지(UTF-8-BOM 형식으로 저장)

            bw.write("id,name,overtimeMinutes"+NEWLINE);
            for (EmployeeWorkingMinuteDto dto : groupByWorkingMinutes) {
                bw.write(dto.getId()+","+dto.getName()+","+dto.getOvertimeMinutes()+NEWLINE);
            }
            bw.flush();
            bw.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "ok";
    }
}

