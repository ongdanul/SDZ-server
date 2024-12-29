package com.elice.sdz.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {

    @Schema(description = "페이지 번호 (1부터 시작)", example = "1")
    private int page;

    @Schema(description = "페이지 크기", example = "10")
    private int size;

    @Schema(description = "검색 타입", example = "all")
    private String type;

    @Schema(description = "검색 키워드", example = "keyword")
    private String keyword;

    public String getKeyword() {
        return keyword != null ? keyword : "";
    }

    public String getType() {
        return type != null ? type : "all";
    }

    public Pageable getPageable(String... props) {

        return PageRequest.of(this.page - 1, this.size, Sort.by(props).descending());
    }
}