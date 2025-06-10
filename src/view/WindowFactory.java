package view;

import view.interfaces.*;

public class WindowFactory {
    public static AuthView createAuthView() {
        return new AuthWindow();
    }

    public static RegisterView createRegisterView() {
        return new RegisterWindow();
    }

    public static WorkerAuthView createWorkerAuthView() {
        return new WorkerAuthWindow();
    }

    public static WorkerRegisterView createWorkerRegisterView() {
        return new WorkerRegisterWindow();
    }

    public static UserMainView createUserMainView(String username, int userId) {
        return new UserMainWindow(username, userId);
    }

    public static WorkerMainView createWorkerMainView(String username) {
        return new WorkerMainWindow(username);
    }
}