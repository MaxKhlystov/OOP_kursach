package view.interfaces;

import model.Car;
import java.awt.event.ActionListener;

public interface UserMainView {

    // Управление UI
    void clearMainPanel();
    void addCarCard(Car car, ActionListener detailsAction, ActionListener editAction, ActionListener deleteAction);
    void showNoCarsMessage();
    void updateUI();

    // Диалоги
    void showAddCarDialog(CarDialogCallback callback);
    void showEditCarDialog(Car car, CarDialogCallback callback);
    void showCarDetailsDialog(Car car);
    void showDeleteConfirmation(String carName, ConfirmationCallback callback);
    void showLogoutOptions(LogoutCallback callback);

    void showError(String message);
    void showMessage(String message);

    void close();
    void navigateToAuth();

    void setAddCarListener(ActionListener listener);
    void setLogoutListener(ActionListener listener);

    @FunctionalInterface
    interface CarDialogCallback {
        boolean processInput(String name, String vin, String plate, String problem, String imagePath);
    }

    @FunctionalInterface
    interface ConfirmationCallback {
        void onResult(boolean confirmed);
    }

    @FunctionalInterface
    interface LogoutCallback {
        void onChoice(int choice);
    }
}