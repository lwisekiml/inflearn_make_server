package com.group.libraryapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;
import java.time.format.DateTimeFormatter;

@SpringBootTest
public class MiniTest {

    @Test
    public void test() {
        String targetDate = "2020-02-02";

//        YearMonth yearMonth = YearMonth.from(LocalDate.parse(targetDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        YearMonth yearMonth = YearMonth.from(LocalDate.now());

        System.out.println(yearMonth.lengthOfMonth());


        LocalDateTime startTime = LocalDateTime.of(2024, 6, 20, 10, 35);
// 결과 : 17:14:55
        LocalDateTime endTime = LocalDateTime.of(2024, 6,20,17,35);
// 결과 : 18:17:35

        Duration duration = Duration.between(startTime, endTime);
        long minutes = duration.toMinutes();
        duration.getSeconds();


    }

    @Test
    public void 날짜시간() {
        String date = "2024-06-06T00:00:00Z";
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);

        // ZonedDateTime에서 LocalDate로 변환
        LocalDate localDate = zonedDateTime.toLocalDate();
// 결과 출력
        System.out.println("LocalDate: " + localDate);
    }
}
