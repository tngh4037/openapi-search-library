package com.library.repository;

import com.library.controller.response.StatResponse;
import com.library.entity.DailyStat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface DailyStatRepository extends JpaRepository<DailyStat, Long> {

    long countByQueryAndEventDateTimeBetween(String query, LocalDateTime start, LocalDateTime end);

    @Query("select new com.library.controller.response.StatResponse(ds.query, count(ds.query)) " +
            "from DailyStat ds " +
            "group by ds.query " +
            "order by count(ds.query) desc")
    List<StatResponse> findTopQuery(Pageable pageable);

}
