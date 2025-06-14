package com.library.service;

import com.library.controller.response.StatResponse;
import com.library.repository.DailyStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyStatQueryService {

    private static final int PAGE = 0;
    private static final int SIZE = 5;

    private final DailyStatRepository dailyStatRepository;

    public StatResponse findQueryCount(String query, LocalDate date) {
        long count = dailyStatRepository.countByQueryAndEventDateTimeBetween(
                query,
                date.atStartOfDay(), // localDate -> localDateTime (00:00:00)
                date.atTime(LocalTime.MAX) // localDate -> localDateTime (23:59:59.999999999)
        );

        return new StatResponse(query, count);
    }

    public List<StatResponse> findTop5Query() {
        Pageable pageable = PageRequest.of(PAGE, SIZE);
        return dailyStatRepository.findTopQuery(pageable);
    }

}
