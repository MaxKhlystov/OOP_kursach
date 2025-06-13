package view.dialogs;

import model.Car;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CompleteRepairDialog extends JDialog {
    private JComboBox<Car> carComboBox;
    private JTextArea partsUsedField;
    private JButton confirmButton;

    public interface CompleteRepairCallback {
        void onCompleteRepair(Car car, String partsUsed);
    }

    private CompleteRepairDialog(List<Car> carsInRepair, CompleteRepairCallback callback) {
        setTitle("Завершение ремонта");
        setModal(true);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        carComboBox = new JComboBox<>(carsInRepair.toArray(new Car[0]));
        partsUsedField = new JTextArea(5, 20);
        confirmButton = new JButton("Завершить");

        JPanel form = new JPanel(new GridLayout(0, 1));
        form.add(new JLabel("Автомобиль в ремонте:"));
        form.add(carComboBox);
        form.add(new JLabel("Использованные комплектующие:"));
        form.add(new JScrollPane(partsUsedField));

        add(form, BorderLayout.CENTER);
        add(confirmButton, BorderLayout.SOUTH);

        confirmButton.addActionListener(e -> {
            Car selectedCar = (Car) carComboBox.getSelectedItem();
            String partsUsed = partsUsedField.getText().trim();
            if (selectedCar != null) {
                callback.onCompleteRepair(selectedCar, partsUsed);
                dispose();
            }
        });
    }

    public static void showDialog(List<Car> carsInRepair, CompleteRepairCallback callback) {
        CompleteRepairDialog dialog = new CompleteRepairDialog(carsInRepair, callback);
        dialog.setVisible(true);
    }
}