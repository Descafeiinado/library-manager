package br.edu.ifba.inf008.plugins.users.ui.views;

import br.edu.ifba.inf008.core.ui.views.GenericConfirmationDialogView;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import br.edu.ifba.inf008.plugins.users.application.services.UserService;
import java.util.function.Consumer;

/**
 * Dialog for confirming the deletion of a user.
 * This dialog prompts the user to confirm the deletion of a specified user.
 * If confirmed, it deletes the user and triggers a callback if provided.
 */
public class DeleteUserDialog extends GenericConfirmationDialogView {

    private static final UserService userService = UserService.getInstance();

    /**
     * Callback to be executed when a user is successfully deleted.
     */
    private Consumer<User> onUserDeleted;

    public DeleteUserDialog(User user) {
        super("Delete User", "Are you sure you want to delete the user: " + user.getName() + "?");

        setOnConfirmedClick(() -> {
            try {
                userService.delete(user.getUserId());

                if (onUserDeleted != null) {
                    onUserDeleted.accept(user);
                }
            } catch (Exception e) {
                return;
            }

            close();
        });
    }

    /**
     * Sets the callback to be executed when a user is deleted.
     *
     * @param onUserDeleted the callback to set
     */
    public void setOnUserDeleted(Consumer<User> onUserDeleted) {
        this.onUserDeleted = onUserDeleted;
    }

}
