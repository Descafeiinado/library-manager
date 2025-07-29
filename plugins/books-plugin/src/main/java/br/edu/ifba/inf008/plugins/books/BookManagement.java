package br.edu.ifba.inf008.plugins.books;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IPlugin;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.domain.annotations.Plugin;
import br.edu.ifba.inf008.core.infrastructure.managers.HibernateManager;
import br.edu.ifba.inf008.core.ui.models.TabInformation;
import br.edu.ifba.inf008.plugins.books.domain.entities.Book;
import br.edu.ifba.inf008.plugins.books.application.services.BookService;
import br.edu.ifba.inf008.plugins.books.ui.CSS;
import br.edu.ifba.inf008.plugins.books.ui.PluginIcons;
import br.edu.ifba.inf008.plugins.books.ui.views.MainView;

@Plugin(name = "book-management")
public class BookManagement implements IPlugin {

    private BookService bookService;

    @Override
    public boolean init() {

        HibernateManager.registerEntityClass(Book.class);

        bookService = BookService.getInstance();

        return true;
    }

    @Override
    public boolean postInit() {
        ICore core = ICore.getInstance();
        IUIController uiController = core.getUIController();

        uiController.loadStylesheetToScene(uiController.getMainScene(), CSS.BOOKS_MANAGEMENT);
        uiController.createTab(
                new TabInformation("Books", uiController.loadIcon(PluginIcons.BOOKS)),
                MainView.supply(uiController, bookService));

        return true;
    }
}
