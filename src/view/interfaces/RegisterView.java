package view.interfaces;

public interface RegisterView {
    String getLogin();
    String getPassword();
    String getConfirmPassword();
    void showError(String message);
    void showSuccess(String message);
    void navigateToAuth();
    void close();
}