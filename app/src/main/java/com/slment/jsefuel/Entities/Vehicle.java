package com.slment.jsefuel.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
    tableName = "vehicle",
    foreignKeys = {
        @ForeignKey(entity = Employee.class,
            parentColumns = "employee_id",
            childColumns = "employee_id"
        )
    },
    indices = {
        @Index(name = "vehNum", value = "vehicle_num", unique = true),
        @Index(name = "license", value = "license_plate", unique = true),
    }
)

public class Vehicle {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "vehicle_id")
    private int vehicleId;
    @ColumnInfo(name = "vehicle_num")
    private String vehicleNum;
    @ColumnInfo(name = "vehicle_type")
    private String vehicleType;
    @ColumnInfo(name = "vin")
    private String vin;
    @ColumnInfo(name = "model_year")
    private String modelYear;
    @ColumnInfo(name = "make")
    private String make;
    @ColumnInfo(name = "model")
    private String model;
    @ColumnInfo(name = "active")
    private boolean active;
    @ColumnInfo(name = "employee_id")
    private int employeeId;
    @ColumnInfo(name = "license_plate")
    private String licensePlate;
    @ColumnInfo(name = "requires_cdl")
    private boolean requiresCdl;
    @ColumnInfo(name = "fuel_type")
    private int fuelType;
    @ColumnInfo(name = "fuel_capicity")
    private int fuelCapacity;
    @ColumnInfo(name = "begin_miles")
    private int beginMiles;
    @ColumnInfo(name = "last_fill_miles")
    private float lastFillMiles;
    @ColumnInfo(name = "last_oil_change")
    private int lastOilChange;
    @ColumnInfo(name = "oil_change_frequency")
    private int oilChangeFrequency;
    @ColumnInfo(name = "recommended_oil")
    private String recommendedOil;
    @ColumnInfo(name = "last_updated")
    private Date lastUpdated;
    @ColumnInfo(name = "last_updated_by")
    private int lastUpdatedBy;

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleNum() {
        return vehicleNum;
    }

    public void setVehicleNum(String vehicleNum) {
        this.vehicleNum = vehicleNum;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getModelYear() {
        return modelYear;
    }

    public void setModelYear(String modelYear) {
        this.modelYear = modelYear;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public boolean getRequiresCdl() { return requiresCdl; }

    public void setRequiresCdl(boolean requiresCdl) { this.requiresCdl = requiresCdl; }

    public int getFuelType() {
        return fuelType;
    }

    public void setFuelType(int fuelType) {
        this.fuelType = fuelType;
    }

    public int getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(int fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public int getBeginMiles() {
        return beginMiles;
    }

    public void setBeginMiles(int beginMiles) {
        this.beginMiles = beginMiles;
    }

    public float getLastFillMiles() {
        return lastFillMiles;
    }

    public void setLastFillMiles(float lastFillMiles) {
        this.lastFillMiles = lastFillMiles;
    }

    public int getLastOilChange() {
        return lastOilChange;
    }

    public void setLastOilChange(int lastOilChange) {
        this.lastOilChange = lastOilChange;
    }

    public int getOilChangeFrequency() {
        return oilChangeFrequency;
    }

    public void setOilChangeFrequency(int oilChangeFrequency) {
        this.oilChangeFrequency = oilChangeFrequency;
    }

    public String getRecommendedOil() {
        return recommendedOil;
    }

    public void setRecommendedOil(String recommendedOil) {
        this.recommendedOil = recommendedOil;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(int lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}
