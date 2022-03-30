package com.slment.jsefuel.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

import com.slment.jsefuel.Database.FuelRepository;
import com.slment.jsefuel.Entities.Provider;

import java.util.List;

public class ProviderViewModel extends AndroidViewModel {
    private FuelRepository repository;
    private List<Provider> providerList;

    public ProviderViewModel(Application application) {
        super(application);
        repository = new FuelRepository(application);
        providerList = repository.getProviderList();
    }

    public List<Provider> getProviderList() {return providerList;}
    public void insert(Provider provider) {repository.insert(provider);}
    public int lastProviderId() {return providerList.get(providerList.size()-1).getProviderId();}
}
