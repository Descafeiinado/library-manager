package br.edu.ifba.inf008.core.domain.models;

/**
 * Represents a request for paginated data.
 * This record holds the page number and the limit of items per page.
 *
 * @param page  The page number to retrieve (0-based index).
 * @param limit The maximum number of items to return per page.
 */
public record PageRequest(int page, int limit) {

    /**
     * Creates a new PageRequest instance.
     *
     * @param page  The page number to retrieve (0-based index).
     * @param limit The maximum number of items to return per page.
     * @return A new PageRequest instance.
     */
    public static PageRequest of(int page, int limit) {
        return new PageRequest(page, limit);
    }

}
