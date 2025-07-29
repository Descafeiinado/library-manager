package br.edu.ifba.inf008.plugins.books.infrastructure.providers.impl;

import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import br.edu.ifba.inf008.plugins.books.infrastructure.providers.BookAvailabilityProvider;
import br.edu.ifba.inf008.plugins.books.infrastructure.repositories.BookRepository;

public class DefaultBookAvailabilityProvider implements BookAvailabilityProvider {

    private final BookRepository bookRepository;

    public DefaultBookAvailabilityProvider(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Long provideAvailableCopies(Long bookId) {
        return bookRepository.findById(bookId)
                .map(Book::getCopiesAvailable)
                .map(Integer::longValue)
                .orElse(0L);
    }

}
