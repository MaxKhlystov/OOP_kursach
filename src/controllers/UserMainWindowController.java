package controllers;

import model.Car;
import model.User;
import repository.DatabaseManager;
import view.interfaces.UserMainView;

import java.awt.event.ActionEvent;
import java.util.List;

public class UserMainWindowController {
    private final UserMainView view;
    private final DatabaseManager databaseManager;
    private final int userId;
    private User currentUser;

    public UserMainWindowController(UserMainView view, String username, int userId) {
        this.view = view;
        this.databaseManager = new DatabaseManager();
        this.userId = userId;
        this.currentUser = databaseManager.getUserById(userId);

        initController();
        loadUserCars();
        checkProfileCompleteness();
    }

    private void initController() {
        view.setAddCarListener(this::handleAddCar);
        view.setLogoutListener(this::handleLogout);
        view.setProfileListener(this::handleProfile);
    }

    private void checkProfileCompleteness() {
        view.clearNotifications();

        if (currentUser.getFullName() == null || currentUser.getFullName().trim().isEmpty()) {
            view.showNotification("Пожалуйста, заполните ваше ФИО в личном кабинете", true);
        }

        if (currentUser.getPhone() == null || currentUser.getPhone().trim().isEmpty()) {
            view.showNotification("Пожалуйста, заполните ваш номер телефона в личном кабинете", true);
        }
        List<Car> userCars = databaseManager.getUserCars(userId);
        if (userCars.isEmpty()) {
            view.showNotification("У вас нет ни одного автомобиля. Добавьте автомобиль через меню 'Автомобиль'", false);
        }
    }

    private void handleProfile(ActionEvent e) {
        view.showProfileDialog(currentUser, (fullName, phone) -> {
            // Эта проверка теперь дублируется в showProfileDialog, можно убрать
            boolean success = databaseManager.updateUserProfile(userId, fullName, phone);
            if (success) {
                currentUser.setFullName(fullName);
                currentUser.setPhone(phone);
                view.showMessage("Профиль успешно обновлен");
                loadUserCars(); // Это обновит уведомления
                return true;
            } else {
                view.showError("Ошибка при обновлении профиля");
                return false;
            }
        });
    }

    private void loadUserCars() {
        List<Car> userCars = databaseManager.getUserCars(userId);
        view.clearMainPanel();
        view.clearNotifications();

        // Сначала проверяем заполненность профиля
        checkProfileCompleteness();

        // Затем проверяем наличие автомобилей
        if (userCars.isEmpty()) {
            view.showNoCarsMessage();
        } else {
            for (Car car : userCars) {
                view.addCarCard(car,
                        e -> view.showCarDetailsDialog(car),
                        e -> handleEditCar(car),
                        e -> handleDeleteCar(car));
            }
        }
    }

    private void handleAddCar(ActionEvent e) {
        view.showAddCarDialog((name, vin, plate, problem, imagePath) -> {
            boolean hasError = false;

            // Сброс выделения перед валидацией
            view.highlightVinField(false);
            view.highlightLicensePlateField(false);

            if (name.isEmpty() || vin.isEmpty() || plate.isEmpty()) {
                view.showError("Все поля должны быть заполнены!");
                return false;
            }

            if (imagePath == null || imagePath.isEmpty()) {
                imagePath = "default.png";
            }

            for (Car existingCar : databaseManager.getAllCars()) {
                if (existingCar.getVin().equalsIgnoreCase(vin)) {
                    view.showError("Автомобиль с таким VIN уже существует.");
                    view.highlightVinField(true);
                    hasError = true;
                }
                if (existingCar.getLicensePlate().equalsIgnoreCase(plate)) {
                    view.showError("Автомобиль с таким гос. номером уже существует.");
                    view.highlightLicensePlateField(true);
                    hasError = true;
                }
            }

            if (hasError) {
                return false; // Не закрываем диалог
            }

            Car newCar = new Car(name, vin, plate, userId, problem, imagePath);
            boolean success = databaseManager.addCar(newCar) != null;
            if (success) {
                loadUserCars();
            }
            return success;
        });
    }

    private void handleEditCar(Car car) {
        view.showEditCarDialog(car, (name, vin, plate, problem, imagePath) -> {
            if (name.isEmpty() || vin.isEmpty() || plate.isEmpty() || problem.isEmpty()) {
                view.showError("Все поля должны быть заполнены!");
                return false;
            }

            car.setName(name);
            car.setVin(vin);
            car.setLicensePlate(plate);
            car.setProblemDescription(problem);
            car.setImagePath(imagePath); // Обновляем путь к изображению

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