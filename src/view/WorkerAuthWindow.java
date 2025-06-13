package view;

import controllers.WorkerMainWindowController;
import view.interfaces.WorkerAuthView;

import javax.swing.*;
import java.awt.*;

public class WorkerAuthWindow extends JFrame implements WorkerAuthView {
    private JLabel titleLabel;
    private JLabel loginLabel;
    private JLabel passwordLabel;
    private JLabel keyLabel;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JTextField keyField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton backButton;
    private JButton exitButton;

    public WorkerAuthWindow() {
        super("Авторизация сотрудника");
        initFrame();
        initComponents();
        buildUI();
    }

    private void initFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 400);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        titleLabel = new JLabel("Авторизация сотрудника", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        loginLabel = new JLabel("Логин:");
        passwordLabel = new JLabel("Пароль:");
        keyLabel = new JLabel("Ключ доступа:");
        loginField = new JTextField();
        passwordField = new JPasswordField();
        keyField = new JTextField();

        loginButton = new JButton("Вход");
        registerButton = new JButton("Регистрация");
        backButton = new JButton("Назад");
        exitButton = new JButton("Выход");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void buildUI() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        contentPanel.add(createInputPanel(loginLabel, loginField));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createInputPanel(passwordLabel, passwordField));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createInputPanel(keyLabel, keyField));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        contentPanel.add(loginButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(registerButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel bottomButtonPanel = new JPanel();
        bottomButtonPanel.setLayout(new BorderLayout());
        bottomButtonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(exitButton);
        bottomButtonPanel.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(backButton);
        bottomButtonPanel.add(rightPanel, BorderLayout.EAST);

        contentPanel.add(bottomButtonPanel);

        add(titleLabel);
        add(contentPanel);
    }

    private JPanel createInputPanel(JLabel label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return panel;
    }

    // Реализация методов интерфейса WorkerAuthView
    @Override
    public String getLogin() {
        return loginField.getText();
    }

    @Override
    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    @Override
    public String getKey() {
        return keyField.getText();
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void navigateToMainWindow(String login, int userId) {
        this.setVisible(false);
        WorkerMainWindow workerMainWindow = new WorkerMainWindow(login);
        new WorkerMainWindowController(workerMainWindow, login);
        workerMainWindow.setVisible(true);
    }

    @Override
    public void navigateToWorkerRegister() {
        this.setVisible(false);
        for (Window window : Window.getWindows()) {
            if (window instanceof WorkerRegisterWindow) {
                window.setVisible(true); // Показываем существующее окно
                return;
            }
        }
    }

    @Override
    public void navigateToUserAuth() {
        this.setVisible(false);
        for (Window window : Window.getWindows()) {
            if (window instanceof AuthWindow) {
                window.setVisible(true); // Показываем существующее окно
                return;
            }
        }
    }

    @Override
    public void close() {
        this.dispose();
    }

    public JButton getLoginButton() { return loginButton; }
    public JButton getRegisterButton() { return registerButton; }
    public JButton getBackButton() { return backButton; }
    public JButton getExitButton() { return exitButton; }
}