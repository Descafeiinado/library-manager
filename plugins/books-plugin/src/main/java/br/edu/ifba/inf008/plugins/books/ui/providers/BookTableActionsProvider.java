package br.edu.ifba.inf008.plugins.books.ui.providers;

import br.edu.ifba.inf008.core.ui.components.table.TableComponent;
import br.edu.ifba.inf008.core.ui.components.table.interfaces.TableAction;
import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import br.edu.ifba.inf008.plugins.books.ui.PluginIcons;
import br.edu.ifba.inf008.plugins.books.ui.views.DeleteBookDialog;
import br.edu.ifba.inf008.plugins.books.ui.views.EditBookDialog;
import br.edu.ifba.inf008.plugins.books.ui.views.ViewBookDialog;
import java.util.List;
import java.util.function.Predicate;

/**
 * Provides actions for the book table component.
 */
public class BookTableActionsProvider {

    /**
     * Returns a list of actions for the book table component. Which includes
     * view, edit, and delete actions.
     *
     * @param tableComponent the table component to which the actions will be added
     * @return a list of actions for the book table component
     */
    public static List<TableAction<Book>> getActions(TableComponent<Book> tableComponent) {
        return List.of(new TableAction<>() {
            public String getLabel() {
                return "View";
            }

            public String getIconPath() {
                return PluginIcons.VIEW;
            }

            public void onAction(Book book) {
                ViewBookDialog viewBookDialog = new ViewBookDialog(book);

                viewBookDialog.showAndWait();
            }

            public Predicate<Book> getCondition() {
                return (book) -> book.getDeactivatedAt() == null;
            }
        }, new TableAction<>() {
            public String getLabel() {
                return "Edit";
            }

            public String getIconPath() {
                return PluginIcons.EDIT;
            }

            public void onAction(Book book) {
                EditBookDialog editBookDialog = new EditBookDialog(book);

                editBookDialog.setOnBookEdited((_) -> tableComponent.reload());
                editBookDialog.showAndWait();
            }

            public Predicate<Book> getCondition() {
                return (_) -> true;
            }
        }, new TableAction<>() {
            public String getLabel() {
                return "Delete";
            }

            public String getIconPath() {
                return PluginIcons.DELETE;
            }

            public void onAction(Book book) {
                DeleteBookDialog deleteBookDialog = new DeleteBookDialog(book);

                deleteBookDialog.setOnBookDeleted((_) -> tableComponent.reload());
                deleteBookDialog.showAndWait();
            }

            public Predicate<Book> getCondition() {
                return (book) -> book.getDeactivatedAt() == null;
            }
        });
    }

}
