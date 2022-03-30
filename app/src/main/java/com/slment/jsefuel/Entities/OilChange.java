package com.slment.jsefuel.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(primaryKeys = {"vehicle_id", "oil_change_date"},
    tableName = "oil_change",
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
//        @Index(name = "vehicleOil", value = {"vehicle_id", "oil_change_date"}, unique = true),
        @Index(name = "oilVehicleId", value = "vehicle_id")
    }
)
public class OilChange {
    @ColumnInfo(name = "oil_change_id")
    private int oilChangeId;
    @NonNull
    @ColumnInfo(name = "vehicle_id")
    public int vehicleId;
    @ColumnInfo(name = "employee_id")
    public int employeeId;
    @NonNull
    @ColumnInfo(name = "oil_change_date")
    public Date oilChangeDate;
    @ColumnInfo(name = "odometer")
    public float odometer;
    @ColumnInfo(name = "total_cost")
    public float totalCost;
    @ColumnInfo(name = "provider_id")
    public int providerId;
    @ColumnInfo(name = "last_updated")
    public Date lastUpdated;
    @ColumnInfo(name = "last_updated_by")
    public int lastUpdatedBy;

    public int getOilChangeId() {
        return oilChangeId;
    }

    public void setOilChangeId(int oilChangeId) {
        this.oilChangeId = oilChangeId;
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

    public Date getOilChangeDate() {
        return oilChangeDate;
    }

    public void setOilChangeDate(Date oilChangeDate) {
        this.oilChangeDate = oilChangeDate;
    }

    public float getOdometer() {
        return odometer;
    }

    public void setOdometer(float odometer) {
        this.odometer = odometer;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
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
