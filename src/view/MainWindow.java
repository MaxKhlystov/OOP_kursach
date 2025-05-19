package view;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private String username;
    private int userId;
    private JPanel mainPanel;
    private JButton addCarButton;
    private JButton logoutButton;

    public MainWindow(String username, int userId) {
        super("Главное окно");
        this.username = username;
        this.userId = userId;
        initWindow();
        initComponents();
    }

    private void initWindow() {
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/media/bmw.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Не удалось загрузить иконку: " + e.getMessage());
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Добро пожаловать, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        logoutButton = new JButton("Выйти");
        topPanel.add(logoutButton, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(topPanel, BorderLayout.NORTH);

        // Main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // Add car button
        addCarButton = new JButton("Добавить автомобиль на ремонт");
        addCarButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(new JLabel("Технический сервис © 2023"));
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void clearMainPanel() {
        mainPanel.removeAll();
    }

    public void addToMainPanel(Component component) {
        mainPanel.add(component);
    }

    public void updateUI() {
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public JButton getAddCarButton() {
        return addCarButton;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }
}