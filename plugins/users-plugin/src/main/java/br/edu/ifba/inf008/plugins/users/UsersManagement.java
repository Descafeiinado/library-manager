package br.edu.ifba.inf008.plugins.users;

import br.edu.ifba.inf008.core.ICore;
import br.edu.ifba.inf008.core.IPlugin;
import br.edu.ifba.inf008.core.IUIController;
import br.edu.ifba.inf008.core.domain.annotations.Plugin;
import br.edu.ifba.inf008.core.infrastructure.managers.HibernateManager;
import br.edu.ifba.inf008.core.ui.Icons;
import br.edu.ifba.inf008.core.ui.components.TableOfContents;
import br.edu.ifba.inf008.core.ui.models.TabInformation;
import br.edu.ifba.inf008.core.ui.models.TableAction;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import br.edu.ifba.inf008.plugins.users.infrastructure.services.UserService;
import br.edu.ifba.inf008.plugins.users.ui.CSS;
import br.edu.ifba.inf008.plugins.users.ui.PluginIcons;
import br.edu.ifba.inf008.plugins.users.ui.views.CreateUserDialog;
import br.edu.ifba.inf008.plugins.users.ui.views.DeleteUserDialog;
import br.edu.ifba.inf008.plugins.users.ui.views.EditUserDialog;
import br.edu.ifba.inf008.plugins.users.ui.views.ViewUserDialog;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

@Plugin(name = "users-management")
public class UsersManagement implements IPlugin {

    private IUIController uiController;
    private UserService userService;

    @Override
    public boolean init() {
        ICore core = ICore.getInstance();

        uiController = core.getUIController();
        uiController.loadStylesheetToScene(uiController.getMainScene(), CSS.USERS_MANAGEMENT);

        uiController.createTab(
                new TabInformation("Users", uiController.loadIcon(PluginIcons.USERS)),
                this::createMainContent);

        HibernateManager.registerEntityClass(User.class);

        userService = UserService.getInstance();

        return true;
    }

    private VBox createMainContent() {
        VBox mainContent = new VBox(10);
        mainContent.getStyleClass().add("um-main-content");

        Label titleLabel = new Label("Users");
        titleLabel.getStyleClass().add("um-title-label");

        Button createButton = new Button("New User");
        createButton.getStyleClass().add("um-icon-button");
        createButton.setGraphic(uiController.loadIcon(Icons.PLUS));

        Region spacer = new Region();

        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(10, titleLabel, spacer, createButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("um-header");

        TableOfContents<User> tableOfContents = new TableOfContents<>(User.class,
                (page, size) -> {
            System.out.println("Loading users: page " + page + ", size " + size + "." + Arrays.toString(
                    userService.findAll(page, size).getContent().toArray()));
                    return userService.findAll(page, size);
                });

        tableOfContents.addActionColumn(List.of(new TableAction<>() {
                                                    public String getLabel() {
                                                        return "View";
                                                    }

                                                    public String getIconPath() {
                                                        return PluginIcons.VIEW;
                                                    }

                                                    public void onAction(User user) {
                                                        ViewUserDialog viewUserDialog = new ViewUserDialog(user);

                                                        viewUserDialog.showAndWait();
                                                    }

                                                    public Predicate<User> getCondition() {
                                                        return (user) -> user.getDeactivatedAt() == null;
                                                    }
                                                },

                new TableAction<>() {
                    public String getLabel() {
                        return "Edit";
                    }

                    public String getIconPath() {
                        return PluginIcons.EDIT;
                    }

                    public void onAction(User user) {
                        EditUserDialog editUserDialog = new EditUserDialog(user);

                        editUserDialog.setOnUserEdited(tableOfContents::reload);
                        editUserDialog.showAndWait();
                    }

                    public Predicate<User> getCondition() {
                        return (_) -> true;
                    }
                }, new TableAction<>() {
                    public String getLabel() {
                        return "Delete";
                    }

                    public String getIconPath() {
                        return PluginIcons.DELETE;
                    }

                    public void onAction(User user) {
                        DeleteUserDialog deleteUserDialog = new DeleteUserDialog(user);

                        deleteUserDialog.setOnUserDeleted((_) -> tableOfContents.reload());
                        deleteUserDialog.showAndWait();
                    }

                    public Predicate<User> getCondition() {
                        return (user) -> user.getDeactivatedAt() == null;
                    }
                }));

        createButton.setOnAction(e -> {
            CreateUserDialog createUserDialog = new CreateUserDialog();

            createUserDialog.setOnUserCreated(_ -> tableOfContents.reload());

            createUserDialog.showAndWait();
        });

        mainContent.getChildren().addAll(header, tableOfContents);

        return mainContent;
    }

}
