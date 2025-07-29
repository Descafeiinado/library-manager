package br.edu.ifba.inf008.plugins.books.ui.views;

import br.edu.ifba.inf008.core.ui.views.GenericConfirmationDialogView;
import br.edu.ifba.inf008.plugins.books.application.services.BookService;
import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import java.util.function.Consumer;

/**
 * Dialog for confirming the deletion of a book. This dialog prompts the book to confirm the
 * deletion of a specified book. If confirmed, it deletes the book and triggers a callback if
 * provided.
 */
public class DeleteBookDialog extends GenericConfirmationDialogView {

    private static final BookService BOOK_SERVICE = BookService.getInstance();

    /**
     * Callback to be executed when a book is successfully deleted.
     */
    private Consumer<Book> onBookDeleted;

    public DeleteBookDialog(Book book) {
        super("Delete Book", "Are you sure you want to delete the book: " + book.getTitle() + "?");

        setOnConfirmedClick(() -> {
            try {
                BOOK_SERVICE.delete(book.getBookId());

                if (onBookDeleted != null) {
                    onBookDeleted.accept(book);
                }
            } catch (Exception e) {
                return;
            }

            close();
        });
    }

    /**
     * Sets the callback to be executed when a book is deleted.
     *
     * @param onBookDeleted the callback to set
     */
    public void setOnBookDeleted(Consumer<Book> onBookDeleted) {
        this.onBookDeleted = onBookDeleted;
    }

}
