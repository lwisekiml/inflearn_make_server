package com.group.libraryapp.miniproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MiniService {

    private final AttendanceRepository attendanceRepository;

    @Value("${kakao.admin}")
    private String kakaoAdmin;

    public MiniService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }


    @Scheduled(cron = "0 31 21 * * *")
    private void createFile() throws JsonProcessingException {

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
    }
}
