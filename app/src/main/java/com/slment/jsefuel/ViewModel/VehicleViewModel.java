package com.slment.jsefuel.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

import com.slment.jsefuel.Database.FuelRepository;
import com.slment.jsefuel.Entities.Vehicle;

import java.util.List;

public class VehicleViewModel  extends AndroidViewModel {
    private FuelRepository repository;
    private List<Vehicle> vehicleList;

    public VehicleViewModel(Application application) {
        super(application);
        repository = new FuelRepository(application);
        vehicleList = repository.getVehicleList();
    }

    public List<Vehicle> getVehicleList() {return vehicleList;}
    public void insert(Vehicle vehicle) {repository.insert(vehicle);}
    public int lastVehicleId() {return vehicleList.get(vehicleList.size()-1).getVehicleId();}
}
