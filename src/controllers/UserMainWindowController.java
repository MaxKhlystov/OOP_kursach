package controllers;

import model.Car;
import repository.DatabaseManager;
import view.interfaces.UserMainView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class UserMainWindowController {
    private final UserMainView view;
    private final DatabaseManager databaseManager;
    private final String username;
    private final int userId;

    public UserMainWindowController(UserMainView view, String username, int userId) {
        this.view = view;
        this.databaseManager = new DatabaseManager(); // Инициализация здесь или через DI
        this.username = username;
        this.userId = userId;

        initController();
        loadUserCars();
    }

    private void initController() {
        // Установка слушателей через методы view
        view.setAddCarListener(this::handleAddCar);
        view.setLogoutListener(e -> handleLogout());

        // Инициализация интерфейса
        view.setUsername(username);
        view.setUserId(userId);
    }

    private void loadUserCars() {
        List<Car> userCars = databaseManager.getUserCars(userId);
        view.clearMainPanel();

        if (userCars.isEmpty()) {
            JLabel noCarsLabel = new JLabel("У вас нет автомобилей в ремонте");
            noCarsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            noCarsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            view.addToMainPanel(noCarsLabel);
        } else {
            for (Car car : userCars) {
                JPanel carCard = createCarCard(car);
                view.addToMainPanel(carCard);
            }
        }
        view.updateUI();
    }

    private JPanel createCarCard(Car car) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(150, 150));

        JLabel nameLabel = new JLabel(car.getName());
        JLabel plateLabel = new JLabel(car.getLicensePlate());
        JLabel statusLabel = new JLabel("Статус: В ремонте");

        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        plateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        plateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton detailsBtn = new JButton("Подробнее");
        JButton deleteBtn = new JButton("Удалить");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(detailsBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonPanel.add(deleteBtn);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(plateLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(statusLabel);
        card.add(Box.createVerticalGlue());
        card.add(buttonPanel);

        view.setShowDetailsListener(car, e -> showCarDetails(car));
        view.setDeleteCarListener(car, e -> deleteCar(car));

        return card;
    }

    private void handleAddCar(ActionEvent e) {
        view.showAddCarDialog();
        Car newCar = new Car("Новый автомобиль", "VIN123", "A123BC", userId, "Проблема");
        if (databaseManager.addCar(newCar) != null) {
            view.showMessage("Автомобиль добавлен на ремонт!");
            loadUserCars();
        } else {
            view.showError("Ошибка при добавлении автомобиля");
        }
    }

    private void showCarDetails(Car car) {
        view.showCarDetailsDialog(car);
    }

    private void deleteCar(Car car) {
        if (view.showConfirmDeleteDialog(car)) {
            if (databaseManager.deleteCar(car.getId(), userId)) {
                view.showMessage("Автомобиль удалён");
                loadUserCars();
            } else {
                view.showError("Ошибка при удалении автомобиля");
            }
        }
    }

    private void handleLogout() {
        String[] options = {"Выйти из аккаунта", "Выйти из приложения", "Отмена"};
        int choice = JOptionPane.showOptionDialog(
                (Component) view,
                "Выберите действие:",
                "Подтверждение выхода",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        switch (choice) {
            case 0:
                view.close();
                view.navigateToAuth();
                break;
            case 1:
                System.exit(0);
                break;
        }
    }
}