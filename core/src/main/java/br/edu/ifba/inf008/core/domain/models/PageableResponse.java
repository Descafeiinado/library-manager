package br.edu.ifba.inf008.core.domain.models;

import java.util.Collection;

/**
 * Represents a paginated response containing a collection of items. This class encapsulates the
 * pagination details such as page number, size, total elements, and the content of the current
 * page.
 *
 * @param <T> The type of items in the paginated response.
 */
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
