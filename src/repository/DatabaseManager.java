package repository;

import model.Car;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_users_URL = "jdbc:sqlite:users.db";
    private static final String DB_workers_URL = "jdbc:sqlite:workers.db";
    private static final String DB_cars_URL = "jdbc:sqlite:cars.db";

    public DatabaseManager() {
        System.out.println("Попытка подключения к базе данных...");
        createDatabaseUsers();
        createDatabaseCars();
        createDatabaseWorkers();
    }

    private void createDatabaseUsers() {
        try (Connection conn = DriverManager.getConnection(DB_users_URL);
             Statement stmt = conn.createStatement()) {
            String sqlUsers = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "login TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL)";
            stmt.execute(sqlUsers);
            System.out.println("Таблицы успешно созданы/проверены");
        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблиц:");
            e.printStackTrace();
        }
    }
    private void createDatabaseWorkers(){
        try (Connection conn = DriverManager.getConnection(DB_workers_URL);
             Statement stmt = conn.createStatement()) {
            String sqlWorkers = "CREATE TABLE IF NOT EXISTS workers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "login TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "access_key TEXT NOT NULL)";
            stmt.execute(sqlWorkers);
            System.out.println("Таблицы успешно созданы/проверены");
        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблиц:");
            e.printStackTrace();
        }
    }

    private void createDatabaseCars() {
        try (Connection conn = DriverManager.getConnection(DB_cars_URL);
             Statement stmt = conn.createStatement()) {
            String sqlCars = "CREATE TABLE IF NOT EXISTS cars (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "vin TEXT NOT NULL UNIQUE," +
                    "license_plate TEXT NOT NULL UNIQUE," +
                    "owner_id INTEGER NOT NULL," +
                    "problem_description TEXT," +
                    "image_path TEXT," +  // Новое поле для хранения пути к изображению
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

        try (Connection conn = DriverManager.getConnection(DB_users_URL);
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

        try (Connection conn = DriverManager.getConnection(DB_users_URL);
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
        try (Connection conn = DriverManager.getConnection(DB_workers_URL);
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
        try (Connection conn = DriverManager.getConnection(DB_workers_URL);
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
    public Car addCar(Car car) {
        String sql = "INSERT INTO cars(name, vin, license_plate, owner_id, problem_description, image_path) " +
                "VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_cars_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, car.getName());
            pstmt.setString(2, car.getVin());
            pstmt.setString(3, car.getLicensePlate());
            pstmt.setInt(4, car.getOwnerId());
            pstmt.setString(5, car.getProblemDescription());
            pstmt.setString(6, car.getImagePath());  // Добавляем путь к изображению

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
                            car.getProblemDescription(),
                            car.getImagePath()  // Сохраняем путь к изображению
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

    public boolean updateCar(Car car) {
        String sql = "UPDATE cars SET name = ?, license_plate = ?, problem_description = ?, image_path = ? " +
                "WHERE id = ? AND owner_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_cars_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, car.getName());
            pstmt.setString(2, car.getLicensePlate());
            pstmt.setString(3, car.getProblemDescription());
            pstmt.setString(4, car.getImagePath());  // Обновляем путь к изображению
            pstmt.setInt(5, car.getId());
            pstmt.setInt(6, car.getOwnerId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении автомобиля:");
            e.printStackTrace();
            return false;
        }
    }

    public static List<Car> getUserCars(int ownerId) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT id, name, vin, license_plate, created_at, problem_description, image_path " +
                "FROM cars WHERE owner_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_cars_URL);
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
                        rs.getString("problem_description"),
                        rs.getString("image_path")  // Получаем путь к изображению
                );
                cars.add(car);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении автомобилей:");
            e.printStackTrace();
        }
        return cars;
    }

    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars";

        try (Connection conn = DriverManager.getConnection(DB_cars_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Car car = new Car(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("vin"),
                        rs.getString("license_plate"),
                        rs.getInt("owner_id"),
                        rs.getString("problem_description"),
                        rs.getString("image_path")  // Получаем путь к изображению
                );
                cars.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

    public boolean deleteCar(int carId, int ownerId) {
        String sql = "DELETE FROM cars WHERE id = ? AND owner_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_cars_URL);
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

        try (Connection conn = DriverManager.getConnection(DB_users_URL);
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
