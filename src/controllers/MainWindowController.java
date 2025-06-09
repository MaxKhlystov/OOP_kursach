package controllers;

import model.Car;
import repository.DatabaseManager;
import view.AuthWindow;
import view.MainWindow;
import view.RegisterWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class MainWindowController {
    private MainWindow view;
    private int userId;
    private String username;
    private DatabaseManager databaseManager;

    public MainWindowController(MainWindow view, String username, int userId) {
        this.view = view;
        this.username = username;
        this.userId = userId;
        this.databaseManager = new DatabaseManager();
        initController();
    }

    private void initController() {
        view.getAddCarButton().addActionListener(this::handleAddCar);
        view.getLogoutButton().addActionListener(e -> handleLogout());
        buildCarsUI();
    }

    private void handleAddCar(ActionEvent e) {
        JDialog dialog = new JDialog(view, "Добавить автомобиль", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField brandField = new JTextField(15);
        JTextField modelField = new JTextField(15);
        JTextField plateField = new JTextField(10);
        JTextField vinField = new JTextField(17);
        JTextArea problemArea = new JTextArea(3, 20);
        problemArea.setLineWrap(true);

        addFormField(dialog, gbc, "Марка:", brandField, 0);
        addFormField(dialog, gbc, "Модель:", modelField, 1);
        addFormField(dialog, gbc, "Гос. номер:", plateField, 2);
        addFormField(dialog, gbc, "VIN-номер:", vinField, 3);
        addFormField(dialog, gbc, "Описание проблемы:", new JScrollPane(problemArea), 4);

        JButton submitButton = new JButton("Добавить");
        submitButton.addActionListener(ev -> {
            Car newCar = new Car(
                    brandField.getText() + " " + modelField.getText(),
                    vinField.getText(),
                    plateField.getText(),
                    userId,
                    problemArea.getText()
            );

            Car addedCar = DatabaseManager.addCar(newCar);
            if (addedCar != null) {
                JOptionPane.showMessageDialog(dialog, "Автомобиль добавлен на ремонт!");
                dialog.dispose();
                buildCarsUI();
            } else {
                JOptionPane.showMessageDialog(dialog, "Ошибка при добавлении автомобиля", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(ev -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }

    private void buildCarsUI() {
        view.clearMainPanel();

        List<Car> userCars = DatabaseManager.getUserCars(userId);
        if (userCars.isEmpty()) {
            JLabel noCarsLabel = new JLabel("У вас нет автомобилей в ремонте");
            noCarsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            noCarsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            view.addToMainPanel(noCarsLabel);
        } else {
            JPanel carsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            carsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            for (Car car : userCars) {
                carsPanel.add(createCarCard(car));
            }

            JScrollPane scrollPane = new JScrollPane(carsPanel);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            view.addToMainPanel(scrollPane);
        }

        view.addToMainPanel(view.getAddCarButton());
        view.updateUI();
    }

    private JPanel createCarCard(Car car) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(220, 150));

        // Создаем элементы перед использованием
        JLabel nameLabel = new JLabel(car.getName());
        JLabel plateLabel = new JLabel(car.getLicensePlate());
        JLabel statusLabel = new JLabel("Статус: В ремонте");
        JButton detailsBtn = new JButton("Подробнее");
        JButton deleteBtn = new JButton("Удалить");

        // Настраиваем выравнивание ПОСЛЕ создания элементов
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        plateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Настройка шрифтов
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        plateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Панель для кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(detailsBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonPanel.add(deleteBtn);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Добавляем элементы на карточку
        card.add(Box.createVerticalGlue());
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(plateLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(statusLabel);
        card.add(Box.createVerticalGlue());
        card.add(buttonPanel);

        // Добавляем обработчики
        detailsBtn.addActionListener(e -> showCarDetails(car));
        deleteBtn.addActionListener(e -> deleteCar(car));

        return card;
    }

    private void showCarDetails(Car car) {
        JDialog detailsDialog = new JDialog(view, "Подробности: " + car.getName(), true); // Добавляем название в заголовок
        detailsDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(30, 10, 30, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Остальной код метода остается без изменений
        addDetailRow(detailsDialog, gbc, "Марка и модель:", car.getName(), 0);
        addDetailRow(detailsDialog, gbc, "VIN номер:", car.getVin(), 1);
        addDetailRow(detailsDialog, gbc, "Гос. номер:", car.getLicensePlate(), 2);
        addDetailRow(detailsDialog, gbc, "Дата приёмки:", car.getCreatedAt().toString(), 3);
        addDetailRow(detailsDialog, gbc, "Описание проблемы:", car.getProblemDescription(), 4);

        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener(e -> detailsDialog.dispose());

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        detailsDialog.add(closeButton, gbc);

        detailsDialog.pack();
        detailsDialog.setLocationRelativeTo(view);
        detailsDialog.setVisible(true);
    }

    private void addDetailRow(JDialog dialog, GridBagConstraints gbc, String label, String value, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        JTextArea valueArea = new JTextArea(value);
        valueArea.setEditable(false);
        valueArea.setLineWrap(true);
        valueArea.setWrapStyleWord(true);
        valueArea.setBackground(dialog.getBackground());
        dialog.add(valueArea, gbc);
    }

    private void deleteCar(Car car) {
        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Вы уверены, что хотите удалить автомобиль " + car.getName() + "?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = DatabaseManager.deleteCar(car.getId(), userId);
            if (success) {
                JOptionPane.showMessageDialog(view, "Автомобиль удалён");
                buildCarsUI();
            } else {
                JOptionPane.showMessageDialog(view, "Ошибка при удалении автомобиля", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleLogout() {
        view.dispose();
        AuthWindow authWindow = new AuthWindow();
        RegisterWindow registerWindow = new RegisterWindow();
        new AuthController(authWindow, registerWindow, this.databaseManager);
        authWindow.setVisible(true);
    }

    private void addFormField(JDialog dialog, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        dialog.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        dialog.add(field, gbc);
    }
}