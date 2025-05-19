package model;

import java.time.LocalDateTime;

public class Car {
    private int id;
    private String name;
    private String vin;
    private String licensePlate;
    private int ownerId;
    private LocalDateTime createdAt;
    private String problemDescription;

    public Car(String name, String vin, String licensePlate, int ownerId, String problemDescription) {
        this.name = name;
        this.vin = vin;
        this.licensePlate = licensePlate;
        this.ownerId = ownerId;
        this.problemDescription = problemDescription;
        this.createdAt = LocalDateTime.now();
    }

    public Car(int id, String name, String vin, String licensePlate, int ownerId, LocalDateTime createdAt, String problemDescription) {
        this.id = id;
        this.name = name;
        this.vin = vin;
        this.licensePlate = licensePlate;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.problemDescription = problemDescription;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public String getVin() { return vin; }
    public String getLicensePlate() { return licensePlate; }
    public int getOwnerId() { return ownerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getProblemDescription() { return problemDescription; }

    public void setName(String name) { this.name = name; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }

    @Override
    public String toString() {
        return String.format("%s (VIN: %s, Гос.номер: %s)", name, vin, licensePlate);
    }
}