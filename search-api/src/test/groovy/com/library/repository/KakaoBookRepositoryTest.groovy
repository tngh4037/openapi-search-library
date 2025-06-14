package com.library.repository

import com.library.Document
import com.library.Item
import com.library.KakaoBookResponse
import com.library.Meta
import com.library.NaverBookResponse
import com.library.feign.KakaoClient
import spock.lang.Specification

import java.time.LocalDate

class KakaoBookRepositoryTest extends Specification {
    BookRepository bookRepository

    KakaoClient kakaoClient = Mock()

    void setup() {
        bookRepository = new KakaoBookRepository(kakaoClient)
    }

    def "search 호출 시, 적절한 데이터 형식으로 변환된다."() {
        given:
        def documents = [
                new Document("제목1", ["저자1"], "isbn1", "출판사1", "2016-02-01T00:00:00.000+09:00"),
                new Document("제목2", ["저자2"], "isbn2", "출판사2", "2016-02-01T00:00:00.000+09:00"),
        ]
        def meta = new Meta(false, 1, 10)
        def response = new KakaoBookResponse(documents, meta)

        and:
        1 * kakaoClient.search("HTTP", 1, 2) >> response

        when:
        def result = bookRepository.search("HTTP", 1, 2)

        then:
        verifyAll(result) {
            size() == 2
            page() == 1
            totalElements() == 10
            contents().size() == 2
            contents().get(0).pubDate() == LocalDate.of(2016, 2, 1)
        }
    }

}
