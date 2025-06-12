package com.library.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "daily_stat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyStat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "query")
    private String query;

    @Column(name = "eventDateTime")
    private LocalDateTime eventDateTime; // 검색이라는 행위가 발생한 시간

    public DailyStat(String query, LocalDateTime eventDateTime) {
        this.query = query;
        this.eventDateTime = eventDateTime;
    }
}
