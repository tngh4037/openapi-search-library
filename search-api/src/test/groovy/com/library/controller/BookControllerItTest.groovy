package com.library.controller

import com.library.controller.request.SearchRequest
import com.library.controller.response.PageResult
import com.library.controller.response.SearchResponse
import com.library.service.BookApplicationService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class BookControllerItTest extends Specification {

    @Autowired
    MockMvc mockMvc

    // Spock 에서 제공하는 Mock 으로 빈등록
    @SpringBean
    BookApplicationService bookApplicationService = Mock()

    // 정상인자, 잘못된 인자(3가지 요청값)
    def "정상 인자로 요청 시, 성공한다."() {
        given:
        def request = new SearchRequest(query: "HTTP", page: 1, size: 10)

        and:
        bookApplicationService.search(*_) >> new PageResult<>(1, 10, 10, [Mock(SearchResponse)])

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/books")
                .param("query", request.getQuery())
                .param("page", request.getPage().toString())
                .param("size", request.getSize().toString())
        )

        then:
        result
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath('$.totalElements').value(10))
                .andExpect(MockMvcResultMatchers.jsonPath('$.page').value(1))
                .andExpect(MockMvcResultMatchers.jsonPath('$.size').value(10))
                .andExpect(MockMvcResultMatchers.jsonPath('$.contents').isArray())
    }

    def "query 가 비어있을 때 BadRequest 응답이 반환된다."() {
        given:
        def request = new SearchRequest(query: "", page: 1, size: 10)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/books")
                .param("query", request.getQuery())
                .param("page", request.getPage().toString())
                .param("size", request.getSize().toString())
        )

        then:
        result
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath('$.errorMessage').value("입력은 비어있을 수 없습니다."))
    }

    def "page 가 음수일 경우에 BadRequest 응답이 반환된다."() {
        given:
        def request = new SearchRequest(query: "HTTP", page: -1, size: 10)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/books")
                .param("query", request.getQuery())
                .param("page", request.getPage().toString())
                .param("size", request.getSize().toString())
        )

        then:
        result
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath('$.errorMessage').value("페이지 번호는 1 이상이어야 합니다."))
    }

    def "size 가 50을 초과하면 BadRequest 응답이 반환된다."() {
        given:
        def request = new SearchRequest(query: "HTTP", page: 1, size: 51)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/books")
                .param("query", request.getQuery())
                .param("page", request.getPage().toString())
                .param("size", request.getSize().toString())
        )

        then:
        result
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath('$.errorMessage').value("페이지 크기는 50 이하여야 합니다."))
    }

}
