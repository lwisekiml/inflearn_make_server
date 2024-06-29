package com.group.libraryapp.miniproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HolidayDto {

    List<EventSpecial> events; // 가져온 공휴일 및 주요 기념일 목록

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EventSpecial {
        private String id; // 일정 ID
        private String title; // 일정 제목
        private TimeDefault time; // 일정 시간
        private boolean holiday; // 공휴일 여부
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TimeDefault {
        private String start_at; // 일정 시작 시각, UTC*, RFC5545의 DATE-TIME 형식
        private String end_at; // 일정 종료 시각, start_at과 같은 형식, start_at 보다 미래 시점의 값
        private boolean all_day; // 종일 일정 여부(기본값: false)
    }
}
