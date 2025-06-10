package view.interfaces;

public interface WorkerRegisterView {
    String getLogin();
    String getPassword();
    String getConfirmPassword();
    String getKey();
    void showError(String message);
    void showSuccess(String message);
    void navigateToWorkerAuth();
    void close();
}