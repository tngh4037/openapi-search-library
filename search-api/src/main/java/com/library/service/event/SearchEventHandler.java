package com.library.service.event;

import com.library.entity.DailyStat;
import com.library.service.DailyStatCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

// 이벤트를 받는 핸들러 정의
@Slf4j
@Component
@RequiredArgsConstructor
public class SearchEventHandler {

    private final DailyStatCommandService dailyStatCommandService;

    @Async // 다른 스레드에서 동작
    @EventListener
    public void handleEvent(SearchEvent searchEvent) {
/*
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
*/
        log.info("[SearchEventHandler] handleEvent: {}", searchEvent);
        DailyStat dailyStat = new DailyStat(searchEvent.query(), searchEvent.timestamp());
        dailyStatCommandService.save(dailyStat);
    }
}
