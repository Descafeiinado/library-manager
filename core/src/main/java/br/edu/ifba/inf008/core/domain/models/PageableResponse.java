package br.edu.ifba.inf008.core.domain.models;

import java.util.Collection;

public class PageableResponse<T> {

    private final int page;
    private final int size;
    private final long totalElements;
    private final Collection<T> content;

    public PageableResponse(int page, int size, long totalElements, Collection<T> content) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public Collection<T> getContent() {
        return content;
    }

}
