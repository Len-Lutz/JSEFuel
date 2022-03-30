package com.slment.jsefuel.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.slment.jsefuel.DAO.EmployeeDao;
import com.slment.jsefuel.DAO.FuelDao;
import com.slment.jsefuel.DAO.OilChangeDao;
import com.slment.jsefuel.DAO.ProviderDao;
import com.slment.jsefuel.DAO.VehicleDao;
import com.slment.jsefuel.Entities.Employee;
import com.slment.jsefuel.Entities.Fuel;
import com.slment.jsefuel.Entities.OilChange;
import com.slment.jsefuel.Entities.Provider;
import com.slment.jsefuel.Entities.Vehicle;
import com.slment.jsefuel.utils.Converters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Employee.class, Vehicle.class, Provider.class, Fuel.class, OilChange.class},
        exportSchema = false, version = 1)
@TypeConverters({Converters.class})
public abstract class FuelDatabase extends RoomDatabase {
    public abstract EmployeeDao employeeDao();
    public abstract FuelDao fuelDao();
    public abstract OilChangeDao oilChangeDao();
    public abstract ProviderDao providerDao();
    public abstract VehicleDao vehicleDao();
    private static final int NUMBER_OF_THREADS = 6;

    private static final String DB_NAME = "JSEFuel.db";
    private static volatile FuelDatabase instance;

    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static FuelDatabase getDatabase(final Context context) {
        if(instance == null) {
            synchronized (FuelDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            FuelDatabase.class, DB_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(roomDatabaseCallback)
                            .build();
                }
            }
        }
        return instance;
    }

    private static final RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };
}
