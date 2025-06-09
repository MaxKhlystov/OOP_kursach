package view;

public interface AuthView {
    String getLogin();
    String getPassword();
    void showError(String message);
    void navigateToMainWindow(String login, int userId);
    void navigateToRegister();
    void navigateToWorkerAuth();
    void close();
}