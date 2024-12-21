package com.elice.sdz.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class PageResponseDTO<E> {

    private int page;
    private int total;
    private int size;
    private int start;
    private int end;
    private boolean prev;
    private boolean next;
    private int first = 1;
    private int last;

    private List<E> dtoList;

    private String keyword;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total, String keyword) {

        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();

        this.total = total;
        this.dtoList = dtoList;

        this.keyword = keyword;

        this.last = (int)(Math.ceil((double) total / size));

        this.start = Math.max(1, this.page - 3);
        this.end = Math.min(this.last, this.page + 3);

        this.prev = this.start > 1;
        this.next = total > this.end * this.size;

        if (total <= 0) {
            this.dtoList = Collections.emptyList();
            this.start = 1;
            this.end = 1;
        }
    }
}