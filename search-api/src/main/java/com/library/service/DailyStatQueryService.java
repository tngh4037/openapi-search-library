package com.library.service;

import com.library.controller.response.StatResponse;
import com.library.repository.DailyStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DailyStatQueryService {

    private final DailyStatRepository dailyStatRepository;

    public StatResponse findQueryCount(String query, LocalDate date) {
        long count = dailyStatRepository.countByQueryAndEventDateTimeBetween(
                query,
                date.atStartOfDay(), // localDate -> localDateTime (00:00:00)
                date.atTime(LocalTime.MAX) // localDate -> localDateTime (23:59:59.999999999)
        );

        return new StatResponse(query, count);
    }

}
