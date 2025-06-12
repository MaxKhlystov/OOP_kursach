package model;

public class User {
    private int id;
    private String login;
    private String fullName;
    private String phone;

    public User(int id, String login, String fullName, String phone) {
        this.id = id;
        this.login = login;
        this.fullName = fullName;
        this.phone = phone;
    }

    // Геттеры
    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    // Сеттеры
    public void setLogin(String login) {
        this.login = login;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Удобный метод для проверки заполненности профиля
    public boolean isProfileIncomplete() {
        return (fullName == null || fullName.trim().isEmpty()) ||
                (phone == null || phone.trim().isEmpty());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}