package view.interfaces;

import model.Car;
import java.awt.event.ActionListener;
import java.util.List;

public interface WorkerMainView {
    void setUsername(String username);

    // Управление UI
    void showAllCars(List<Car> cars);
    void updateCarsTable();
    void clearSelection();
    void setViewTitle(String title);

    // Диалоговые окна
    String showStatusChangeDialog(Car car, String[] statuses);
    void showError(String message);
    void showMessage(String message);

    // Навигация
    void close();
    void navigateToAuth();

    // Слушатели
    void setLogoutListener(ActionListener listener);
    void setStatusChangeListener(ActionListener listener);
    void setRefreshListener(ActionListener listener);
    void setToggleViewListener(ActionListener listener);

    // Получение выбранных данных
    int getSelectedCarRow();
    int getSelectedCarId();
}