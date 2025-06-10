package view.interfaces;

import model.Car;
import javax.swing.*;
import java.util.List;

public interface WorkerMainView {
    // Управление данными
    void setUsername(String username);

    // Управление UI
    void showAllCars(List<Car> cars);
    void updateCarsTable();
    void clearSelection();

    // Диалоговые окна
    void showStatusChangeDialog(Car car);
    void showError(String message);
    void showMessage(String message);

    // Навигация
    void close();
    void navigateToAuth();

    // Колбэки для контроллера
    void setLogoutListener(java.awt.event.ActionListener listener);
    void setStatusChangeListener(java.awt.event.ActionListener listener);
    void setRefreshListener(java.awt.event.ActionListener listener);
}