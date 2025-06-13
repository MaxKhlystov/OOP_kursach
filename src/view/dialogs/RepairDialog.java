package view.dialogs;

import model.Car;
import model.User;
import repository.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RepairDialog extends JDialog {
    private JComboBox<User> userComboBox;
    private JComboBox<Car> carComboBox;
    private JTextArea problemField;
    private JButton startRepairButton;
    private DatabaseManager dbManager;

    public interface AddRepairCallback {
        void onAddRepair(Car selectedCar, String problemDescription);
    }

    public RepairDialog(List<User> users, Map<User, List<Car>> userCars, AddRepairCallback callback) {
        this.dbManager = new DatabaseManager();
        setTitle("Добавление автомобиля на ремонт");
        setModal(true);
        setSize(400, 350); // Увеличим высоту для лучшего отображения
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Собираем все автомобили (активные + архивные)
        Set<Car> allCars = new HashSet<>();
        for (List<Car> cars : userCars.values()) {
            allCars.addAll(cars);
        }

        // Добавляем архивные автомобили
        List<Car> archivedCars = dbManager.getArchivedRepairs();
        allCars.addAll(archivedCars);

        // 2. Настройка компонентов
        userComboBox = new JComboBox<>(users.toArray(new User[0]));
        carComboBox = new JComboBox<>();
        problemField = new JTextArea(5, 20);
        startRepairButton = new JButton("Начать ремонт");

        // 3. Обработчик выбора пользователя
        userComboBox.addActionListener(e -> updateCarComboBox(userCars));

        // 4. Инициализация первоначального списка автомобилей
        updateCarComboBox(userCars);

        // 5. Создание формы
        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        form.add(new JLabel("Выберите пользователя:"));
        form.add(userComboBox);
        form.add(new JLabel("Выберите автомобиль:"));
        form.add(carComboBox);
        form.add(new JLabel("Описание проблемы:"));
        form.add(new JScrollPane(problemField));

        // 6. Кнопка с обработчиком
        startRepairButton.addActionListener(e -> {
            Car selectedCar = (Car) carComboBox.getSelectedItem();
            String description = problemField.getText().trim();

            if (selectedCar == null) {
                JOptionPane.showMessageDialog(this,
                        "Выберите автомобиль",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Введите описание проблемы",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Проверяем, не находится ли автомобиль уже в ремонте
            if (!dbManager.isCarArchived(selectedCar.getId()) &&
                    !"Нет статуса".equals(selectedCar.getStatus())) {
                JOptionPane.showMessageDialog(this,
                        "Этот автомобиль уже в ремонте",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            callback.onAddRepair(selectedCar, description);
            dispose();
        });

        add(form, BorderLayout.CENTER);
        add(startRepairButton, BorderLayout.SOUTH);
    }

    private void updateCarComboBox(Map<User, List<Car>> userCars) {
        User selectedUser = (User) userComboBox.getSelectedItem();
        if (selectedUser != null) {
            // Берем автомобили пользователя + архивные
            List<Car> userCarsList = userCars.getOrDefault(selectedUser, new ArrayList<>());
            List<Car> archivedCars = dbManager.getArchivedRepairsByOwner(selectedUser.getId());

            List<Car> allCars = new ArrayList<>();
            allCars.addAll(userCarsList);
            allCars.addAll(archivedCars);

            carComboBox.setModel(new DefaultComboBoxModel<>(allCars.toArray(new Car[0])));
        }
    }

    public static void showDialog(List<User> users, Map<User, List<Car>> userCars, AddRepairCallback callback) {
        RepairDialog dialog = new RepairDialog(users, userCars, callback);
        dialog.setVisible(true);
    }
}