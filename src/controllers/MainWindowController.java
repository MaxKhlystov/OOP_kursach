package controllers;

import model.Car;
import repository.DatabaseManager;
import view.AuthWindow;
import view.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class MainWindowController {
    private MainWindow view;
    private int userId;
    private String username;

    public MainWindowController(MainWindow view, String username, int userId) {
        this.view = view;
        this.username = username;
        this.userId = userId;
        initController();
    }

    private void initController() {
        view.getAddCarButton().addActionListener(this::handleAddCar);
        view.getLogoutButton().addActionListener(e -> handleLogout());
        buildCarsUI();
    }

    private void handleAddCar(ActionEvent e) {
        JDialog dialog = new JDialog(view, "Добавить автомобиль", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField brandField = new JTextField(15);
        JTextField modelField = new JTextField(15);
        JTextField plateField = new JTextField(10);
        JTextField vinField = new JTextField(17);
        JTextArea problemArea = new JTextArea(3, 20);
        problemArea.setLineWrap(true);

        addFormField(dialog, gbc, "Марка:", brandField, 0);
        addFormField(dialog, gbc, "Модель:", modelField, 1);
        addFormField(dialog, gbc, "Гос. номер:", plateField, 2);
        addFormField(dialog, gbc, "VIN-номер:", vinField, 3);
        addFormField(dialog, gbc, "Описание проблемы:", new JScrollPane(problemArea), 4);

        JButton submitButton = new JButton("Добавить");
        submitButton.addActionListener(ev -> {
            Car newCar = new Car(
                    brandField.getText() + " " + modelField.getText(),
                    vinField.getText(),
                    plateField.getText(),
                    userId,
                    problemArea.getText()
            );

            Car addedCar = DatabaseManager.addCar(newCar);
            if (addedCar != null) {
                JOptionPane.showMessageDialog(dialog, "Автомобиль добавлен на ремонт!");
                dialog.dispose();
                buildCarsUI();
            } else {
                JOptionPane.showMessageDialog(dialog, "Ошибка при добавлении автомобиля", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(ev -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }

    private void buildCarsUI() {
        view.clearMainPanel();

        List<Car> userCars = DatabaseManager.getUserCars(userId);
        if (userCars.isEmpty()) {
            JLabel noCarsLabel = new JLabel("У вас нет автомобилей в ремонте");
            noCarsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            noCarsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            view.addToMainPanel(noCarsLabel);
        } else {
            JPanel carsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            carsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            for (Car car : userCars) {
                carsPanel.add(createCarCard(car));
            }

            JScrollPane scrollPane = new JScrollPane(carsPanel);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            view.addToMainPanel(scrollPane);
        }

        view.addToMainPanel(view.getAddCarButton());
        view.updateUI();
    }

    private JPanel createCarCard(Car car) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        card.setPreferredSize(new Dimension(200, 150));

        JLabel nameLabel = new JLabel(car.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel plateLabel = new JLabel(car.getLicensePlate());
        plateLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel statusLabel = new JLabel("Статус: В ремонте");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton detailsBtn = new JButton("Подробнее");
        detailsBtn.addActionListener(e -> showCarDetails(car));

        JButton deleteBtn = new JButton("Удалить");
        deleteBtn.addActionListener(e -> deleteCar(car));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(detailsBtn);
        buttonPanel.add(deleteBtn);

        card.add(nameLabel);
        card.add(plateLabel);
        card.add(statusLabel);
        card.add(Box.createVerticalGlue());
        card.add(buttonPanel);

        return card;
    }

    private void showCarDetails(Car car) {
        JDialog detailsDialog = new JDialog(view, "Подробности автомобиля", true);
        detailsDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        addDetailRow(detailsDialog, gbc, "Марка и модель:", car.getName(), 0);
        addDetailRow(detailsDialog, gbc, "VIN номер:", car.getVin(), 1);
        addDetailRow(detailsDialog, gbc, "Гос. номер:", car.getLicensePlate(), 2);
        addDetailRow(detailsDialog, gbc, "Дата приёмки:", car.getCreatedAt().toString(), 3);
        addDetailRow(detailsDialog, gbc, "Описание проблемы:", car.getProblemDescription(), 4);

        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener(e -> detailsDialog.dispose());

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        detailsDialog.add(closeButton, gbc);

        detailsDialog.pack();
        detailsDialog.setLocationRelativeTo(view);
        detailsDialog.setVisible(true);
    }

    private void addDetailRow(JDialog dialog, GridBagConstraints gbc, String label, String value, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        JTextArea valueArea = new JTextArea(value);
        valueArea.setEditable(false);
        valueArea.setLineWrap(true);
        valueArea.setWrapStyleWord(true);
        valueArea.setBackground(dialog.getBackground());
        dialog.add(valueArea, gbc);
    }

    private void deleteCar(Car car) {
        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Вы уверены, что хотите удалить автомобиль " + car.getName() + "?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = DatabaseManager.deleteCar(car.getId(), userId);
            if (success) {
                JOptionPane.showMessageDialog(view, "Автомобиль удалён");
                buildCarsUI();
            } else {
                JOptionPane.showMessageDialog(view, "Ошибка при удалении автомобиля", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleLogout() {
        view.dispose();
        new AuthWindow().setVisible(true);
    }

    private void addFormField(JDialog dialog, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        dialog.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        dialog.add(field, gbc);
    }
}