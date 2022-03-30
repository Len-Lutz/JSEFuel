package com.slment.jsefuel.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        tableName = "provider",
        indices = {@Index(name = "nameAddress", value = {"name", "address"}, unique = true)}
)
public class Provider {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "provider_id")
    private int providerId;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "address")
    private String address;
    @ColumnInfo(name = "city")
    private String city;
    @ColumnInfo(name = "phone")
    private String phone;
    @ColumnInfo(name = "fuel_types")
    private int fuelTypes;
    @ColumnInfo(name = "services")
    private int services;
    @ColumnInfo(name = "active")
    private boolean active;
    @ColumnInfo(name = "last_updated")
    private Date lastUpdated;
    @ColumnInfo(name = "last_updated_by")
    private int lastUpdatedBy;

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getFuelTypes() {
        return fuelTypes;
    }

    public void setFuelTypes(int fuelTypes) {
        this.fuelTypes = fuelTypes;
    }

    public int getServices() {
        return services;
    }

    public void setServices(int services) {
        this.services = services;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
