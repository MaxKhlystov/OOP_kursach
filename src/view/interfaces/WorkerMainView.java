package view.interfaces;

import model.Car;
import model.User;
import java.awt.event.ActionListener;
import java.util.List;

public interface WorkerMainView {
    void setUsername(String username);

    void showAllCars(List<Car> activeCars, List<Car> archivedCars);
    void updateCarsTable();
    void clearSelection();
    void setViewTitle(String title);

    String showStatusChangeDialog(Car car, String[] statuses);
    void showError(String message);
    void showMessage(String message);
    void showProfileDialog(User user, ProfileCallback callback); // Добавлено

    void close();
    void navigateToAuth();

    void setLogoutListener(ActionListener listener);
    void setStatusChangeListener(ActionListener listener);
    void setRefreshListener(ActionListener listener);
    void setToggleViewListener(ActionListener listener);
    void setProfileListener(ActionListener listener);
    void setAddCarListener(ActionListener listener);

    int getSelectedCarRow();
    int getSelectedCarId();

    interface ProfileCallback {
        boolean processProfileInput(String fullName, String phone);
    }
}