package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import model.Car;
import view.interfaces.UserMainView;

public class UserMainWindow extends JFrame implements UserMainView {
    private JPanel mainPanel;
    private JMenuBar menuBar;
    private static final Color CARD_BORDER_COLOR = new Color(20,20,20);
    private static final int CARD_BORDER_THICKNESS = 1;

    public UserMainWindow(String username, int userId) {
        super("Главное окно - " + username);
        initWindow();
        initComponents();
    }

    private void initWindow() {
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        JMenuItem logoutItem = new JMenuItem("Выйти");
        fileMenu.add(logoutItem);

        JMenu carMenu = new JMenu("Автомобиль");
        JMenuItem addCarItem = new JMenuItem("Добавить автомобиль");
        carMenu.add(addCarItem);

        menuBar.add(fileMenu);
        menuBar.add(carMenu);
        setJMenuBar(menuBar);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
    }

    @Override
    public void clearMainPanel() {
        mainPanel.removeAll();
        updateUI();
    }

    @Override
    public void addCarCard(Car car, ActionListener detailsAction, ActionListener editAction, ActionListener deleteAction) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER_COLOR, CARD_BORDER_THICKNESS),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        Dimension maxSize = new Dimension(200, 200);
        card.setMaximumSize(maxSize);

        JLabel nameLabel = new JLabel(car.getName());
        JLabel vinLabel = new JLabel("VIN: " + car.getVin());
        JLabel plateLabel = new JLabel("Гос. номер: " + car.getLicensePlate());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton detailsBtn = new JButton("Подробнее");
        JButton editBtn = new JButton("Изменить");
        JButton deleteBtn = new JButton("Удалить");

        detailsBtn.addActionListener(detailsAction);
        editBtn.addActionListener(editAction);
        deleteBtn.addActionListener(deleteAction);

        buttonPanel.add(detailsBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        card.add(nameLabel);
        card.add(vinLabel);
        card.add(plateLabel);
        card.add(buttonPanel);

        mainPanel.add(card);
        updateUI();
    }

    @Override
    public void showNoCarsMessage() {
        JLabel noCarsLabel = new JLabel("У вас нет автомобилей в ремонте");
        noCarsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        noCarsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(noCarsLabel);
    }

    @Override
    public void updateUI() {
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    @Override
    public void showAddCarDialog(CarDialogCallback callback) {
        JTextField nameField = new JTextField();
        JTextField vinField = new JTextField();
        JTextField plateField = new JTextField();
        JTextArea problemArea = new JTextArea(3, 20);

        Object[] message = {
                "Название:", nameField,
                "VIN:", vinField,
                "Гос. номер:", plateField,
                "Описание проблемы:", new JScrollPane(problemArea)
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Добавить автомобиль",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            boolean success = callback.processInput(
                    nameField.getText(),
                    vinField.getText(),
                    plateField.getText(),
                    problemArea.getText());

            if (success) {
                showMessage("Автомобиль успешно добавлен");
            }
        }
    }

    @Override
    public void showEditCarDialog(Car car, CarDialogCallback callback) {
        JTextField nameField = new JTextField(car.getName());
        JTextField vinField = new JTextField(car.getVin());
        JTextField plateField = new JTextField(car.getLicensePlate());
        JTextArea problemArea = new JTextArea(car.getProblemDescription(), 3, 20);

        Object[] message = {
                "Название:", nameField,
                "VIN:", vinField,
                "Гос. номер:", plateField,
                "Описание проблемы:", new JScrollPane(problemArea)
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Редактировать автомобиль",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            boolean success = callback.processInput(
                    nameField.getText(),
                    vinField.getText(),
                    plateField.getText(),
                    problemArea.getText());

            if (success) {
                showMessage("Автомобиль успешно обновлен");
            }
        }
    }

    @Override
    public void showCarDetailsDialog(Car car) {
        String message = String.format(
                "Название: %s\nVIN: %s\nГос. номер: %s\nДата добавления: %s\nОписание проблемы:\n%s",
                car.getName(),
                car.getVin(),
                car.getLicensePlate(),
                car.getCreatedAt(),
                car.getProblemDescription());

        JOptionPane.showMessageDialog(
                this,
                message,
                "Подробности автомобиля",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showDeleteConfirmation(String carName, ConfirmationCallback callback) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Вы уверены, что хотите удалить автомобиль " + carName + "?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        callback.onResult(option == JOptionPane.YES_OPTION);
    }

    @Override
    public void showLogoutOptions(LogoutCallback callback) {
        Object[] options = {"Выйти в меню авторизации", "Выйти из программы"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Выберите действие:",
                "Выход",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        callback.onChoice(choice);
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
    public void navigateToAuth() {
        this.dispose();
        for (Window window : Window.getWindows()) {
            if (window instanceof AuthWindow) {
                window.setVisible(true);
                return;
            }
        }
    }

    @Override
    public void setAddCarListener(ActionListener listener) {
        JMenu carMenu = menuBar.getMenu(1);
        carMenu.getItem(0).addActionListener(listener);
    }

    @Override
    public void setLogoutListener(ActionListener listener) {
        JMenu fileMenu = menuBar.getMenu(0);
        fileMenu.getItem(0).addActionListener(listener);
    }

    @Override
    public void close() {
        this.dispose();
    }
}