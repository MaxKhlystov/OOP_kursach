package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import model.Car;
import model.User;
import utils.ImageUtils;
import view.interfaces.UserMainView;

public class UserMainWindow extends JFrame implements UserMainView {
    private JPanel mainPanel;
    private JPanel notificationPanel;
    private JMenuBar menuBar;
    private JTextField currentVinField;
    private JTextField currentPlateField;

    private static final Color CARD_BORDER_COLOR = new Color(20,20,20);
    private static final int CARD_BORDER_THICKNESS = 1;

    public UserMainWindow() {
        super("Главное окно");
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

        // Панель уведомлений
        notificationPanel = new JPanel();
        notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS));
        notificationPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        add(notificationPanel, BorderLayout.NORTH);

        menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        JMenuItem logoutItem = new JMenuItem("Выйти");
        fileMenu.add(logoutItem);

        JMenu userMenu = new JMenu("Пользователь");
        JMenuItem profileItem = new JMenuItem("Личный кабинет");
        userMenu.add(profileItem);

        JMenu carMenu = new JMenu("Автомобиль");
        JMenuItem addCarItem = new JMenuItem("Добавить автомобиль");
        carMenu.add(addCarItem);

        menuBar.add(fileMenu);
        menuBar.add(userMenu);
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
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER_COLOR, CARD_BORDER_THICKNESS, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(500, 160));
        card.setBackground(new Color(245, 245, 245));

        // Левая часть: изображение
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(150, 100));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            BufferedImage image = ImageUtils.loadImage(car.getImagePath());
            ImageIcon icon;
            if (image != null) {
                icon = new ImageIcon(image.getScaledInstance(150, 100, Image.SCALE_SMOOTH));
            } else {
                icon = new ImageIcon();
            }
            imageLabel.setIcon(icon);
        } catch (IOException e) {
            imageLabel.setText("Ошибка загрузки изображения");
        }

        // Центр: Информация
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(card.getBackground());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JLabel nameLabel = new JLabel(car.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 30));
        JLabel vinLabel = new JLabel("VIN: " + car.getVin());
        vinLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel plateLabel = new JLabel("Гос. номер: " + car.getLicensePlate());
        plateLabel.setFont(new Font("Arial", Font.BOLD, 20));

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(vinLabel);
        infoPanel.add(plateLabel);

        // Правая часть: Кнопки
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(card.getBackground());

        JButton detailsBtn = new JButton("Подробнее");
        JButton editBtn = new JButton("Изменить");
        JButton deleteBtn = new JButton("Удалить");

        detailsBtn.addActionListener(detailsAction);
        editBtn.addActionListener(editAction);
        deleteBtn.addActionListener(deleteAction);

        detailsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        editBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(detailsBtn);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(editBtn);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(deleteBtn);

        card.add(imageLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.EAST);

        mainPanel.add(Box.createVerticalStrut(10)); // отступ между карточками
        mainPanel.add(card);
        updateUI();
    }

    @Override
    public void showNoCarsMessage() {
        JLabel noCarsLabel = new JLabel("У вас нет автомобилей");
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
        currentVinField = new JTextField();
        currentPlateField = new JTextField();
        JTextArea problemArea = new JTextArea(3, 20);
        JLabel imageLabel = new JLabel("Фото не выбрано");
        JButton uploadBtn = new JButton("Загрузить фото");

        BufferedImage[] uploadedImage = {null};

        uploadBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    uploadedImage[0] = ImageIO.read(fileChooser.getSelectedFile());
                    imageLabel.setText("Фото выбрано: " + fileChooser.getSelectedFile().getName());
                } catch (IOException ex) {
                    showError("Ошибка при загрузке изображения");
                }
            }
        });

        Object[] message = {
                "Название:", nameField,
                "VIN:", currentVinField,
                "Гос. номер:", currentPlateField,
                "Описание проблемы:", new JScrollPane(problemArea),
                uploadBtn, imageLabel
        };

        int option;
        do {
            option = JOptionPane.showConfirmDialog(
                    this,
                    message,
                    "Добавить автомобиль",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            // Сбросим цвета перед валидацией
            highlightVinField(false);
            highlightLicensePlateField(false);

            String imagePath = null;
            if (uploadedImage[0] != null) {
                try {
                    String fileName = "car_" + System.currentTimeMillis() + ".jpg";
                    ImageUtils.saveImage(uploadedImage[0], fileName);
                    imagePath = fileName;
                } catch (IOException ex) {
                    showError("Ошибка при сохранении изображения");
                }
            }

            boolean success = callback.processInput(
                    nameField.getText(),
                    currentVinField.getText(),
                    currentPlateField.getText(),
                    problemArea.getText(),
                    imagePath);

            if (success) {
                showMessage("Автомобиль успешно добавлен");
                return;
            }

            // Если неуспешно — оставляем окно открытым (цикл продолжится)

        } while (true);
    }

    @Override
    public void showEditCarDialog(Car car, CarDialogCallback callback) {
        JDialog dialog = new JDialog(this, "Редактировать автомобиль", true);
        dialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField(car.getName());
        JTextField vinField = new JTextField(car.getVin());
        JTextField plateField = new JTextField(car.getLicensePlate());
        JTextArea problemArea = new JTextArea(car.getProblemDescription(), 3, 20);

        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(150, 100));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        JButton uploadBtn = new JButton("Изменить фото");
        BufferedImage[] currentImage = {null};
        String[] newImagePath = {null};

        // Загрузка текущего изображения
        try {
            String imagePath = (car.getImagePath() != null && !car.getImagePath().isEmpty()) ?
                    car.getImagePath() : "default.png";
            File imageFile = new File(ImageUtils.MEDIA_PATH + imagePath);
            if (imageFile.exists()) {
                currentImage[0] = ImageIO.read(imageFile);
            } else {
                // Если файл не найден, используем дефолтное изображение
                currentImage[0] = ImageIO.read(new File(ImageUtils.MEDIA_PATH + "default.png"));
            }
            ImageIcon icon = new ImageIcon(currentImage[0].getScaledInstance(150, 100, Image.SCALE_SMOOTH));
            imageLabel.setIcon(icon);
        } catch (IOException ex) {
            imageLabel.setText("Ошибка загрузки изображения");
        }
        uploadBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(dialog);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    BufferedImage img = ImageIO.read(selectedFile);
                    if (img != null) {
                        currentImage[0] = img;
                        ImageIcon icon = new ImageIcon(img.getScaledInstance(150, 100, Image.SCALE_SMOOTH));
                        imageLabel.setIcon(icon);
                        // Генерируем уникальное имя файла
                        String fileName = "car_" + System.currentTimeMillis() + ".jpg";
                        newImagePath[0] = fileName;
                        dialog.pack();
                    }
                } catch (IOException ex) {
                    showError("Ошибка при загрузке изображения");
                }
            }
        });

        // Добавляем компоненты в contentPanel
        contentPanel.add(new JLabel("Название:"));
        contentPanel.add(nameField);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(new JLabel("VIN:"));
        contentPanel.add(vinField);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(new JLabel("Гос. номер:"));
        contentPanel.add(plateField);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(new JLabel("Описание проблемы:"));
        contentPanel.add(new JScrollPane(problemArea));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(new JLabel("Фото:"));

        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        imagePanel.add(imageLabel);
        contentPanel.add(imagePanel);
        contentPanel.add(Box.createVerticalStrut(5));

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        filePanel.add(uploadBtn);
        contentPanel.add(filePanel);
        dialog.add(contentPanel, BorderLayout.CENTER);
        // Кнопки OK и Cancel
        JPanel buttonPanel = new JPanel();
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Отмена");

        okBtn.addActionListener(ev -> {
            String finalImagePath = newImagePath[0] != null ? newImagePath[0] : car.getImagePath();
            if (finalImagePath == null || finalImagePath.isEmpty()) {
                finalImagePath = "default.png";
            }

            // Сохраняем новое изображение, если оно было загружено
            if (newImagePath[0] != null && currentImage[0] != null) {
                try {
                    ImageUtils.saveImage(currentImage[0], newImagePath[0]);
                } catch (IOException ex) {
                    showError("Ошибка при сохранении изображения");
                    return;
                }
            }

            boolean success = callback.processInput(
                    nameField.getText(),
                    vinField.getText(),
                    plateField.getText(),
                    problemArea.getText(),
                    finalImagePath);

            if (success) {
                showMessage("Автомобиль успешно обновлен");
                dialog.dispose();
            }
        });

        cancelBtn.addActionListener(ev -> dialog.dispose());
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    @Override
    public void showCarDetailsDialog(Car car) {
        String message = String.format(
                "Название: %s\nVIN: %s\nГос. номер: %s\nОписание проблемы:\n%s",
                car.getName(),
                car.getVin(),
                car.getLicensePlate(),
                car.getProblemDescription(),
                car.getImagePath());

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
    public void showNotification(String message, boolean isWarning) {
        JPanel notificationCard = new JPanel();
        notificationCard.setLayout(new BorderLayout());
        notificationCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isWarning ? Color.ORANGE : Color.BLUE, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        notificationCard.setBackground(isWarning ? new Color(255, 250, 205) : new Color(225, 245, 254));

        JLabel messageLabel = new JLabel(message);
        JButton closeButton = new JButton("×");
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setFont(new Font("Arial", Font.BOLD, 12));

        closeButton.addActionListener(e -> {
            notificationPanel.remove(notificationCard);
            notificationPanel.revalidate();
            notificationPanel.repaint();
        });

        notificationCard.add(messageLabel, BorderLayout.CENTER);
        notificationCard.add(closeButton, BorderLayout.EAST);

        notificationPanel.add(notificationCard);
        notificationPanel.revalidate();
        notificationPanel.repaint();
    }

    @Override
    public void showProfileDialog(User user, ProfileCallback callback) {
        JDialog dialog = new JDialog(this, "Личный кабинет", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 350); // Увеличили высоту для новой кнопки
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

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        JButton saveButton = new JButton("Сохранить");
        JButton deleteAccountButton = new JButton("Удалить аккаунт");
        deleteAccountButton.setForeground(Color.RED);
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

        deleteAccountButton.addActionListener(e -> {
            showDeleteAccountConfirmation(confirmed -> {
                if (confirmed) {
                    callback.onAccountDeleteRequested();
                    dialog.dispose();
                }
            });
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(deleteAccountButton);
        buttonPanel.add(cancelButton);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    @Override
    public void showDeleteAccountConfirmation(ConfirmationCallback callback) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Вы уверены, что хотите удалить свой аккаунт? Это действие нельзя отменить!",
                "Подтверждение удаления аккаунта",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        callback.onResult(option == JOptionPane.YES_OPTION);
    }

    @Override
    public void highlightVinField(boolean highlight) {
        if (currentVinField != null) {
            currentVinField.setBackground(highlight ? new Color(255, 220, 220) : Color.WHITE);
        }
    }

    @Override
    public void highlightLicensePlateField(boolean highlight) {
        if (currentPlateField != null) {
            currentPlateField.setBackground(highlight ? new Color(255, 220, 220) : Color.WHITE);
        }
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
    public void clearNotifications() {
        notificationPanel.removeAll();
        notificationPanel.revalidate();
        notificationPanel.repaint();
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
        JMenu carMenu = menuBar.getMenu(2);
        carMenu.getItem(0).addActionListener(listener);
    }

    @Override
    public void setLogoutListener(ActionListener listener) {
        JMenu fileMenu = menuBar.getMenu(0);
        fileMenu.getItem(0).addActionListener(listener);
    }

    @Override
    public void setProfileListener(ActionListener listener) {
        JMenu userMenu = menuBar.getMenu(1);
        userMenu.getItem(0).addActionListener(listener);
    }

    @Override
    public void close() {
        this.dispose();
    }
}