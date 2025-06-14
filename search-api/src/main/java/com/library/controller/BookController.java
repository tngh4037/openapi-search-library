package com.library.controller;

import com.library.controller.request.SearchRequest;
import com.library.controller.response.ErrorResponse;
import com.library.controller.response.PageResult;
import com.library.controller.response.SearchResponse;
import com.library.controller.response.StatResponse;
import com.library.service.BookApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/books")
@Tag(name = "book", description = "book api")
public class BookController {

    private final BookApplicationService bookApplicationService;

    @Operation(summary = "search API", description = "도서 검색결과 제공", tags = {"book"})
    @ApiResponses(value = { // 해당 API를 호출하면서 발생할 다양한 상황들 정의
            @ApiResponse(responseCode = "200", description = "successful request", content = @Content(schema = @Schema(implementation = PageResult.class))),
            @ApiResponse(responseCode = "400", description = "invalid request", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public PageResult<SearchResponse> search(@Valid SearchRequest request) {
        log.info("[BookController] search={}", request);
        return bookApplicationService.search(request.getQuery(), request.getPage(), request.getSize());
    }

    @Operation(summary = "stats API", description = "쿼리 통계 결과 제공", tags = {"book"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = StatResponse.class))),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/stats")
    public StatResponse findQueryStats(@RequestParam(name = "query") String query,
                                       @RequestParam(name = "date") LocalDate date) {
        log.info("[BookController] find stats query={}, date={}", query, date);
        return bookApplicationService.findQueryCount(query, date);
    }

    @Operation(summary = "stats ranking API", description = "상위 쿼리 통계 결과 제공", tags = {"book"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = StatResponse.class)))),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/stats/ranking")
    public List<StatResponse> findTop5Stats() {
        log.info("[BookController] find top 5 stats");
        return bookApplicationService.findTop5Query();
    }

}
