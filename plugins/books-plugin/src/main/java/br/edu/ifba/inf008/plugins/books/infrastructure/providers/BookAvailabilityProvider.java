package br.edu.ifba.inf008.plugins.books.infrastructure.providers;

public interface BookAvailabilityProvider {

    Long provideAvailableCopies(Long bookId);
}
