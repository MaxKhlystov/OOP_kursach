package view;

import model.Car;
import model.User;
import repository.DatabaseManager;
import view.interfaces.WorkerMainView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class WorkerMainWindow extends JFrame implements WorkerMainView {
    private JTable inProgressTable;
    private JTable completedTable;
    private JTable archiveTable;
    private JTabbedPane tabbedPane;
    private JMenuBar menuBar;

    public WorkerMainWindow(String username) {
        super("Панель сотрудника");
        initWindow(username);
        initComponents();
    }

    private void initWindow(String username) {
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        JMenuItem logoutItem = new JMenuItem("Выйти");
        fileMenu.add(logoutItem);

        JMenu userMenu = new JMenu("Пользователь");
        JMenuItem profileItem = new JMenuItem("Личный кабинет");
        userMenu.add(profileItem);

        JMenu carMenu = new JMenu("Автомобиль");
        JMenuItem addCarItem = new JMenuItem("Добавить автомобиль");
        JMenuItem changeStatusItem = new JMenuItem("Изменить статус");
        carMenu.add(addCarItem);
        carMenu.add(changeStatusItem);

        menuBar.add(fileMenu);
        menuBar.add(userMenu);
        menuBar.add(carMenu);
        setJMenuBar(menuBar);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        inProgressTable = new JTable();
        JScrollPane inProgressScrollPane = new JScrollPane(inProgressTable);

        completedTable = new JTable();
        JScrollPane completedScrollPane = new JScrollPane(completedTable);

        archiveTable = new JTable();
        JScrollPane archiveScrollPane = new JScrollPane(archiveTable);

        tabbedPane.addTab("Текущие ремонты", inProgressScrollPane);
        tabbedPane.addTab("Клиенты", completedScrollPane); // Измененное название
        tabbedPane.addTab("Архив ремонтов", archiveScrollPane);

        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void showAllCars(List<Car> activeCars, List<Car> archivedCars) {
        // Таблица для текущих ремонтов
        String[] inProgressColumns = {"ID", "Марка", "VIN", "Гос. номер", "Владелец", "Телефон", "Начало ремонта", "Статус"};
        DefaultTableModel inProgressModel = new DefaultTableModel(inProgressColumns, 0);

        // Таблица для клиентов (бывшие архивные машины)
        String[] clientsColumns = {"ID", "Марка", "VIN", "Гос. номер", "Владелец", "Телефон", "Последний ремонт"};
        DefaultTableModel clientsModel = new DefaultTableModel(clientsColumns, 0);

        // Таблица для архива
        String[] archiveColumns = {"ID", "Марка", "VIN", "Гос. номер", "Владелец", "Телефон", "Дата ремонта", "Комплектующие"};
        DefaultTableModel archiveModel = new DefaultTableModel(archiveColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Делаем только колонку с комплектующими редактируемой
            }
        };

        // Обрабатываем активные автомобили
        for (Car car : activeCars) {
            User owner = new DatabaseManager().getUserById(car.getOwnerId());
            String ownerName = owner != null ? owner.getFullName() : "Не указано";
            String ownerPhone = owner != null ? owner.getPhone() : "Не указан";

            if (!"Нет статуса".equals(car.getStatus())) {
                inProgressModel.addRow(new Object[]{
                        car.getId(),
                        car.getName(),
                        car.getVin(),
                        car.getLicensePlate(),
                        ownerName,
                        ownerPhone,
                        car.getStartRepairTime() != null ? car.getStartRepairTime().toString() : "Не начат",
                        car.getStatus()
                });
            }
        }

        // Обрабатываем архивные автомобили для вкладки "Клиенты"
        for (Car car : archivedCars) {
            User owner = new DatabaseManager().getUserById(car.getOwnerId());
            String ownerName = owner != null ? owner.getFullName() : "Не указано";
            String ownerPhone = owner != null ? owner.getPhone() : "Не указан";

            clientsModel.addRow(new Object[]{
                    car.getId(),
                    car.getName(),
                    car.getVin(),
                    car.getLicensePlate(),
                    ownerName,
                    ownerPhone,
                    car.getEndRepairTime() != null ? car.getEndRepairTime().toString() : "Не указано"
            });
        }

        // Обрабатываем архивные автомобили для вкладки "Архив"
        for (Car car : archivedCars) {
            User owner = new DatabaseManager().getUserById(car.getOwnerId());
            String ownerName = owner != null ? owner.getFullName() : "Не указано";
            String ownerPhone = owner != null ? owner.getPhone() : "Не указан";

            // Извлекаем использованные комплектующие из описания проблемы
            String partsUsed = extractPartsUsed(car.getProblemDescription());

            archiveModel.addRow(new Object[]{
                    car.getId(),
                    car.getName(),
                    car.getVin(),
                    car.getLicensePlate(),
                    ownerName,
                    ownerPhone,
                    car.getEndRepairTime() != null ? car.getEndRepairTime().toString() : "Не указано",
                    partsUsed
            });
        }

        inProgressTable.setModel(inProgressModel);
        completedTable.setModel(clientsModel);
        archiveTable.setModel(archiveModel);

        setupPartsComboBox();
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
        int selectedTab = tabbedPane.getSelectedIndex();
        if (selectedTab == 0) {
            return inProgressTable.getSelectedRow();
        } else if (selectedTab == 1 && completedTable != null) {
            return completedTable.getSelectedRow();
        }
        return -1;
    }

    @Override
    public int getSelectedCarId() {
        int row = getSelectedCarRow();
        if (row >= 0) {
            int selectedTab = tabbedPane.getSelectedIndex();
            if (selectedTab == 0) {
                return (int) inProgressTable.getModel().getValueAt(row, 0);
            } else if (selectedTab == 1 && completedTable != null) {
                return (int) completedTable.getModel().getValueAt(row, 0);
            }
        }
        return -1;
    }

    @Override
    public void updateCarsTable() {
        ((DefaultTableModel) inProgressTable.getModel()).fireTableDataChanged();
        ((DefaultTableModel) completedTable.getModel()).fireTableDataChanged();
    }

    @Override
    public void clearSelection() {
        inProgressTable.clearSelection();
        completedTable.clearSelection();
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
    public void showProfileDialog(User user, ProfileCallback callback) {
        JDialog dialog = new JDialog(this, "Личный кабинет сотрудника", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField loginField = new JTextField(user.getLogin());
        loginField.setEditable(false);
        JTextField fullNameField = new JTextField(user.getFullName() != null ? user.getFullName() : "");
        JTextField phoneField = new JTextField(user.getPhone() != null ? user.getPhone() : "");

        contentPanel.add(new JLabel("Логин:"));
        contentPanel.add(loginField);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(new JLabel("ФИО:"));
        contentPanel.add(fullNameField);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(new JLabel("Телефон:"));
        contentPanel.add(phoneField);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        saveButton.addActionListener(e -> {
            if (fullNameField.getText().isEmpty() || phoneField.getText().isEmpty()) {
                showError("Все поля должны быть заполнены!");
                return;
            }

            boolean success = callback.processProfileInput(
                    fullNameField.getText(),
                    phoneField.getText()
            );
            if (success) {
                dialog.dispose();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    @Override
    public void setLogoutListener(ActionListener listener) {
        JMenu fileMenu = menuBar.getMenu(0);
        fileMenu.getItem(0).addActionListener(listener);
    }

    @Override
    public void setStatusChangeListener(ActionListener listener) {
        JMenu carMenu = menuBar.getMenu(2);
        carMenu.getItem(1).addActionListener(listener);
    }

    @Override
    public void setRefreshListener(ActionListener listener) {
        // Можно добавить кнопку обновления в меню
    }

    @Override
    public void setToggleViewListener(ActionListener listener) {
        // Теперь не нужно, так как есть вкладки
    }

    @Override
    public void setViewTitle(String title) {
        // Теперь не нужно, так как есть вкладки
    }

    private String extractPartsUsed(String problemDescription) {
        if (problemDescription == null) return "";
        // Логика извлечения информации о комплектующих из описания проблемы
        if (problemDescription.contains("Использованные детали:")) {
            return problemDescription.split("Использованные детали:")[1].trim();
        }
        return problemDescription;
    }

    private void setupPartsComboBox() {
        // Примерный список комплектующих
        String[] parts = {"Двигатель", "Тормозная система", "Подвеска", "КПП", "Электроника", "Кузовные детали"};

        TableColumn partsColumn = archiveTable.getColumnModel().getColumn(7);
        JComboBox<String> comboBox = new JComboBox<>(parts);
        partsColumn.setCellEditor(new DefaultCellEditor(comboBox));
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

    @Override
    public void setProfileListener(ActionListener listener) {
        JMenu userMenu = menuBar.getMenu(1);
        userMenu.getItem(0).addActionListener(listener);
    }

    @Override
    public void setAddCarListener(ActionListener listener) {
        JMenu carMenu = menuBar.getMenu(2);
        carMenu.getItem(0).addActionListener(listener);
    }
}