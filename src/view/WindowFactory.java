package view;

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
    public static MainView createMainView() {
        return new MainWindow();
    }
}