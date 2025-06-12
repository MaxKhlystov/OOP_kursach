package view;

import model.Car;
import model.User;
import repository.DatabaseManager;
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
    private JButton toggleViewButton;
    private JLabel statusLabel;

    public WorkerMainWindow(String username) {
        super("Панель сотрудника");
        initWindow(username);
        initComponents();
    }

    private void initWindow(String username) {
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel headerPanel = new JPanel(new BorderLayout());

        // Заголовок с именем пользователя
        JLabel titleLabel = new JLabel("Сотрудник: " + username, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        statusLabel = new JLabel("Автомобили в ремонте", SwingConstants.RIGHT);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
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
        toggleViewButton = new JButton("Показать выполненные");
        logoutButton = new JButton("Выйти");

        buttonPanel.add(changeStatusButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(toggleViewButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void showAllCars(List<Car> cars) {
        String[] columnNames = {"ID", "Марка", "VIN", "Гос. номер", "Владелец", "Телефон", "Статус", "Описание проблемы"};
        Object[][] data = new Object[cars.size()][8];

        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            User owner = new DatabaseManager().getUserById(car.getOwnerId());
            String ownerName = (owner != null && owner.getFullName() != null) ? owner.getFullName() : "Не указано";
            String ownerPhone = (owner != null && owner.getPhone() != null) ? owner.getPhone() : "Не указан";

            data[i][0] = car.getId();
            data[i][1] = car.getName();
            data[i][2] = car.getVin();
            data[i][3] = car.getLicensePlate();
            data[i][4] = ownerName;
            data[i][5] = ownerPhone;
            data[i][6] = car.getStatus();
            data[i][7] = car.getProblemDescription();
        }

        carsTable.setModel(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    @Override
    public String showStatusChangeDialog(Car car, String[] statuses) {
        return (String) JOptionPane.showInputDialog(
                this,
                "Автомобиль: " + car.getName() + "\nТекущий статус: " + car.getStatus() +
                        "\nВыберите новый статус:",
                "Изменение статуса",
                JOptionPane.PLAIN_MESSAGE,
                null,
                statuses,
                car.getStatus()
        );
    }

    @Override
    public int getSelectedCarRow() {
        return carsTable.getSelectedRow();
    }

    @Override
    public int getSelectedCarId() {
        int row = carsTable.getSelectedRow();
        if (row >= 0) {
            return (int) carsTable.getModel().getValueAt(row, 0);
        }
        return -1;
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
    public void setToggleViewListener(java.awt.event.ActionListener listener) {
        toggleViewButton.addActionListener(listener);
    }

    @Override
    public void setViewTitle(String title) {
        statusLabel.setText(title);
        toggleViewButton.setText(title.contains("ремонте") ?
                "Показать выполненные" : "Показать в ремонте");
    }

    @Override
    public void close() {
        this.dispose();
    }

    @Override
    public void navigateToAuth() {
        this.dispose();
        for (Window window : Window.getWindows()) {
            if (window instanceof WorkerAuthWindow) {
                window.setVisible(true);
                return;
            }
        }
    }
}