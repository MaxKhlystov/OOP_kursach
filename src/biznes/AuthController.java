package biznes;

import repository.DatabaseManager;
import view.AuthWindow;
import view.RegisterWindow;
import view.MainWindow;
import javax.swing.*;

public class AuthController {
    private AuthWindow authWindow;
    private RegisterWindow registerWindow;
    private DatabaseManager databaseManager;

    public AuthController(AuthWindow authWindow, RegisterWindow registerWindow) {
        this.authWindow = authWindow;
        this.registerWindow = registerWindow;
        this.databaseManager = new DatabaseManager();
        initControllers();
        System.out.println("AuthController инициализирован");
    }

    private void initControllers() {
        // Обработчик входа
        authWindow.getLoginButton().addActionListener(e -> handleLogin());

        // Обработчик регистрации
        authWindow.getRegisterButton().addActionListener(e -> {
            authWindow.setVisible(false);
            registerWindow.setVisible(true);
        });

        // Обработчик возврата с регистрации
        registerWindow.getBackButton().addActionListener(e -> {
            registerWindow.setVisible(false);
            authWindow.setVisible(true);
        });

        // Обработчик подтверждения регистрации
        registerWindow.getRegisterButton().addActionListener(e -> handleRegistration());
    }

    private void handleLogin() {
        String login = authWindow.getLoginField().getText();
        String password = new String(authWindow.getPasswordField().getPassword());

        if (databaseManager.validateUser(login, password)) {
            authWindow.dispose();
            new MainWindow(login).setVisible(true); // Передаем логин в главное окно
        } else {
            JOptionPane.showMessageDialog(authWindow,
                    "Неверные учетные данные",
                    "Ошибка входа",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegistration() {
        String login = registerWindow.getLoginField().getText();
        String password = new String(registerWindow.getPasswordField().getPassword());
        String confirmPassword = new String(registerWindow.getConfirmPasswordField().getPassword());

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(registerWindow,
                    "Пароли не совпадают",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (databaseManager.registerUser(login, password)) {
            JOptionPane.showMessageDialog(registerWindow,
                    "Регистрация успешна!",
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE);
            registerWindow.dispose();
            authWindow.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(registerWindow,
                    "Логин уже занят",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}