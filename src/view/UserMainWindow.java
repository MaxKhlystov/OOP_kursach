package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import model.Car;
import utils.ImageUtils;
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
        Dimension maxSize = new Dimension(200, 250);
        card.setMaximumSize(maxSize);

        // Загрузка и отображение изображения
        JLabel imageLabel = new JLabel();
        try {
            BufferedImage image = ImageUtils.loadImage(car.getImagePath());
            ImageIcon icon = new ImageIcon(image.getScaledInstance(150, 100, Image.SCALE_SMOOTH));
            imageLabel.setIcon(icon);
        } catch (IOException e) {
            imageLabel.setText("Нет изображения");
        }

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

        card.add(imageLabel);
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
        JLabel imageLabel = new JLabel("Фото не выбрано");
        JButton uploadBtn = new JButton("Загрузить фото");

        // Переменная для хранения загруженного изображения
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
                }finally {
                    fileChooser.setVisible(false);
                }
            }
        });

        Object[] message = {
                "Название:", nameField,
                "VIN:", vinField,
                "Гос. номер:", plateField,
                "Описание проблемы:", new JScrollPane(problemArea),
                uploadBtn, imageLabel
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Добавить автомобиль",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String imagePath = null;
            if (uploadedImage[0] != null) {
                try {
                    String fileName = "car_" + System.currentTimeMillis() + ".jpg";
                    imagePath = ImageUtils.saveImage(uploadedImage[0], fileName);
                } catch (IOException ex) {
                    showError("Ошибка при сохранении изображения");
                }
            }

            boolean success = callback.processInput(
                    nameField.getText(),
                    vinField.getText(),
                    plateField.getText(),
                    problemArea.getText(),
                    imagePath); // Передаем путь к изображению

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

        // Компоненты для работы с изображением
        JLabel imageLabel = new JLabel();
        JButton uploadBtn = new JButton("Изменить фото");
        BufferedImage[] currentImage = {null};
        String[] newImagePath = {null};

        // Загрузка текущего изображения
        try {
            if (car.getImagePath() != null && !car.getImagePath().isEmpty()) {
                currentImage[0] = ImageIO.read(new File(car.getImagePath()));
                ImageIcon icon = new ImageIcon(currentImage[0].getScaledInstance(150, 100, Image.SCALE_SMOOTH));
                imageLabel.setIcon(icon);
                imageLabel.setText("");
            } else {
                currentImage[0] = ImageUtils.loadImage("default.jpg");
                ImageIcon icon = new ImageIcon(currentImage[0].getScaledInstance(150, 100, Image.SCALE_SMOOTH));
                imageLabel.setIcon(icon);
                imageLabel.setText("(используется заглушка)");
            }
        } catch (IOException ex) {
            imageLabel.setText("Ошибка загрузки изображения");
        }

        uploadBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    currentImage[0] = ImageIO.read(fileChooser.getSelectedFile());
                    ImageIcon icon = new ImageIcon(currentImage[0].getScaledInstance(150, 100, Image.SCALE_SMOOTH));
                    imageLabel.setIcon(icon);
                    imageLabel.setText("Новое фото: " + fileChooser.getSelectedFile().getName());

                    // Сохраняем временный путь к новому изображению
                    String fileName = "car_" + System.currentTimeMillis() + ".jpg";
                    newImagePath[0] = ImageUtils.saveImage(currentImage[0], fileName);
                } catch (IOException ex) {
                    showError("Ошибка при загрузке изображения");
                }finally {
                    fileChooser.setVisible(false);
                }
            }
        });

        Object[] message = {
                "Название:", nameField,
                "VIN:", vinField,
                "Гос. номер:", plateField,
                "Описание проблемы:", new JScrollPane(problemArea),
                "Текущее фото:", imageLabel,
                uploadBtn
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Редактировать автомобиль",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String finalImagePath = (newImagePath[0] != null) ? newImagePath[0] : car.getImagePath();

            boolean success = callback.processInput(
                    nameField.getText(),
                    vinField.getText(),
                    plateField.getText(),
                    problemArea.getText(),
                    finalImagePath);

            if (success) {
                showMessage("Автомобиль успешно обновлен");
            }
        }
    }

    @Override
    public void showCarDetailsDialog(Car car) {
        String message = String.format(
                "Название: %s\nVIN: %s\nГос. номер: %s\nОписание проблемы:\n%s",
                car.getName(),
                car.getVin(),
                car.getLicensePlate(),
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