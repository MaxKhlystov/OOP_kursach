package repository;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:users.db";

    public DatabaseManager() {
        System.out.println("Попытка подключения к базе данных...");
        createDatabase();
    }

    private void createDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            System.out.println("Подключение к базе данных успешно установлено.");
            String sqlWorkers = "CREATE TABLE IF NOT EXISTS workers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "login TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "access_key TEXT NOT NULL)";
            stmt.execute(sqlWorkers);

            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "login TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL)";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Ошибка при подключении к базе данных:");
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
}
