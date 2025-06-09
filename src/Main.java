import controllers.AuthController;
import repository.DatabaseManager;
import view.AuthWindow;
import view.RegisterWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AuthWindow authWindow = new AuthWindow();
            RegisterWindow registerWindow = new RegisterWindow();

            // Создаём DatabaseManager один раз
            DatabaseManager dbManager = new DatabaseManager();

            // Передаём dbManager в контроллер
            new AuthController(authWindow, registerWindow, dbManager);

            authWindow.setVisible(true);
        });
    }
}