package com.slment.jsefuel.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

import com.slment.jsefuel.Database.FuelRepository;
import com.slment.jsefuel.Entities.OilChange;

import java.util.List;

public class OilViewModel extends AndroidViewModel {
    private FuelRepository repository;
    private List<OilChange> oilChangeList;

    public OilViewModel(Application application) {
        super(application);
        repository = new FuelRepository(application);
        oilChangeList = repository.getOilChangeList();
    }

    public List<OilChange> getOilChangeList() {return oilChangeList;}
    public void insert(OilChange oilChange) {repository.insert(oilChange);}
    public int lastOilChangeId() {return oilChangeList.get(oilChangeList.size()-1).getOilChangeId();}
}
