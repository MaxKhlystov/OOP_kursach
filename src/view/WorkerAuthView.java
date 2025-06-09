package view;

public interface WorkerAuthView {
    String getLogin();
    String getPassword();
    String getKey();
    void showError(String message);
    void navigateToMainWindow(String login, int userId);
    void navigateToWorkerRegister();
    void navigateToAuth();
    void close();
}