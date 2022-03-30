package com.slment.jsefuel.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

import com.slment.jsefuel.Database.FuelRepository;
import com.slment.jsefuel.Entities.Fuel;

import java.util.List;

public class FuelViewModel extends AndroidViewModel {
    private FuelRepository repository;
    private List<Fuel> fuelList;

    public FuelViewModel(Application application) {
        super(application);
        repository = new FuelRepository(application);
        fuelList = repository.getFuelList();
    }

    public List<Fuel> getFuelList() {return fuelList;}
    public void insert(Fuel fuel) {repository.insert(fuel);}
    public int lastFuelId() {return fuelList.get(fuelList.size()-1).getFuelId();}
}
