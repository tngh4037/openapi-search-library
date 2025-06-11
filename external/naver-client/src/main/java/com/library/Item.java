package com.library;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Item {
    private String title;
    private String link;
    private String image;
    private String author;
    private String discount;
    private String publisher;
    @JsonProperty("pubdate") // 받아오는 필드 명시
    private String pubDate; // 클라이언트로 보낼 때
    private String isbn;
    private String description;
}
