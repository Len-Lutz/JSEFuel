package com.slment.jsefuel.Database;

import android.app.Application;
import android.util.Log;

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

import java.util.List;

public class FuelRepository {
    private final EmployeeDao employeeDao;
    private final FuelDao fuelDao;
    private final OilChangeDao oilChangeDao;
    private final ProviderDao providerDao;
    private final VehicleDao vehicleDao;
    private List<Employee> repEmployeeList;
    private List<Fuel> repFuelList;
    private List<Fuel> repNewFuelList;
    private List<OilChange> repOilChangeList;
    private List<OilChange> repNewOilChangeList;
    private List<Provider> repProviderList;
    private List<Vehicle> repVehicleList;
    private Employee currentEmp;
    private Fuel tempFuel;
    private OilChange tempOilChange;
    
    public FuelRepository(Application application){
        FuelDatabase db = FuelDatabase.getDatabase(application);
        employeeDao = db.employeeDao();
        fuelDao = db.fuelDao();
        oilChangeDao = db.oilChangeDao();
        providerDao = db.providerDao();
        vehicleDao = db.vehicleDao();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<Employee> getEmployeeList() {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            repEmployeeList = employeeDao.getAllEmployees();
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return repEmployeeList;
    }

    public Employee getEmployeeByWorkCell(String phoneNumber) {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            currentEmp = employeeDao.getEmployeeByWorkCell(phoneNumber);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return currentEmp;
    }

    public Fuel getFuelByVehPlusDateTime(int vehId, long dateTime) {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            tempFuel = fuelDao.getFuelByVehPlusDateTime(vehId, dateTime);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return tempFuel;
    }

    public List<Fuel> getFuelList() {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            repFuelList = fuelDao.getAllFuels();
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return repFuelList;
    }

    public List<Fuel> getNewFuels() {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            repNewFuelList = fuelDao.getNewFuels();
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return repNewFuelList;
    }

    public OilChange getOilChangeByVehPlusDate(int vehId, long dateTime) {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            tempOilChange = oilChangeDao.getOilChangeByVehPlusDate(vehId, dateTime);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return tempOilChange;
    }

    public List<OilChange> getOilChangeList() {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            repOilChangeList = oilChangeDao.getAllOilChanges();
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return repOilChangeList;
    }

    public List<OilChange> getNewOilChanges() {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            repNewOilChangeList = oilChangeDao.getNewOilChanges();
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return repNewOilChangeList;
    }

    public List<Provider> getProviderList() {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            repProviderList = providerDao.getAllProviders();
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return repProviderList;
    }

    public List<Vehicle> getVehicleList() {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            repVehicleList = vehicleDao.getAllVehicles();
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return repVehicleList;
    }

    public void insert(Employee employee) {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            employeeDao.insertEmployee(employee);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void insert(Fuel fuel) {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            fuelDao.insertFuel(fuel);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void insert(OilChange oilChange) {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            oilChangeDao.insertOilChange(oilChange);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void insert(Provider provider) {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            providerDao.insertProvider(provider);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void insert(Vehicle vehicle) {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            vehicleDao.insertVehicle(vehicle);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(Employee employee) {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            employeeDao.updateEmployee(employee);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(Fuel fuel) {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            fuelDao.updateFuel(fuel);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(OilChange oilChange) {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            oilChangeDao.updateOilChange(oilChange);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(Provider provider) {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            providerDao.updateProvider(provider);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(Vehicle vehicle) {
        FuelDatabase.databaseWriteExecutor.execute(()-> {
            vehicleDao.updateVehicle(vehicle);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
