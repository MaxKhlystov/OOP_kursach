package biznes;

import repository.DatabaseManager;
import view.*;

import javax.swing.*;

public class AuthController {
    private AuthWindow authWindow;
    private RegisterWindow registerWindow;
    private DatabaseManager databaseManager;
    private WorkerAuthWindow workerAuthWindow;
    private WorkerRegisterWindow workerRegisterWindow;

    public AuthController(AuthWindow authWindow, RegisterWindow registerWindow) {
        this.authWindow = authWindow;
        this.registerWindow = registerWindow;
        this.databaseManager = new DatabaseManager();
        this.workerAuthWindow = new WorkerAuthWindow();
        this.workerRegisterWindow = new WorkerRegisterWindow();
        initControllers();
        System.out.println("AuthController инициализирован");
    }

    private void initControllers() {
        authWindow.getLoginButton().addActionListener(e -> handleLogin());
        authWindow.getExitButton().addActionListener(e -> System.exit(0));

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
            new MainWindow(login).setVisible(true); // Открываем главное окно для сотрудника
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
            authWindow.dispose();
            new MainWindow(login).setVisible(true);
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