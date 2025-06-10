package controllers;

import model.Car;
import repository.DatabaseManager;
import view.interfaces.WorkerMainView;
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
        view.close();
        view.navigateToAuth();
    }
}