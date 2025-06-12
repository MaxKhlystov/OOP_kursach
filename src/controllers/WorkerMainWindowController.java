package controllers;

import model.Car;
import model.User;
import repository.DatabaseManager;
import view.interfaces.WorkerMainView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class WorkerMainWindowController {
    private final WorkerMainView view;
    private final DatabaseManager dbManager;
    private boolean showInProgressCars = true;

    public WorkerMainWindowController(WorkerMainView view) {
        this.view = view;
        this.dbManager = new DatabaseManager();
        initController();
        loadCars();
    }

    private void initController() {
        view.setLogoutListener(e -> handleLogout());
        view.setStatusChangeListener(e -> handleStatusChange());
        view.setRefreshListener(e -> loadCars());
        view.setToggleViewListener(e -> toggleView());
    }

    private void toggleView() {
        showInProgressCars = !showInProgressCars;
        loadCars();
    }

    private void loadCars() {
        List<Car> cars;
        if (showInProgressCars) {
            cars = dbManager.getCarsByStatus("В ремонте");
        } else {
            cars = dbManager.getCarsByStatus("Ремонт выполнен");
        }

        view.showAllCars(cars);
    }

    private void handleStatusChange() {
        int selectedRow = view.getSelectedCarRow();
        if (selectedRow == -1) {
            view.showError("Выберите автомобиль для изменения статуса");
            return;
        }

        int carId = view.getSelectedCarId();
        Car car = dbManager.getCarById(carId);
        if (car == null) {
            view.showError("Автомобиль не найден");
            return;
        }

        String[] statuses = {"В ремонте", "Ремонт выполнен"};
        String newStatus = view.showStatusChangeDialog(car, statuses);

        if (newStatus != null && !newStatus.equals(car.getStatus())) {
            if (dbManager.updateCarStatus(carId, newStatus)) {
                view.showMessage("Статус автомобиля успешно изменен");

                if ("Ремонт выполнен".equals(newStatus)) {
                    notifyOwner(car.getOwnerId());
                }

                loadCars();
            } else {
                view.showError("Ошибка при изменении статуса");
            }
        }
    }

    private void notifyOwner(int ownerId) {
        User owner = dbManager.getUserById(ownerId);
        if (owner != null && owner.getPhone() != null && !owner.getPhone().isEmpty()) {
            // Здесь можно реализовать отправку SMS или email уведомления
            String message = String.format(
                    "Уважаемый %s, ваш автомобиль готов к выдаче. Статус: Ремонт выполнен.",
                    owner.getFullName()
            );
            System.out.println("Уведомление отправлено владельцу: " + owner.getFullName() +
                    ", телефон: " + owner.getPhone() + ", сообщение: " + message);
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