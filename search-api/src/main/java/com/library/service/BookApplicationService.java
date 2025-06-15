package com.library.service;

import com.library.controller.response.PageResult;
import com.library.controller.response.SearchResponse;
import com.library.controller.response.StatResponse;
import com.library.entity.DailyStat;
import com.library.service.event.SearchEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookApplicationService {

    private final BookQueryService bookQueryService;
    // private final DailyStatCommandService dailyStatCommandService;
    private final DailyStatQueryService dailyStatQueryService;

    private final ApplicationEventPublisher eventPublisher;

    public PageResult<SearchResponse> search(String query, int page, int size) {
        // 1) 외부 API 호출
        PageResult<SearchResponse> response = bookQueryService.search(query, page, size);

        // 2) 검색 통계 데이터 저장 => 응답 향상을 위해 격리 (by 이벤트)
        if (!response.contents().isEmpty()) {
            log.info("검색결과 개수: {}", response.size());
            eventPublisher.publishEvent(new SearchEvent(query, LocalDateTime.now()));
        }
        // 참고) 원래 하려고 했던 아래 처리는 이벤트 핸들러(SearchEventHandler)에서 처리
        // DailyStat dailyStat = new DailyStat(query, LocalDateTime.now());
        // dailyStatCommandService.save(dailyStat);

        // 3) API 응답
        return response;
    }

    public StatResponse findQueryCount(String query, LocalDate date) {
        return dailyStatQueryService.findQueryCount(query, date);
    }

    public List<StatResponse> findTop5Query() {
        return dailyStatQueryService.findTop5Query();
    }
}
