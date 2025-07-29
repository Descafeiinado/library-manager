package br.edu.ifba.inf008.plugins.users.ui.providers;

import br.edu.ifba.inf008.core.ui.components.table.TableComponent;
import br.edu.ifba.inf008.core.ui.components.table.interfaces.TableAction;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import br.edu.ifba.inf008.plugins.users.ui.PluginIcons;
import br.edu.ifba.inf008.plugins.users.ui.views.DeleteUserDialog;
import br.edu.ifba.inf008.plugins.users.ui.views.EditUserDialog;
import br.edu.ifba.inf008.plugins.users.ui.views.ViewUserDialog;
import java.util.List;
import java.util.function.Predicate;

/**
 * Provides actions for the user table component.
 */
public class UserTableActionsProvider {

    /**
     * Returns a list of actions for the user table component. Which includes
     * view, edit, and delete actions.
     *
     * @param tableComponent the table component to which the actions will be added
     * @return a list of actions for the user table component
     */
    public static List<TableAction<User>> getActions(TableComponent<User> tableComponent) {
        return List.of(new TableAction<>() {
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
        }, new TableAction<>() {
            public String getLabel() {
                return "Edit";
            }

            public String getIconPath() {
                return PluginIcons.EDIT;
            }

            public void onAction(User user) {
                EditUserDialog editUserDialog = new EditUserDialog(user);

                editUserDialog.setOnUserEdited(tableComponent::reload);
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

                deleteUserDialog.setOnUserDeleted((_) -> tableComponent.reload());
                deleteUserDialog.showAndWait();
            }

            public Predicate<User> getCondition() {
                return (user) -> user.getDeactivatedAt() == null;
            }
        });
    }

}
