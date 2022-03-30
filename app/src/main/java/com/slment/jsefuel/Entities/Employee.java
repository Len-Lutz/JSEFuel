package com.slment.jsefuel.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
    tableName = "employee",
    indices = {
        @Index(name = "name", value = {"last_name", "first_name", "middle_name"}),
        @Index(name = "empVehicle", value = "vehicle_id"),
        @Index(name = "workCell", value = "work_cell")
    }
)

public class Employee {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "employee_id")
    private int employeeId;
    @ColumnInfo(name = "last_name")
    @NonNull
    private String lastName;
    @ColumnInfo(name = "first_name")
    @NonNull
    private String firstName;
    @ColumnInfo(name = "middle_name")
    private String middleName;
    @ColumnInfo(name = "birth_date")
    private Date birthDate;
    @ColumnInfo(name = "active")
    private boolean active;
    @ColumnInfo(name = "license_expires")
    private Date licenseExpires;
    @ColumnInfo(name = "license_cdl")
    private boolean licenseCdl;
    @ColumnInfo(name = "work_cell")
    private String workCell;
    @ColumnInfo(name = "vehicle_id")
    private int vehicleId;
    @ColumnInfo(name = "last_data_sync")
    private Date lastDataSync;
    @ColumnInfo(name = "last_updated")
    private Date lastUpdated;
    @ColumnInfo(name = "last_updated_by")
    private int lastUpdatedBy;

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    @NonNull
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@NonNull String lastName) {
        this.lastName = lastName;
    }

    @NonNull
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NonNull String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getLicenseExpires() {
        return licenseExpires;
    }

    public void setLicenseExpires(Date licenseExpires) {
        this.licenseExpires = licenseExpires;
    }

    public boolean isLicenseCdl() {
        return licenseCdl;
    }

    public void setLicenseCdl(boolean licenseCdl) {
        this.licenseCdl = licenseCdl;
    }

    public String getWorkCell() {
        return workCell;
    }

    public void setWorkCell(String workCell) {
        this.workCell = workCell;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Date getLastDataSync() {
        return lastDataSync;
    }

    public void setLastDataSync(Date lastDataSync) {
        this.lastDataSync = lastDataSync;
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
