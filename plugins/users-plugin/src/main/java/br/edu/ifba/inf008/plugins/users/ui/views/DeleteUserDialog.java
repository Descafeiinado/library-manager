package br.edu.ifba.inf008.plugins.users.ui.views;

import br.edu.ifba.inf008.core.ui.views.GenericConfirmationDialogView;
import br.edu.ifba.inf008.plugins.users.domain.entities.User;
import br.edu.ifba.inf008.plugins.users.infrastructure.services.UserService;
import java.util.function.Consumer;

public class DeleteUserDialog extends GenericConfirmationDialogView {

    private static final UserService userService = UserService.getInstance();

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

    public void setOnUserDeleted(Consumer<User> onUserDeleted) {
        this.onUserDeleted = onUserDeleted;
    }

}
