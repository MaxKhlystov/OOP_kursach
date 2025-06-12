package view.interfaces;

import model.Car;
import model.User;
import java.awt.event.ActionListener;

public interface UserMainView {

    // Управление UI
    void clearMainPanel();
    void addCarCard(Car car, ActionListener detailsAction, ActionListener editAction, ActionListener deleteAction);
    void showNoCarsMessage();
    void updateUI();
    void showNotification(String message, boolean isWarning);
    void clearNotifications();

    // Диалоги
    void showAddCarDialog(CarDialogCallback callback);
    void showEditCarDialog(Car car, CarDialogCallback callback);
    void showCarDetailsDialog(Car car);
    void showDeleteConfirmation(String carName, ConfirmationCallback callback);
    void showLogoutOptions(LogoutCallback callback);
    void showProfileDialog(User user, ProfileCallback callback);
    void showDeleteAccountConfirmation(ConfirmationCallback callback);


    void showError(String message);
    void showMessage(String message);

    void close();
    void navigateToAuth();

    void setAddCarListener(ActionListener listener);
    void setLogoutListener(ActionListener listener);
    void setProfileListener(ActionListener listener);

    void highlightVinField(boolean highlight);
    void highlightLicensePlateField(boolean highlight);

    @FunctionalInterface
    interface CarDialogCallback {
        boolean processInput(String name, String vin, String plate, String imagePath);
    }

    @FunctionalInterface
    interface ConfirmationCallback {
        void onResult(boolean confirmed);
    }

    @FunctionalInterface
    interface LogoutCallback {
        void onChoice(int choice);
    }

    public interface ProfileCallback {
        boolean processProfileInput(String fullName, String phone);
        void onAccountDeleteRequested(); // добавить этот метод
    }

}