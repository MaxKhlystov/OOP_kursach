package view;

import javax.swing.*;
import java.awt.*;

public class WorkerRegisterWindow extends JFrame implements WorkerRegisterView {
    private JLabel titleLabel;
    private JLabel loginLabel;
    private JLabel passwordLabel;
    private JLabel confirmPasswordLabel;
    private JLabel keyLabel;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField keyField;
    private JButton registerButton;
    private JButton backButton;

    public WorkerRegisterWindow() {
        super("Регистрация сотрудника");
        initFrame();
        initComponents();
        buildUI();
    }

    private void initFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 370);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        titleLabel = new JLabel("Регистрация сотрудника", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        loginLabel = new JLabel("Логин:");
        passwordLabel = new JLabel("Пароль:");
        confirmPasswordLabel = new JLabel("Повторите пароль:");
        keyLabel = new JLabel("Ключ доступа:");
        loginField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();
        keyField = new JTextField();

        registerButton = new JButton("Зарегистрироваться");
        backButton = new JButton("Назад");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void buildUI() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        contentPanel.add(createInputPanel(loginLabel, loginField));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createInputPanel(passwordLabel, passwordField));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createInputPanel(confirmPasswordLabel, confirmPasswordField));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createInputPanel(keyLabel, keyField));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        contentPanel.add(registerButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(backButton);

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

    // Реализация методов интерфейса WorkerRegisterView
    @Override
    public String getLogin() {
        return loginField.getText();
    }

    @Override
    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    @Override
    public String getConfirmPassword() {
        return new String(confirmPasswordField.getPassword());
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
    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Успех", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void navigateToWorkerAuth() {
        this.setVisible(false);
    }

    @Override
    public void close() {
        this.dispose();
    }

    // Геттеры для кнопок
    public JButton getRegisterButton() { return registerButton; }
    public JButton getBackButton() { return backButton; }
}