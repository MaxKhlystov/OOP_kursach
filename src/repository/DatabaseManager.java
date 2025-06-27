package repository;

import model.Car;
import model.User;

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
                    "password TEXT NOT NULL," +
                    "full_name TEXT," +
                    "phone TEXT" +
                    ")";
            stmt.execute(sqlUsers);
            System.out.println("Таблица users успешно создана/проверена");

        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблиц:");
            e.printStackTrace();
        }
    }

    private void createDatabaseWorkers() {
        try (Connection conn = DriverManager.getConnection(DB_workers_URL);
             Statement stmt = conn.createStatement()) {
            String sqlWorkers = "CREATE TABLE IF NOT EXISTS workers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "login TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "access_key TEXT NOT NULL," +
                    "full_name TEXT," +
                    "phone TEXT" +
                    ")";
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

            stmt.execute("PRAGMA foreign_keys = ON");

            // Основная таблица для текущих ремонтов
            String sqlCars = "CREATE TABLE IF NOT EXISTS cars (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "vin TEXT NOT NULL UNIQUE," +
                    "license_plate TEXT NOT NULL UNIQUE," +
                    "owner_id INTEGER NOT NULL," +
                    "problem_description TEXT," +
                    "image_path TEXT," +
                    "status TEXT DEFAULT 'Нет статуса'," +
                    "start_repair_time TIMESTAMP NULL," +
                    "end_repair_time TIMESTAMP NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE)";
            stmt.execute(sqlCars);

            // Таблица для архивных (завершенных) ремонтов
            String sqlArchive = "CREATE TABLE IF NOT EXISTS repairs_archive (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "car_id INTEGER NOT NULL," +
                    "name TEXT NOT NULL," +
                    "vin TEXT NOT NULL," +
                    "license_plate TEXT NOT NULL," +
                    "owner_id INTEGER NOT NULL," +
                    "problem_description TEXT," +
                    "image_path TEXT," +
                    "status TEXT DEFAULT 'Ремонт выполнен'," +
                    "start_repair_time TIMESTAMP NULL," +
                    "end_repair_time TIMESTAMP NULL," +
                    "created_at TIMESTAMP," +
                    "archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "parts_used TEXT," +
                    "FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE)";
            stmt.execute(sqlArchive);

            System.out.println("Таблицы cars и repairs_archive успешно созданы/проверены");
        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблиц:");
            e.printStackTrace();
        }
    }

    public boolean archiveCompletedRepair(Car car, String partsUsed) {
        String insertSql = "INSERT INTO repairs_archive (" +
                "car_id, name, vin, license_plate, owner_id, problem_description, " +
                "image_path, status, start_repair_time, end_repair_time, created_at, parts_used) " +
                "SELECT id, name, vin, license_plate, owner_id, problem_description, " +
                "image_path, status, start_repair_time, end_repair_time, created_at, ? " +
                "FROM cars WHERE id = ?";

        String deleteSql = "DELETE FROM cars WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_cars_URL)) {
            conn.setAutoCommit(false);

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {

                // Копируем запись в архив
                insertStmt.setString(1, partsUsed);
                insertStmt.setInt(2, car.getId());
                insertStmt.executeUpdate();

                // Удаляем из основной таблицы
                deleteStmt.setInt(1, car.getId());
                deleteStmt.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Car> getArchivedRepairs() {
        List<Car> archivedCars = new ArrayList<>();
        String sql = "SELECT * FROM repairs_archive ORDER BY archived_at DESC";

        try (Connection conn = DriverManager.getConnection(DB_cars_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Car car = new Car(
                        rs.getInt("car_id"),
                        rs.getString("name"),
                        rs.getString("vin"),
                        rs.getString("license_plate"),
                        rs.getInt("owner_id"),
                        rs.getString("problem_description") != null ? rs.getString("problem_description") : "",
                        rs.getString("image_path") != null ? rs.getString("image_path") : "",
                        rs.getString("status")
                );
                Timestamp startRepairTime = rs.getTimestamp("start_repair_time");
                if (startRepairTime != null) {
                    car.setStartRepairTime(startRepairTime.toLocalDateTime());
                }
                Timestamp endRepairTime = rs.getTimestamp("end_repair_time");
                if (endRepairTime != null) {
                    car.setEndRepairTime(endRepairTime.toLocalDateTime());
                }
                archivedCars.add(car);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке архивных ремонтов:");
            e.printStackTrace();
        }
        return archivedCars;
    }

    public boolean isCarArchived(int carId) {
        String sql = "SELECT COUNT(*) FROM repairs_archive WHERE car_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_cars_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, carId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

    public boolean updateUserProfile(int userId, String fullName, String phone) {
        String sql = "UPDATE users SET full_name = ?, phone = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_users_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fullName);
            pstmt.setString(2, phone);
            pstmt.setInt(3, userId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении профиля пользователя:");
            e.printStackTrace();
            return false;
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT id, login, full_name, phone FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_users_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("full_name"),
                        rs.getString("phone")
                );
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении данных пользователя:");
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, login, full_name, phone FROM users";

        try (Connection conn = DriverManager.getConnection(DB_users_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("full_name"),
                        rs.getString("phone")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка пользователей:");
            e.printStackTrace();
        }
        return users;
    }

    public boolean deleteUser(int userId) {
        String deleteCarsSql = "DELETE FROM cars WHERE owner_id = ?";

        try (Connection connCars = DriverManager.getConnection(DB_cars_URL);
             PreparedStatement pstmtCars = connCars.prepareStatement(deleteCarsSql)) {

            pstmtCars.setInt(1, userId);
            pstmtCars.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении автомобилей пользователя:");
            e.printStackTrace();
            return false;
        }

        String deleteUserSql = "DELETE FROM users WHERE id = ?";

        try (Connection connUsers = DriverManager.getConnection(DB_users_URL);
             PreparedStatement pstmtUsers = connUsers.prepareStatement(deleteUserSql)) {

            pstmtUsers.setInt(1, userId);
            return pstmtUsers.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении пользователя:");
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
        String sql = "INSERT INTO cars(name, vin, license_plate, owner_id, problem_description, image_path, status, start_repair_time) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_cars_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, car.getName());
            pstmt.setString(2, car.getVin());
            pstmt.setString(3, car.getLicensePlate());
            pstmt.setInt(4, car.getOwnerId());
            pstmt.setString(5, car.getProblemDescription());
            pstmt.setString(6, car.getImagePath());
            pstmt.setString(7, car.getStatus());
            pstmt.setTimestamp(8, Timestamp.valueOf(car.getStartRepairTime()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    car.setId(generatedKeys.getInt(1));
                    return car;
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении автомобиля:");
            e.printStackTrace();
        }
        return null;
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
            pstmt.executeUpdate();

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении автомобиля:");
            e.printStackTrace();
            return false;
        }
    }

    public static List<Car> getUserCars(int ownerId) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT id, name, vin, license_plate, created_at, problem_description, image_path, status " +
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
                        rs.getString("image_path"),
                        rs.getString("status")
                );
                cars.add(car);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении автомобилей:");
            e.printStackTrace();
        }
        return cars;
    }

    public List<Car> getCarsByStatus(String status) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name, u.phone FROM cars c " +
                "LEFT JOIN users u ON c.owner_id = u.id " +
                "WHERE c.status = ?";

        try (Connection conn = DriverManager.getConnection(DB_cars_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Car car = new Car(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("vin"),
                        rs.getString("license_plate"),
                        rs.getInt("owner_id"),
                        rs.getString("problem_description"),
                        rs.getString("image_path"),
                        rs.getString("status")
                );
                cars.add(car);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении автомобилей по статусу:");
            e.printStackTrace();
        }
        return cars;
    }

    public Car getCarById(int carId) {
        String sql = "SELECT * FROM cars WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_cars_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, carId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Car(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("vin"),
                        rs.getString("license_plate"),
                        rs.getInt("owner_id"),
                        rs.getString("problem_description"),
                        rs.getString("image_path"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении автомобиля по ID:");
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateCarStatus(int carId, String newStatus) {
        String sql = "UPDATE cars SET status = ?, start_repair_time = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_cars_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setTimestamp(2, "В ремонте".equals(newStatus) ?
                    Timestamp.valueOf(LocalDateTime.now()) : null);
            pstmt.setInt(3, carId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean completeCarRepair(Car car, String partsUsed) {
        String sql = "UPDATE cars SET status = 'Ремонт выполнен', end_repair_time = ?, " +
                "problem_description = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_cars_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(2, car.getProblemDescription() + "\nИспользованные детали: " + partsUsed);
            pstmt.setInt(3, car.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCarRepairInfo(Car car) {
        String sql = "UPDATE cars SET problem_description = ?, status = ?, start_repair_time = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_cars_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, car.getProblemDescription());
            pstmt.setString(2, car.getStatus());
            pstmt.setTimestamp(3, Timestamp.valueOf(car.getStartRepairTime()));
            pstmt.setInt(4, car.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении информации о ремонте:");
            e.printStackTrace();
            return false;
        }
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
                        rs.getString("image_path"),
                        rs.getString("status")
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
            return -1;

        } catch (SQLException e) {
            System.err.println("Ошибка при получении ID пользователя:");
            e.printStackTrace();
            return -1;
        }
    }

    public boolean updateWorkerProfile(String login, String fullName, String phone) {
        String sql = "UPDATE workers SET full_name = ?, phone = ? WHERE login = ?";

        try (Connection conn = DriverManager.getConnection(DB_workers_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fullName);
            pstmt.setString(2, phone);
            pstmt.setString(3, login);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении профиля сотрудника:");
            e.printStackTrace();
            return false;
        }
    }

    public User getWorkerByLogin(String login) {
        String sql = "SELECT login, full_name, phone FROM workers WHERE login = ?";
        try (Connection conn = DriverManager.getConnection(DB_workers_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        -1, // ID не используется для сотрудников
                        rs.getString("login"),
                        rs.getString("full_name"),
                        rs.getString("phone")
                );
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении данных сотрудника:");
            e.printStackTrace();
        }
        return null;
    }

    public List<Car> getArchivedRepairsByOwner(int ownerId) {
        List<Car> archivedCars = new ArrayList<>();
        String sql = "SELECT * FROM repairs_archive WHERE owner_id = ? ORDER BY archived_at DESC";

        try (Connection conn = DriverManager.getConnection(DB_cars_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ownerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Car car = new Car(
                        rs.getInt("car_id"),
                        rs.getString("name"),
                        rs.getString("vin"),
                        rs.getString("license_plate"),
                        rs.getInt("owner_id"),
                        rs.getString("problem_description"),
                        rs.getString("image_path"),
                        rs.getString("status")
                );
                archivedCars.add(car);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке архивных ремонтов:");
            e.printStackTrace();
        }
        return archivedCars;
    }
}
