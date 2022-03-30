package com.slment.jsefuel.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.slment.jsefuel.Entities.Vehicle;
import java.util.List;

@Dao
public interface VehicleDao {
    @Query("SELECT * FROM vehicle ORDER BY vehicle_id")
    List<Vehicle> getAllVehicles();

    @Query("SELECT * FROM vehicle WHERE :vehicleId = vehicle_id")
    Vehicle getVehicle(int vehicleId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertVehicle(Vehicle vehicle);

    @Update
    void updateVehicle(Vehicle vehicle);
}
