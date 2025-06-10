package controllers;

import repository.DatabaseManager;
import view.*;
import view.interfaces.AuthView;
import view.interfaces.RegisterView;
import view.interfaces.WorkerAuthView;
import view.interfaces.WorkerRegisterView;

import javax.swing.*;

public class AuthController {
    private final AuthView authView;
    private final RegisterView registerView;
    private final WorkerAuthView workerAuthView;
    private final WorkerRegisterView workerRegisterView;
    private final DatabaseManager databaseManager;

    public AuthController(AuthView authView, RegisterView registerView,
                          WorkerAuthView workerAuthView, WorkerRegisterView workerRegisterView,
                          DatabaseManager dbManager) {
        this.authView = authView;
        this.registerView = registerView;
        this.workerAuthView = workerAuthView;
        this.workerRegisterView = workerRegisterView;
        this.databaseManager = dbManager;
        initControllers();
    }

    private void initControllers() {
        // Обработка кнопок AuthWindow
        ((AuthWindow) authView).getLoginButton().addActionListener(e -> handleLogin());
        ((AuthWindow) authView).getRegisterButton().addActionListener(e ->{
            ((JFrame) authView).setVisible(false);
            ((JFrame) registerView).setVisible(true);
        });
        ((AuthWindow) authView).getWorkerButton().addActionListener(e ->{
            ((JFrame) authView).setVisible(false);
            ((JFrame) workerAuthView).setVisible(true);
        });
        ((AuthWindow) authView).getExitButton().addActionListener(e -> System.exit(0));

        // Обработка кнопок RegisterWindow
        ((RegisterWindow) registerView).getRegisterButton().addActionListener(e -> handleRegistration());
        ((RegisterWindow) registerView).getBackButton().addActionListener(e -> {
            ((JFrame) registerView).setVisible(false);
            ((JFrame) authView).setVisible(true);
        });

        // Обработка кнопок WorkerAuthWindow
        ((WorkerAuthWindow) workerAuthView).getLoginButton().addActionListener(e -> handleWorkerLogin());
        ((WorkerAuthWindow) workerAuthView).getRegisterButton().addActionListener(e -> {
            ((JFrame) workerAuthView).setVisible(false);
            ((JFrame) workerRegisterView).setVisible(true);
        });
        ((WorkerAuthWindow) workerAuthView).getBackButton().addActionListener(e -> {
            ((JFrame) workerAuthView).setVisible(false);
            ((JFrame) authView).setVisible(true);
        });
        ((WorkerAuthWindow) workerAuthView).getExitButton().addActionListener(e -> System.exit(0));

        // Обработка кнопок WorkerRegisterWindow
        ((WorkerRegisterWindow) workerRegisterView).getRegisterButton().addActionListener(e -> handleWorkerRegistration());
        ((WorkerRegisterWindow) workerRegisterView).getBackButton().addActionListener(e -> {
            ((JFrame) workerRegisterView).setVisible(false);
            ((JFrame) workerAuthView).setVisible(true);
        });
    }

    private void handleLogin() {
        String login = authView.getLogin();
        String password = authView.getPassword();

        if (databaseManager.validateUser(login, password)) {
            int userId = databaseManager.getUserId(login);
            if (userId != -1) {
                authView.navigateToMainWindow(login, userId);
            } else {
                authView.showError("Ошибка получения данных пользователя");
            }
        } else {
            authView.showError("Неверные учетные данные");
        }
    }

    private void handleRegistration() {
        String login = registerView.getLogin();
        String password = registerView.getPassword();
        String confirmPassword = registerView.getConfirmPassword();

        if (!password.equals(confirmPassword)) {
            registerView.showError("Пароли не совпадают");
            return;
        }

        if (password.isEmpty() || login.isEmpty()) {
            registerView.showError("Пароль или логин не могут быть пустыми");
            return;
        }

        if (databaseManager.registerUser(login, password)) {
            registerView.showSuccess("Регистрация успешна!");
            ((JFrame) authView).setVisible(true);
            ((JFrame) registerView).setVisible(false);
        } else {
            registerView.showError("Ошибка регистрации. Проверьте логин и пароль.");
        }
    }


    private void handleWorkerLogin() {
        String login = workerAuthView.getLogin();
        String password = workerAuthView.getPassword();
        String key = workerAuthView.getKey();

        if (databaseManager.validateWorker(login, password, key)) {
            int userId = databaseManager.getUserId(login);
            workerAuthView.navigateToMainWindow(login, userId);
        } else {
            workerAuthView.showError("Неверные учетные данные");
        }
    }

    private void handleWorkerRegistration() {
        String login = workerRegisterView.getLogin();
        String password = workerRegisterView.getPassword();
        String confirmPassword = workerRegisterView.getConfirmPassword();
        String key = workerRegisterView.getKey();

        if (!password.equals(confirmPassword)) {
            workerRegisterView.showError("Пароли не совпадают");
            return;
        }

        if (databaseManager.registerWorker(login, password, key)) {
            workerRegisterView.showSuccess("Регистрация успешна!");
            ((JFrame) workerAuthView).setVisible(true);
            ((JFrame) workerRegisterView).setVisible(false);
        } else {
            workerRegisterView.showError("Ошибка регистрации. Проверьте логин, пароль и ключ доступа.");
        }
    }
}