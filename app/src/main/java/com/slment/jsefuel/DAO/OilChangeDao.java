package com.slment.jsefuel.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.slment.jsefuel.Entities.Fuel;
import com.slment.jsefuel.Entities.OilChange;
import java.util.List;

@Dao
public interface OilChangeDao {
    @Query("SELECT * FROM oil_change ORDER BY vehicle_id, oil_change_date")
    List<OilChange> getAllOilChanges();

    @Query("SELECT * FROM oil_change WHERE oil_change_id = 0")
    List<OilChange> getNewOilChanges();

    @Query("SELECT * FROM oil_change WHERE :oilChangeId = oil_change_id")
    OilChange getOilChange(int oilChangeId);

    @Query("SELECT * FROM oil_change WHERE :vehId = vehicle_id AND :dateTime = oil_change_date")
    OilChange getOilChangeByVehPlusDate(int vehId, long dateTime);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertOilChange(OilChange oilChange);

    @Update
    void updateOilChange(OilChange oilChange);

}
