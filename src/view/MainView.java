package view;

import javax.swing.*;
import java.awt.*;

import model.Car;

public interface MainView {
    // Управление данными
    void setUsername(String username);
    void setUserId(int userId);

    // Управление UI
    void clearMainPanel();
    void addToMainPanel(Component component);
    void updateUI();

    // Диалоговые окна
    void showAddCarDialog();
    void showCarDetailsDialog(Car car);
    boolean showConfirmDeleteDialog(Car car);
    void showError(String message);
    void showMessage(String message);

    // Навигация
    void close();
    void navigateToAuth();

    // Геттеры для кнопок
    JButton getAddCarButton();
    JButton getLogoutButton();

    // Колбэки для контроллера
    void setAddCarListener(java.awt.event.ActionListener listener);
    void setLogoutListener(java.awt.event.ActionListener listener);
    void setDeleteCarListener(Car car, java.awt.event.ActionListener listener);
    void setShowDetailsListener(Car car, java.awt.event.ActionListener listener);
}