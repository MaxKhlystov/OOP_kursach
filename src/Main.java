import view.AuthWindow;
import view.RegisterWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AuthWindow authWindow = new AuthWindow();
            RegisterWindow registerWindow = new RegisterWindow();

            new biznes.AuthController(authWindow, registerWindow);

            authWindow.setVisible(true);
        });
    }
}