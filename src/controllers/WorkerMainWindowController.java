package controllers;

import model.Car;
import model.User;
import repository.DatabaseManager;
import view.dialogs.CompleteRepairDialog;
import view.dialogs.RepairDialog;
import view.interfaces.WorkerMainView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.*;

public class WorkerMainWindowController {
    private final WorkerMainView view;
    private final DatabaseManager dbManager;
    private User currentWorker;

    public WorkerMainWindowController(WorkerMainView view, String username) {
        this.view = view;
        this.dbManager = new DatabaseManager();
        this.currentWorker = new User(-1, username, "Сотрудник", "");
        initController();
        loadAllCars();
    }

    private void initController() {
        view.setLogoutListener(this::handleLogout);
        view.setStatusChangeListener(this::handleStatusChange);
        view.setRefreshListener(e -> loadAllCars());
        view.setProfileListener(this::handleProfile);
        view.setAddCarListener(this::handleAddCar);
    }

    private void handleProfile(ActionEvent e) {
        view.showProfileDialog(currentWorker, (fullName, phone) -> {
            currentWorker.setFullName(fullName);
            currentWorker.setPhone(phone);
            view.showMessage("Данные сотрудника обновлены");
            return true;
        });
    }

    private void loadAllCars() {
        List<Car> activeCars = dbManager.getAllCars();
        List<Car> archivedCars = dbManager.getArchivedRepairs();
        view.showAllCars(activeCars, archivedCars);
    }

    private void handleStatusChange(ActionEvent e) {
        int carId = view.getSelectedCarId();
        if (carId == -1) {
            view.showError("Выберите автомобиль из таблицы");
            return;
        }
        Car selectedCar = dbManager.getCarById(carId);
        if (selectedCar == null) {
            view.showError("Автомобиль не найден");
            return;
        }
        if ("Ремонт выполнен".equals(selectedCar.getStatus())) {
            view.showMessage("Ремонт уже завершен");
            return;
        }
        if ("В ремонте".equals(selectedCar.getStatus())) {
            CompleteRepairDialog.showDialog(
                    Collections.singletonList(selectedCar),
                    (car, parts) -> {
                        try {
                            car.setEndRepairTime(LocalDateTime.now());
                            boolean updateSuccess = dbManager.completeCarRepair(car, parts);
                            if (updateSuccess) {
                                boolean archiveSuccess = dbManager.archiveCompletedRepair(car, parts);
                                if (archiveSuccess) {
                                    view.showMessage("Ремонт завершен и перемещен в архив");
                                    loadAllCars();
                                } else {
                                    view.showError("Ошибка при архивировании ремонта");
                                }
                            } else {
                                view.showError("Ошибка при обновлении статуса");
                            }
                        } catch (Exception ex) {
                            view.showError("Ошибка: " + ex.getMessage());
                        }
                    }
            );
        } else {
            String[] statuses = {"На диагностике", "В ремонте"};
            String newStatus = view.showStatusChangeDialog(selectedCar, statuses);
            if (newStatus != null && !newStatus.equals(selectedCar.getStatus())) {
                boolean success = dbManager.updateCarStatus(selectedCar.getId(), newStatus);
                if (success) {
                    view.showMessage("Статус обновлен");
                    loadAllCars();
                } else {
                    view.showError("Ошибка при обновлении статуса");
                }
            }
        }
    }

    private void handleAddCar(ActionEvent e) {
        List<User> users = dbManager.getAllUsers();
        Map<User, List<Car>> userCars = new HashMap<>();

        for (User user : users) {
            userCars.put(user, dbManager.getUserCars(user.getId()));
        }

        List<Car> archivedCars = dbManager.getArchivedRepairs();
        for (Car car : archivedCars) {
            User owner = dbManager.getUserById(car.getOwnerId());
            if (owner != null) {
                userCars.computeIfAbsent(owner, k -> new ArrayList<>()).add(car);
            }
        }

        RepairDialog.showDialog(users, userCars, (selectedCar, problem) -> {
            try {
                if (dbManager.isCarArchived(selectedCar.getId())) {
                    Car newCar = new Car(
                            selectedCar.getName(),
                            selectedCar.getVin(),
                            selectedCar.getLicensePlate(),
                            selectedCar.getOwnerId(),
                            selectedCar.getImagePath(),
                            "В ремонте"
                    );
                    newCar.setProblemDescription(problem);
                    newCar.setStartRepairTime(LocalDateTime.now());

                    if (dbManager.addCar(newCar) != null) {
                        view.showMessage("Автомобиль добавлен на ремонт");
                        loadAllCars();
                    } else {
                        view.showError("Ошибка при добавлении автомобиля");
                    }
                }
                else {
                    selectedCar.setProblemDescription(problem);
                    selectedCar.setStatus("В ремонте");
                    selectedCar.setStartRepairTime(LocalDateTime.now());

                    if (dbManager.updateCarRepairInfo(selectedCar)) {
                        view.showMessage("Автомобиль добавлен на ремонт");
                        loadAllCars();
                    } else {
                        view.showError("Ошибка при обновлении статуса");
                    }
                }
            } catch (Exception ex) {
                view.showError("Ошибка: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    private void notifyOwner(int ownerId) {
        User owner = dbManager.getUserById(ownerId);
        if (owner != null && owner.getPhone() != null && !owner.getPhone().isEmpty()) {
            String message = String.format(
                    "Уважаемый %s, ваш автомобиль готов к выдаче. Статус: Ремонт выполнен.",
                    owner.getFullName()
            );
            System.out.println("Уведомление отправлено владельцу: " + owner.getFullName() +
                    ", телефон: " + owner.getPhone() + ", сообщение: " + message);
        }
    }

    private void handleLogout(ActionEvent e) {
        int choice = JOptionPane.showOptionDialog(
                null,
                "Выберите действие:",
                "Подтверждение выхода",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Выйти в меню авторизации", "Выйти из программы"},
                "Выйти в меню авторизации"
        );

        if (choice == 0) {
            view.close();
            view.navigateToAuth();
        } else if (choice == 1) {
            System.exit(0);
        }
    }
}