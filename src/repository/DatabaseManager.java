package repository;

import model.Car;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:users.db";

    public DatabaseManager() {
        System.out.println("Попытка подключения к базе данных...");
        createDatabase();
    }

    private void createDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Существующие таблицы (users и workers)
            String sqlUsers = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "login TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL)";
            stmt.execute(sqlUsers);

            String sqlWorkers = "CREATE TABLE IF NOT EXISTS workers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "login TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "access_key TEXT NOT NULL)";
            stmt.execute(sqlWorkers);

            // Новая таблица cars
            String sqlCars = "CREATE TABLE IF NOT EXISTS cars (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "vin TEXT NOT NULL UNIQUE," +
                    "license_plate TEXT NOT NULL UNIQUE," +
                    "owner_id INTEGER NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE)";
            stmt.execute(sqlCars);

            System.out.println("Таблицы успешно созданы/проверены");
        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблиц:");
            e.printStackTrace();
        }
    }

    public boolean registerUser(String login, String password) {
        String sql = "INSERT INTO users(login, password) VALUES(?, ?)";
        System.out.println("Попытка регистрации пользователя: " + login);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("Пользователь " + login + " успешно зарегистрирован.");
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println("Ошибка: Пользователь с логином " + login + " уже существует.");
            } else {
                System.err.println("Ошибка при регистрации пользователя:");
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean validateUser(String login, String password) {
        String sql = "SELECT password FROM users WHERE login = ?";
        System.out.println("Попытка входа пользователя: " + login);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                boolean isValid = storedPassword.equals(password);
                System.out.println("Проверка пароля для " + login + ": " + (isValid ? "Успех" : "Неверный пароль"));
                return isValid;
            } else {
                System.out.println("Пользователь с логином: " + login + " не найден.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при проверке пользователя:");
            e.printStackTrace();
            return false;
        }
    }
    public boolean registerWorker(String login, String password, String key) {
        if (!key.equals("OOP")) {
            System.out.println("Неверный ключ доступа");
            return false;
        }

        String sql = "INSERT INTO workers(login, password, access_key) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            pstmt.setString(3, key);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean validateWorker(String login, String password, String key) {
        String sql = "SELECT password, access_key FROM workers WHERE login = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("password").equals(password) &&
                        rs.getString("access_key").equals(key);
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static Car addCar(Car car) {
        String sql = "INSERT INTO cars(name, vin, license_plate, owner_id, problem_description) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, car.getName());
            pstmt.setString(2, car.getVin());
            pstmt.setString(3, car.getLicensePlate());
            pstmt.setInt(4, car.getOwnerId());
            pstmt.setString(5, car.getProblemDescription());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Car(
                            generatedKeys.getInt(1),
                            car.getName(),
                            car.getVin(),
                            car.getLicensePlate(),
                            car.getOwnerId(),
                            LocalDateTime.now(),
                            car.getProblemDescription()
                    );
                }
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении автомобиля:");
            e.printStackTrace();
            return null;
        }
    }

    public static List<Car> getUserCars(int ownerId) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT id, name, vin, license_plate, created_at, problem_description FROM cars WHERE owner_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ownerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Car car = new Car(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("vin"),
                        rs.getString("license_plate"),
                        ownerId,
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("problem_description")
                );
                cars.add(car);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении автомобилей:");
            e.printStackTrace();
        }

        return cars;
    }

    /**
     * Удаляет автомобиль
     * @param carId ID автомобиля
     * @param ownerId ID владельца (для проверки прав)
     * @return true если удаление успешно
     */
    public static boolean deleteCar(int carId, int ownerId) {
        String sql = "DELETE FROM cars WHERE id = ? AND owner_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, carId);
            pstmt.setInt(2, ownerId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении автомобиля:");
            e.printStackTrace();
            return false;
        }
    }
    public int getUserId(String login) {
        String sql = "SELECT id FROM users WHERE login = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1; // или бросить исключение

        } catch (SQLException e) {
            System.err.println("Ошибка при получении ID пользователя:");
            e.printStackTrace();
            return -1;
        }
    }
}
