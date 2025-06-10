package controllers;

import model.Car;
import repository.DatabaseManager;
import view.interfaces.UserMainView;

import java.awt.event.ActionEvent;
import java.util.List;

public class UserMainWindowController {
    private final UserMainView view;
    private final DatabaseManager databaseManager;
    private final int userId;

    public UserMainWindowController(UserMainView view, String username, int userId) {
        this.view = view;
        this.databaseManager = new DatabaseManager();
        this.userId = userId;

        initController();
        loadUserCars();
    }

    private void initController() {
        view.setAddCarListener(this::handleAddCar);
        view.setLogoutListener(this::handleLogout);
    }

    private void loadUserCars() {
        List<Car> userCars = databaseManager.getUserCars(userId);
        view.clearMainPanel();

        if (userCars.isEmpty()) {
            view.showNoCarsMessage();
        } else {
            for (Car car : userCars) {
                view.addCarCard(car,
                        e -> showCarDetails(car),
                        e -> handleEditCar(car),
                        e -> handleDeleteCar(car));
            }
        }
    }

    private void handleAddCar(ActionEvent e) {
        view.showAddCarDialog((name, vin, plate, problem) -> {
            if (name.isEmpty() || vin.isEmpty() || plate.isEmpty()) {
                view.showError("Все поля должны быть заполнены!");
                return false;
            }

            Car newCar = new Car(name, vin, plate, userId, problem);
            boolean success = databaseManager.addCar(newCar) != null;
            if (success) {
                loadUserCars();
            }
            return success;
        });
    }

    private void handleEditCar(Car car) {
        view.showEditCarDialog(car, (name, vin, plate, problem) -> {
            if (name.isEmpty() || vin.isEmpty() || plate.isEmpty()) {
                view.showError("Все поля должны быть заполнены!");
                return false;
            }

            car.setName(name);
            car.setLicensePlate(plate);
            car.setProblemDescription(problem);
            boolean success = databaseManager.updateCar(car);
            if (success) {
                loadUserCars();
            }
            return success;
        });
    }

    private void showCarDetails(Car car) {
        view.showCarDetailsDialog(car);
    }

    private void handleDeleteCar(Car car) {
        view.showDeleteConfirmation(car.getName(), confirmed -> {
            if (confirmed) {
                boolean success = databaseManager.deleteCar(car.getId(), userId);
                if (success) {
                    loadUserCars();
                    view.showMessage("Автомобиль успешно удален");
                } else {
                    view.showError("Не удалось удалить автомобиль");
                }
            }
        });
    }

    private void handleLogout(ActionEvent e) {
        view.showLogoutOptions(choice -> {
            if (choice == 0) {
                view.close();
                view.navigateToAuth();
            } else if (choice == 1) {
                System.exit(0);
            }
        });
    }
}