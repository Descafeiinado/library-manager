package br.edu.ifba.inf008.core.domain.models;

public record PageRequest(int page, int limit) {

    public static PageRequest of(int page, int limit) {
        return new PageRequest(page, limit);
    }

}
