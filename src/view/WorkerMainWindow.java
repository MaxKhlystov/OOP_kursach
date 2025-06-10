package view;

import model.Car;
import view.interfaces.WorkerMainView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class WorkerMainWindow extends JFrame implements WorkerMainView {
    private JTable carsTable;
    private JButton logoutButton;
    private JButton changeStatusButton;
    private JButton refreshButton;

    public WorkerMainWindow(String username) {
        super("Панель сотрудника");
        initWindow(username);
        initComponents();
    }

    private void initWindow(String username) {
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Заголовок с именем пользователя
        JLabel titleLabel = new JLabel("Сотрудник: " + username, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Таблица автомобилей
        carsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(carsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        changeStatusButton = new JButton("Изменить статус");
        refreshButton = new JButton("Обновить");
        logoutButton = new JButton("Выйти");

        buttonPanel.add(changeStatusButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void showAllCars(List<Car> cars) {
        String[] columnNames = {"ID", "Марка", "VIN", "Гос. номер", "Владелец", "Статус", "Описание проблемы"};
        Object[][] data = new Object[cars.size()][7];

        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            data[i][0] = car.getId();
            data[i][1] = car.getName();
            data[i][2] = car.getVin();
            data[i][3] = car.getLicensePlate();
            data[i][4] = car.getOwnerId();
            data[i][5] = "В ремонте"; // Статус по умолчанию
            data[i][6] = car.getProblemDescription();
        }

        carsTable.setModel(new DefaultTableModel(data, columnNames));
    }

    @Override
    public void updateCarsTable() {
        ((DefaultTableModel) carsTable.getModel()).fireTableDataChanged();
    }

    @Override
    public void clearSelection() {
        carsTable.clearSelection();
    }

    @Override
    public void showStatusChangeDialog(Car car) {
        String[] statuses = {"В ожидании", "В ремонте", "Готово", "Отменено"};
        String newStatus = (String) JOptionPane.showInputDialog(
                this,
                "Текущий статус: " + car.getProblemDescription() + "\nВыберите новый статус:",
                "Изменение статуса",
                JOptionPane.PLAIN_MESSAGE,
                null,
                statuses,
                statuses[0]
        );

        if (newStatus != null) {
            // Контроллер обработает изменение через listener
        }
    }

    @Override
    public void setUsername(String username) {
        setTitle("Панель сотрудника: " + username);
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Сообщение", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void setLogoutListener(java.awt.event.ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    @Override
    public void setStatusChangeListener(java.awt.event.ActionListener listener) {
        changeStatusButton.addActionListener(listener);
    }

    @Override
    public void setRefreshListener(java.awt.event.ActionListener listener) {
        refreshButton.addActionListener(listener);
    }

    @Override
    public void close() {
        this.dispose();
    }

    @Override
    public void navigateToAuth() {
        this.dispose();
    }
}