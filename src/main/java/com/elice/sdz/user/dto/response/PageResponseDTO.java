package com.elice.sdz.user.dto.response;

import com.elice.sdz.user.dto.request.PageRequestDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class PageResponseDTO<E> {

    private int page;
    private int total;
    private int size;

    private List<E> dtoList;

    private String keyword;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total, String keyword) {

        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();

        this.total = total;
        this.dtoList = dtoList;

        this.keyword = keyword;

        if (total <= 0) {
            this.dtoList = Collections.emptyList();
        }
    }
}