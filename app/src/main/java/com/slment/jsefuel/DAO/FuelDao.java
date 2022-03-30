package com.slment.jsefuel.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.slment.jsefuel.Entities.Fuel;
import java.util.List;

@Dao
public interface FuelDao {
    @Query("SELECT * FROM fuel ORDER BY vehicle_id, fill_date")
    List<Fuel> getAllFuels();

    @Query("SELECT * FROM fuel WHERE fuel_id = 0")
    List<Fuel> getNewFuels();

    @Query("SELECT * FROM fuel WHERE :fuelId = fuel_id")
    Fuel getFuel(int fuelId);

    @Query("SELECT * FROM fuel WHERE :vehId = vehicle_id AND :dateTime = fill_date")
    Fuel getFuelByVehPlusDateTime(int vehId, long dateTime);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFuel(Fuel fuel);

    @Update
    void updateFuel(Fuel fuel);
}
