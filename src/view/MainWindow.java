package view;

import javax.swing.*;
import java.awt.*;
import model.Car;

public class MainWindow extends JFrame implements MainView {
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
        mainPanel.add(addCarButton);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(new JLabel("Технический сервис © 2023"));
        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public void clearMainPanel() {
        mainPanel.removeAll();
        mainPanel.add(addCarButton); // Добавляем кнопку обратно после очистки
        updateUI();
    }

    @Override
    public void addToMainPanel(Component component) {
        mainPanel.add(component);
        updateUI();
    }

    @Override
    public void updateUI() {
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    @Override
    public void showAddCarDialog() {
        // Реализация диалога добавления автомобиля
        JOptionPane.showMessageDialog(this, "Диалог добавления автомобиля будет здесь");
    }

    @Override
    public void showCarDetailsDialog(Car car) {
        // Реализация диалога просмотра деталей
        JOptionPane.showMessageDialog(this, "Детали автомобиля: " + car.toString());
    }

    @Override
    public boolean showConfirmDeleteDialog(Car car) {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы уверены, что хотите удалить автомобиль " + car.getName() + "?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            // Контроллер обработает это через listener
        }
        return false;
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Сообщение", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void navigateToAuth() {
        this.dispose();
        AuthWindow authWindow = new AuthWindow();
        authWindow.setVisible(true);
    }

    @Override
    public JButton getAddCarButton() {
        return addCarButton;
    }

    @Override
    public JButton getLogoutButton() {
        return logoutButton;
    }

    @Override
    public void setAddCarListener(java.awt.event.ActionListener listener) {
        addCarButton.addActionListener(listener);
    }

    @Override
    public void setLogoutListener(java.awt.event.ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    @Override
    public void setDeleteCarListener(Car car, java.awt.event.ActionListener listener) {
        // Реализуется при создании кнопок удаления для каждого автомобиля
    }

    @Override
    public void setShowDetailsListener(Car car, java.awt.event.ActionListener listener) {
        // Реализуется при создании кнопок просмотра для каждого автомобиля
    }

    @Override
    public void close() {
        this.dispose();
    }
}