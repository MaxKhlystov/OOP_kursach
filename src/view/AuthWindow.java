package view;

import javax.swing.*;
import java.awt.*;

public class AuthWindow extends JFrame {
    private JLabel titleLabel;
    private JLabel loginLabel;
    private JLabel passwordLabel;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton workerButton;
    private JButton exitButton;

    public AuthWindow() {
        super("Авторизация");
        initFrame();
        initComponents();
        buildUI();
    }

    private void initFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 350);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setLocationRelativeTo(null);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/media/bmw.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Не удалось загрузить иконку: " + e.getMessage());
        }
    }

    private void initComponents() {
        titleLabel = new JLabel("Technical Inspection", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 20));
        titleLabel.setForeground(new Color(0, 100, 200));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        loginLabel = new JLabel("Логин:");
        loginField = new JTextField();
        passwordLabel = new JLabel("Пароль:");
        passwordField = new JPasswordField();

        loginButton = new JButton("Вход");
        registerButton = new JButton("Регистрация");
        workerButton = new JButton("Я Сотрудник");
        exitButton = new JButton("Выход");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        workerButton.setAlignmentX(Component.ABORT);
        exitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void buildUI() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        contentPanel.add(createInputPanel(loginLabel, loginField));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createInputPanel(passwordLabel, passwordField));
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
        rightPanel.add(workerButton);
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

    // Геттеры
    public JButton getLoginButton() { return loginButton; }
    public JButton getRegisterButton() { return registerButton; }
    public JButton getExitButton(){return exitButton;}
    public JButton getWorkerButton(){return workerButton;}
    public JTextField getLoginField() { return loginField; }
    public JPasswordField getPasswordField() { return passwordField; }
}