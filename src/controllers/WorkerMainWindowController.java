package controllers;

import model.Car;
import repository.DatabaseManager;
import view.interfaces.WorkerMainView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class WorkerMainWindowController {
    private final WorkerMainView view;
    private final DatabaseManager dbManager;

    public WorkerMainWindowController(WorkerMainView view) {
        this.view = view;
        this.dbManager = new DatabaseManager();
        initController();
        loadAllCars();
    }

    private void initController() {
        view.setLogoutListener(e -> handleLogout());
        view.setStatusChangeListener(e -> handleStatusChange());
        view.setRefreshListener(e -> loadAllCars());
    }

    private void loadAllCars() {
        List<Car> allCars = dbManager.getAllCars(); // Нужно добавить этот метод в DatabaseManager
        view.showAllCars(allCars);
    }

    private void handleStatusChange() {
        // Логика изменения статуса выбранного автомобиля
        view.showMessage("Статус изменен");
        loadAllCars();
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