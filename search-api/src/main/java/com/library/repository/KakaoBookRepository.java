package com.library.repository;

import com.library.Document;
import com.library.KakaoBookResponse;
import com.library.controller.response.PageResult;
import com.library.controller.response.SearchResponse;
import com.library.feign.KakaoClient;
import com.library.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class KakaoBookRepository implements BookRepository {

    private final KakaoClient kakaoClient;

    @Override
    public PageResult<SearchResponse> search(String query, int page, int size) {
        KakaoBookResponse response = kakaoClient.search(query, page, size);

        List<SearchResponse> responses = response.documents().stream()
                .map(this::createResponse)
                .collect(Collectors.toList());

        return new PageResult<>(page, size, response.meta().totalCount(), responses);
    }

    private SearchResponse createResponse(Document document) {
        return SearchResponse.builder()
                .title(document.title())
                .author(document.authors().get(0)) // (저자가 여러명일 수 있다. 제일 상위 저자를 응답.)
                .publisher(document.publisher())
                .pubDate(DateUtils.parseOffsetDateTime(document.datetime()).toLocalDate())
                .isbn(document.isbn())
                .build();
    }
}
