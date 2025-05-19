package controllers;

import repository.DatabaseManager;
import view.*;

import javax.swing.*;

public class AuthController {
    private AuthWindow authWindow;
    private RegisterWindow registerWindow;
    private WorkerAuthWindow workerAuthWindow;
    private WorkerRegisterWindow workerRegisterWindow;
    private DatabaseManager databaseManager;

    public AuthController(AuthWindow authWindow, RegisterWindow registerWindow, DatabaseManager dbManager) {
        this.authWindow = authWindow;
        this.registerWindow = registerWindow;
        this.databaseManager = dbManager;
        this.workerAuthWindow = new WorkerAuthWindow();
        this.workerRegisterWindow = new WorkerRegisterWindow();
        initControllers();
        System.out.println("AuthController инициализирован");
    }

    private void initControllers() {
        authWindow.getLoginButton().addActionListener(e -> handleLogin());
        authWindow.getExitButton().addActionListener(e -> System.exit(0));
        workerAuthWindow.getExitButton().addActionListener(e-> System.exit(0));

        workerRegisterWindow.getBackButton().addActionListener(e -> {
            workerRegisterWindow.setVisible(false);
            workerAuthWindow.setVisible(true);
        });

        workerRegisterWindow.getRegisterButton().addActionListener(e -> handleWorkerRegistration());

        authWindow.getWorkerButton().addActionListener(e -> {
            authWindow.setVisible(false);
            workerAuthWindow.setVisible(true);
        });

        authWindow.getRegisterButton().addActionListener(e -> {
            authWindow.setVisible(false);
            registerWindow.setVisible(true);
        });

        registerWindow.getBackButton().addActionListener(e -> {
            registerWindow.setVisible(false);
            authWindow.setVisible(true);
        });

        registerWindow.getRegisterButton().addActionListener(e -> handleRegistration());

        workerAuthWindow.getLoginButton().addActionListener(e -> handleWorkerLogin());
        workerAuthWindow.getBackButton().addActionListener(e -> {
            workerAuthWindow.setVisible(false);
            authWindow.setVisible(true);
        });
        workerAuthWindow.getRegisterButton().addActionListener(e -> {
            workerAuthWindow.setVisible(false);
            workerRegisterWindow.setVisible(true);
        });

        workerRegisterWindow.getBackButton().addActionListener(e -> {
            workerRegisterWindow.setVisible(false);
            workerAuthWindow.setVisible(true);
        });
    }
    //вход сотрудника
    private void handleWorkerLogin() {
        String login = workerAuthWindow.getLoginField().getText();
        String password = new String(workerAuthWindow.getPasswordField().getPassword());
        String key = workerAuthWindow.getKeyField().getText();

        if (databaseManager.validateWorker(login, password, key)) {
            workerAuthWindow.dispose();
            int userId = databaseManager.getUserId(login);
            MainWindow mainWindow = new MainWindow(login, userId);
            new MainWindowController(mainWindow, login, userId);
            mainWindow.setVisible(true); // Открываем главное окно для сотрудника
        } else {
            JOptionPane.showMessageDialog(workerAuthWindow,
                    "Неверные учетные данные",
                    "Ошибка входа сотрудника",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    //регистрация сотрудника
    private void handleWorkerRegistration() {
        String login = workerRegisterWindow.getLoginField().getText();
        String password = new String(workerRegisterWindow.getPasswordField().getPassword());
        String confirmPassword = new String(workerRegisterWindow.getConfirmPasswordField().getPassword());
        String key = workerRegisterWindow.getKeyField().getText();

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(workerRegisterWindow,
                    "Пароли не совпадают",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (databaseManager.registerWorker(login, password, key)) {
            JOptionPane.showMessageDialog(workerRegisterWindow,
                    "Регистрация успешна!",
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE);
            workerRegisterWindow.dispose();
            workerAuthWindow.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(workerRegisterWindow,
                    "Ошибка регистрации. Проверьте логин, пароль и ключ доступа.",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    //вход клиента
    private void handleLogin() {
        String login = authWindow.getLoginField().getText();
        String password = new String(authWindow.getPasswordField().getPassword());

        if (databaseManager.validateUser(login, password)) {
            int userId = databaseManager.getUserId(login); // Получаем ID
            if (userId != -1) {
                authWindow.dispose();
                new MainWindow(login, userId).setVisible(true); // Передаём ID
            } else {
                JOptionPane.showMessageDialog(authWindow,
                        "Ошибка получения данных пользователя",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(authWindow,
                    "Неверные учетные данные",
                    "Ошибка входа",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    //регистрация клиента
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
        if (password.isEmpty() || login.isEmpty()){
            JOptionPane.showMessageDialog(registerWindow,
                    "Пароль или логин не могут быть пустыми",
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
            JOptionPane.showMessageDialog(workerRegisterWindow,
                    "Ошибка регистрации. Проверьте логин, пароль и ключ доступа.",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}