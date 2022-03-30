package com.slment.jsefuel.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(primaryKeys = {"vehicle_id", "fill_date"},
    tableName = "fuel",
    foreignKeys = {
        @ForeignKey(
            entity = Employee.class,
            parentColumns = "employee_id",
            childColumns = "employee_id"
        ),
        @ForeignKey(
            entity = Vehicle.class,
            parentColumns = "vehicle_id",
            childColumns = "vehicle_id",
            onDelete = CASCADE
        ),
        @ForeignKey(
            entity = Provider.class,
            parentColumns = "provider_id",
            childColumns = "provider_id"
        )
    },
    indices = {
//        @Index(name = "vehicleFill", value = {"vehicle_id", "fill_date"}, unique = true),
        @Index(name = "fuelVehicleId", value = "vehicle_id")
    }
)
public class Fuel {
    @ColumnInfo(name = "fuel_id")
    private int fuelId;
    @NonNull
    @ColumnInfo(name = "vehicle_id")
    private int vehicleId;
    @ColumnInfo(name = "employee_id")
    private int employeeId;
    @NonNull
    @ColumnInfo(name = "fill_date")
    private Date fillDate;
    @ColumnInfo(name = "miles")
    private float miles;
    @ColumnInfo(name = "odometer")
    private float odometer;
    @ColumnInfo(name = "quantity")
    private float quantity;
    @ColumnInfo(name = "fill_cost")
    private float fillCost;
    @ColumnInfo(name = "provider_id")
    private int providerId;
    @ColumnInfo(name = "last_updated")
    private Date lastUpdated;
    @ColumnInfo(name = "last_updated_by")
    private int lastUpdatedBy;

    public int getFuelId() {
        return fuelId;
    }

    public void setFuelId(int fuelId) {
        this.fuelId = fuelId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public Date getFillDate() {
        return fillDate;
    }

    public void setFillDate(Date fillDate) {
        this.fillDate = fillDate;
    }

    public float getMiles() {
        return miles;
    }

    public void setMiles(float miles) {
        this.miles = miles;
    }

    public float getOdometer() {
        return odometer;
    }

    public void setOdometer(float odometer) {
        this.odometer = odometer;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getFillCost() {
        return fillCost;
    }

    public void setFillCost(float fillCost) {
        this.fillCost = fillCost;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
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
