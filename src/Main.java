import controllers.AuthController;
import repository.DatabaseManager;
import view.*;
import view.interfaces.*;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();

        AuthView authView = WindowFactory.createAuthView();
        RegisterView registerView = WindowFactory.createRegisterView();
        WorkerAuthView workerAuthView = WindowFactory.createWorkerAuthView();
        WorkerRegisterView workerRegisterView = WindowFactory.createWorkerRegisterView();
        new AuthController(
                authView,
                registerView,
                workerAuthView,
                workerRegisterView,
                dbManager
        );

        ((JFrame) authView).setVisible(true);
    }
}