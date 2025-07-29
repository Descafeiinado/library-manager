package br.edu.ifba.inf008.plugins.books.infrastructure.managers;


import br.edu.ifba.inf008.plugins.books.infrastructure.providers.BookAvailabilityProvider;
import br.edu.ifba.inf008.plugins.books.infrastructure.providers.impl.DefaultBookAvailabilityProvider;
import br.edu.ifba.inf008.plugins.books.infrastructure.repositories.BookRepository;

public class BookAvailabilityManager {

    private static BookAvailabilityManager instance;

    private BookAvailabilityProvider provider;

    public BookAvailabilityManager(BookRepository bookRepository) {
        this.provider = new DefaultBookAvailabilityProvider(bookRepository);

        instance = this;
    }

    public static BookAvailabilityManager getInstance() {
        return instance;
    }

    public void setProvider(BookAvailabilityProvider provider) {
        this.provider = provider;
    }

    public Long getAvailableCopies(Long bookId) {
        return provider.provideAvailableCopies(bookId);
    }

    public boolean isBookAvailable(Long bookId) {
        Long availableCopies = getAvailableCopies(bookId);

        return availableCopies != null && availableCopies > 0;
    }

}
