package com.slment.jsefuel.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.slment.jsefuel.Entities.Provider;
import java.util.List;

@Dao
public interface ProviderDao {
    @Query("SELECT * FROM provider ORDER BY name, address")
    List<Provider> getAllProviders();

    @Query("SELECT * FROM provider WHERE :providerId = provider_id")
    Provider getProvider(int providerId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProvider(Provider provider);

    @Update
    void updateProvider(Provider provider);
}
