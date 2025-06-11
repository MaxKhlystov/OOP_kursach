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
    private String imagePath;

    public Car(String name, String vin, String licensePlate, int ownerId, String problemDescription, String imagePath) {
        this.name = name;
        this.vin = vin;
        this.licensePlate = licensePlate;
        this.ownerId = ownerId;
        this.problemDescription = problemDescription;
        this.imagePath = imagePath;
    }

    public Car(int id, String name, String vin, String licensePlate, int ownerId, String problemDescription, String imagePath) {
        this.id = id;
        this.name = name;
        this.vin = vin;
        this.licensePlate = licensePlate;
        this.ownerId = ownerId;
        this.problemDescription = problemDescription;
        this.imagePath = imagePath;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public String getVin() { return vin; }
    public String getLicensePlate() { return licensePlate; }
    public int getOwnerId() { return ownerId; }
    public String getProblemDescription() { return problemDescription; }
    public String getImagePath() { return imagePath; }

    public void setName(String name) { this.name = name; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath ;}
    public void setVin(String vin) { this.vin = vin; }

    @Override
    public String toString() {
        return String.format("%s (VIN: %s, Гос.номер: %s)", name, vin, licensePlate);
    }
}
