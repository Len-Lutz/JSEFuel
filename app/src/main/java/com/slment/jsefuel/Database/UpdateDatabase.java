package com.slment.jsefuel.Database;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.slment.jsefuel.Entities.Employee;
import com.slment.jsefuel.Entities.Fuel;
import com.slment.jsefuel.Entities.OilChange;
import com.slment.jsefuel.Entities.Provider;
import com.slment.jsefuel.Entities.Vehicle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Float.parseFloat;

public class UpdateDatabase extends AppCompatActivity {
    public Connection conn = null;
    public static String LOG_TAG = "##### UpdateDatabase: ";
    private String lastUpdate = "2018-01-01 01:01:01";
    private String strConnection = null;
    private String strUser = null;
    private String strPassword= null;
    private String workCell = null;
    public ProgressDialog progDlg = null;
    protected Context context;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdfmdy = new SimpleDateFormat("MM-dd-yyyy");
    SimpleDateFormat sdfymd = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdftime24 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdftime12 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private FuelRepository repository;
    public boolean haveDb = false;
    public boolean closeWhenDone = false;
    public Employee currentEmp = null;

    public void doUpdate(Context context, String date, boolean closeWhenDone, String workCell) {
        this.context = context;
        this.lastUpdate = date;
        this.closeWhenDone = closeWhenDone;
        this.workCell = workCell;

         strConnection = "jdbc:mysql://192.168.0.199:3306/jsefuel_db";
         strUser = "Len";
         strPassword = "D7lsi2357";


        progDlg = new ProgressDialog(context);
        Log.println(Log.VERBOSE, LOG_TAG, "Attempting to bring up Progress Dialog (in ud).");
        progDlg.setTitle("Updating Data...");
        progDlg.setMessage("\nPlease wait...");
        progDlg.setIndeterminate(false);
        progDlg.setCancelable(false);
        progDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progDlg.setProgress(0);
        progDlg.setMax(0);
        progDlg.show();

        Log.println(Log.VERBOSE, LOG_TAG, "Calling Async task.");
        new Async().execute();
        Log.println(Log.VERBOSE, LOG_TAG, "Async task should be running.");
    }

    class Async extends AsyncTask<Void, Integer, Void> {
        Integer count = 0;

        @Override
        protected Void doInBackground(Void... voids) {
            Log.println(Log.VERBOSE, LOG_TAG, "In background task.");
            Log.println(Log.VERBOSE, LOG_TAG, "Creating repository.");
            repository = new FuelRepository(getApplication());
            ResultSet employeeSet;
            ResultSet vehicleSet;
            ResultSet providerSet;
            ResultSet fuelSet;
            ResultSet oilChangeSet;
            int employeeCount = 0;
            int vehicleCount = 0;
            int providerCount = 0;
            int fuelCount = 0;
            int oilChangeCount = 0;
            Employee tempEmployee = new Employee();
            Vehicle tempVehicle = new Vehicle();
            Provider tempProvider = new Provider();
            Fuel tempFuel = new Fuel();
            OilChange tempOilChange = new OilChange();
            int employeeId = 0;
            boolean exceptionThrown = false;
            Date updateTime;
            currentEmp = repository.getEmployeeByWorkCell(workCell);
            if (currentEmp != null) {
                employeeId = currentEmp.getEmployeeId();
            }

            String sqlStr;

            try {
                Calendar workDate = Calendar.getInstance();
                workDate.set(Calendar.SECOND, 0);
                workDate.set(Calendar.MILLISECOND, 0);
                updateTime = workDate.getTime();
                Class.forName("com.mysql.jdbc.Driver");
                Log.println(Log.VERBOSE, LOG_TAG, "Connecting to external database.");
                conn = DriverManager.getConnection(strConnection, strUser, strPassword);
                Statement statement = conn.createStatement();
                publishProgress(0, 0, 0);

                Log.println(Log.VERBOSE, LOG_TAG, "Gathering Employee Records newer than: " + lastUpdate);
                sqlStr = "SELECT * FROM Employee WHERE lastUpdated > '" + lastUpdate + "'";
                employeeSet = statement.executeQuery(sqlStr);
                if(employeeSet.last()){
                    employeeCount = employeeSet.getRow();
                    employeeSet = statement.executeQuery(sqlStr);
                }

                if(employeeCount > 0){
                    count = 0;
                    Log.println(Log.VERBOSE, LOG_TAG, "Saving Employee Records.");
                    while(employeeSet.next()){
                        // fill tempEmployee with data from company database
                        tempEmployee.setEmployeeId(employeeSet.getInt("employeeId"));
                        tempEmployee.setLastName(employeeSet.getString("lastName"));
                        tempEmployee.setFirstName(employeeSet.getString("firstName"));
                        tempEmployee.setMiddleName(employeeSet.getString("middleName"));
                        workDate.setTime(sdfymd.parse(employeeSet.getString("birthDate"),
                                new ParsePosition(0)));
                        tempEmployee.setBirthDate(workDate.getTime());
                        tempEmployee.setActive(employeeSet.getInt("active") == 0 ? false : true);
                        workDate.setTime(sdfymd.parse(employeeSet.getString("licenseExpires"),
                                new ParsePosition(0)));
                        tempEmployee.setLicenseExpires(workDate.getTime());
                        tempEmployee.setLicenseCdl(employeeSet.getInt("licenseCdl") == 0 ? false : true);
                        tempEmployee.setWorkCell(employeeSet.getString("workCell"));
                        tempEmployee.setVehicleId(employeeSet.getInt("vehicleId"));
                        workDate.setTime(sdftime24.parse(employeeSet.getString("lastDataSync"),
                                new ParsePosition(0)));
                        tempEmployee.setLastDataSync(workDate.getTime());
                        workDate.setTime(sdftime24.parse(employeeSet.getString("lastUpdated"),
                                new ParsePosition(0)));
                        tempEmployee.setLastUpdated(workDate.getTime());
                        tempEmployee.setLastUpdatedBy(employeeSet.getInt("lastUpdatedBy"));
                        if (tempEmployee.getWorkCell().matches(workCell)) {
                            employeeId = tempEmployee.getEmployeeId();
                        }

                        // Try to insert record.  Our insert procedure is set to IGNORE ON CONFLICT,
                        // so, if the record already exists, nothing happens.  Then we do an UPDATE.
                        // If the record was inserted, the update is redundant, but if the record
                        // was NOT inserted, an update is required.  This method ensures that the
                        // record (or changes to it) actually make it into the database.
                        // The redundancy should not cause significant overhead because most of
                        // the time we are only working with a few records in some of the tables,
                        // while other tables may not have any new information since the last
                        // update.
                        repository.insert(tempEmployee);
                        repository.update(tempEmployee);

                        publishProgress(++count, employeeCount, 1);
                    }
                }

                Log.println(Log.VERBOSE, LOG_TAG, "Gathering Vehicle Records.");
                sqlStr = "SELECT * FROM Vehicle WHERE lastUpdated > '" + lastUpdate + "'";
                vehicleSet = statement.executeQuery(sqlStr);
                if(vehicleSet.last()){
                    vehicleCount = vehicleSet.getRow();
                    vehicleSet = statement.executeQuery(sqlStr);
                }

                if(vehicleCount > 0){
                    count = 0;
                    Log.println(Log.VERBOSE, LOG_TAG, "Saving Vehicle Records.");
                    while(vehicleSet.next()){
                        // fill tempVehicle with data from company database
                        tempVehicle.setVehicleId(vehicleSet.getInt("vehicleId"));
                        tempVehicle.setVehicleNum(vehicleSet.getString("vehicleNum"));
                        tempVehicle.setVehicleType(vehicleSet.getString("vehicleType"));
                        tempVehicle.setVin(vehicleSet.getString("vin"));
                        tempVehicle.setModelYear(vehicleSet.getString("modelYear"));
                        tempVehicle.setMake(vehicleSet.getString("make"));
                        tempVehicle.setModel(vehicleSet.getString("model"));
                        tempVehicle.setActive(vehicleSet.getInt("active") == 0 ? false : true);
                        tempVehicle.setEmployeeId(vehicleSet.getInt("employeeId"));
                        tempVehicle.setLicensePlate(vehicleSet.getString("licensePlate"));
                        tempVehicle.setRequiresCdl(vehicleSet.getInt("requiresCdl") == 0 ? false : true);
                        tempVehicle.setFuelType(vehicleSet.getInt("fuelType"));
                        tempVehicle.setFuelCapacity(vehicleSet.getInt("fuelCapacity"));
                        tempVehicle.setBeginMiles(vehicleSet.getInt("beginMiles"));
                        tempVehicle.setLastFillMiles(vehicleSet.getFloat("lastFillMiles"));
                        tempVehicle.setLastOilChange(vehicleSet.getInt("lastOilChange"));
                        tempVehicle.setOilChangeFrequency(vehicleSet.getInt("oilChangeFrequency"));
                        tempVehicle.setRecommendedOil(vehicleSet.getString("recommendedOil"));
                        workDate.setTime(sdftime24.parse(vehicleSet.getString("lastUpdated"),
                                new ParsePosition(0)));
                        tempVehicle.setLastUpdated(workDate.getTime());
                        tempVehicle.setLastUpdatedBy(vehicleSet.getInt("lastUpdatedBy"));

                        // Try to insert record.  Our insert procedure is set to IGNORE ON CONFLICT,
                        // so, if the record already exists, nothing happens.  Then we do an UPDATE.
                        // If the record was inserted, the update is redundant, but if the record
                        // was NOT inserted, an update is required.  This method ensures that the
                        // record (or changes to it) actually make it into the database.
                        // The redundancy should not cause significant overhead because most of
                        // the time we are only working with a few records in some of the tables,
                        // while other tables may not have any new information since the last
                        // update.
                        repository.insert(tempVehicle);
                        repository.update(tempVehicle);

                        publishProgress(++count, vehicleCount, 2);
                    }
                }

                Log.println(Log.VERBOSE, LOG_TAG, "Gathering Provider Records.");
                sqlStr = "SELECT * FROM Provider WHERE lastUpdated > '" + lastUpdate + "'";
                providerSet = statement.executeQuery(sqlStr);
                if(providerSet.last()){
                    providerCount = providerSet.getRow();
                    providerSet = statement.executeQuery(sqlStr);
                }

                if(providerCount > 0){
                    count = 0;
                    Log.println(Log.VERBOSE, LOG_TAG, "Saving Provider Records");
                    while(providerSet.next()){
                        // fill tempProvider with data from company database
                        tempProvider.setProviderId(providerSet.getInt("providerId"));
                        tempProvider.setName(providerSet.getString("name"));
                        tempProvider.setAddress(providerSet.getString("address"));
                        tempProvider.setCity(providerSet.getString("city"));
                        tempProvider.setPhone(providerSet.getString("phone"));
                        tempProvider.setFuelTypes(providerSet.getInt("fuelTypes"));
                        tempProvider.setServices(providerSet.getInt("services"));
                        tempProvider.setActive(providerSet.getInt("active") == 0 ? false : true);
                        workDate.setTime(sdftime24.parse(providerSet.getString("lastUpdated"),
                                new ParsePosition(0)));
                        tempProvider.setLastUpdated(workDate.getTime());
                        tempProvider.setLastUpdatedBy(providerSet.getInt("lastUpdatedBy"));


                        // Try to insert record.  Our insert procedure is set to IGNORE ON CONFLICT,
                        // so, if the record already exists, nothing happens.  Then we do an UPDATE.
                        // If the record was inserted, the update is redundant, but if the record
                        // was NOT inserted, an update is required.  This method ensures that the
                        // record (or changes to it) actually make it into the database.
                        // The redundancy should not cause significant overhead because most of
                        // the time we are only working with a few records in some of the tables,
                        // while other tables may not have any new information since the last
                        // update.
                        repository.insert(tempProvider);
                        repository.update(tempProvider);

                        publishProgress(++count, providerCount, 3);
                    }
                }

                Log.println(Log.VERBOSE, LOG_TAG, "Gathering Fuel Records.");
                sqlStr = "SELECT * FROM Fuel WHERE lastUpdated > '" + lastUpdate + "'";
                fuelSet = statement.executeQuery(sqlStr);
                if(fuelSet.last()){
                    fuelCount = fuelSet.getRow();
                    fuelSet = statement.executeQuery(sqlStr);
                }

                if(fuelCount > 0){
                    count = 0;
                    Log.println(Log.VERBOSE, LOG_TAG, "Saving Fuel Records.");
                    while(fuelSet.next()){
                        // fill tempFuel with data from company database
                        tempFuel.setFuelId(fuelSet.getInt("fuelId"));
                        tempFuel.setVehicleId(fuelSet.getInt("vehicleId"));
                        tempFuel.setEmployeeId(fuelSet.getInt("employeeId"));
                        workDate.setTime(sdftime24.parse(fuelSet.getString("fillDate"),
                                new ParsePosition(0)));
                        tempFuel.setFillDate(workDate.getTime());
                        tempFuel.setMiles(parseFloat(fuelSet.getString("miles")));
                        tempFuel.setOdometer(parseFloat(fuelSet.getString("odometer")));
                        tempFuel.setQuantity(parseFloat(fuelSet.getString("quantity")));
                        tempFuel.setFillCost(parseFloat(fuelSet.getString("fillCost")));
                        tempFuel.setProviderId(fuelSet.getInt("providerId"));
                        workDate.setTime(sdftime24.parse(fuelSet.getString("lastUpdated"),
                                new ParsePosition(0)));
                        tempFuel.setLastUpdated(workDate.getTime());
                        tempFuel.setLastUpdatedBy(fuelSet.getInt("lastUpdatedBy"));


                        // Try to insert record.  Our insert procedure is set to IGNORE ON CONFLICT,
                        // so, if the record already exists, nothing happens.  Then we do an UPDATE.
                        // If the record was inserted, the update is redundant, but if the record
                        // was NOT inserted, an update is required.  This method ensures that the
                        // record (or changes to it) actually make it into the database.
                        // The redundancy should not cause significant overhead because most of
                        // the time we are only working with a few records in some of the tables,
                        // while other tables may not have any new information since the last
                        // update.
                        repository.insert(tempFuel);
                        repository.update(tempFuel);

                        publishProgress(++count, fuelCount, 4);
                    }
                }

                Log.println(Log.VERBOSE, LOG_TAG, "Gathering OilChange Records.");
                sqlStr = "SELECT * FROM OilChange WHERE lastUpdated > '" + lastUpdate + "'";
                oilChangeSet = statement.executeQuery(sqlStr);
                if(oilChangeSet.last()){
                    oilChangeCount = oilChangeSet.getRow();
                    oilChangeSet = statement.executeQuery(sqlStr);
                }

                if(oilChangeCount > 0){
                    count = 0;
                    Log.println(Log.VERBOSE, LOG_TAG, "Saving OilChange Records.");
                    while(oilChangeSet.next()){
                        // fill tempOilChange with data from company database
                        tempOilChange.setOilChangeId(oilChangeSet.getInt("oilChangeId"));
                        tempOilChange.setVehicleId(oilChangeSet.getInt("vehicleId"));
                        tempOilChange.setEmployeeId(oilChangeSet.getInt("employeeId"));
                        workDate.setTime(sdfymd.parse(oilChangeSet.getString("oilChangeDate"),
                                new ParsePosition(0)));
                        tempOilChange.setOilChangeDate(workDate.getTime());
                        tempOilChange.setOdometer(parseFloat(oilChangeSet.getString("odometer")));
                        tempOilChange.setTotalCost(parseFloat(oilChangeSet.getString("totalCost")));
                        tempOilChange.setProviderId(oilChangeSet.getInt("providerId"));
                        workDate.setTime(sdftime24.parse(oilChangeSet.getString("lastUpdated"),
                                new ParsePosition(0)));
                        tempOilChange.setLastUpdated(workDate.getTime());
                        tempOilChange.setLastUpdatedBy(oilChangeSet.getInt("lastUpdatedBy"));

                        // Try to insert record.  Our insert procedure is set to IGNORE ON CONFLICT,
                        // so, if the record already exists, nothing happens.  Then we do an UPDATE.
                        // If the record was inserted, the update is redundant, but if the record
                        // was NOT inserted, an update is required.  This method ensures that the
                        // record (or changes to it) actually make it into the database.
                        // The redundancy should not cause significant overhead because most of
                        // the time we are only working with a few records in some of the tables,
                        // while other tables may not have any new information since the last
                        // update.
                        repository.insert(tempOilChange);
                        repository.update(tempOilChange);

                        publishProgress(++count, oilChangeCount, 5);
                    }
                }
                Log.println(Log.VERBOSE, LOG_TAG, "Done saving records.");

                if ((employeeCount + vehicleCount + providerCount + fuelCount +oilChangeCount > 0)) {
                    try {
                        String updateStr = sdftime24.format(updateTime);
                        sqlStr = "UPDATE Employee SET lastUpdated = '" + updateStr +
                                "', lastUpdatedBy = " + employeeId +
                                ", lastDataSync = '" + updateStr +
                                "' WHERE workCell = '" + workCell + "'";
                        statement.executeUpdate(sqlStr);
                        Log.println(Log.VERBOSE, LOG_TAG, "lastUpdated set to: " + updateStr);
                        tempEmployee = repository.getEmployeeByWorkCell(workCell);
                        tempEmployee.setLastDataSync(updateTime);
                        repository.update(tempEmployee);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        Log.println(Log.VERBOSE, LOG_TAG,
                                "Problem Updating Employee Record with new lastSyncDate.\n\t\t" +
                                        "sqlStr = ##" + sqlStr + "##");
                        exceptionThrown = true;
                    } finally {
                        if (!exceptionThrown) {
                            Log.println(Log.VERBOSE, LOG_TAG,
                                    "Updated Current Employee Record's lastUpdated info.");
                        }
                    }
                } else {
                    publishProgress(0, 0, 6);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                Log.println(Log.VERBOSE, LOG_TAG, e.toString());
                progDlg.dismiss();
            } finally {
                try {
                    Log.println(Log.VERBOSE, LOG_TAG, "Closing external database connection.");
                    if(conn != null) conn.close();
                } catch (SQLException se) {
                    Log.println(Log.VERBOSE, LOG_TAG, "SQL Exception on conn.close");
                }
            }
            Log.println(Log.VERBOSE, LOG_TAG, "Done with update!");
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progDlg.setProgress(progress[0]);
            progDlg.setMax(progress[1]);
            switch (progress[2]){
                case 0:
                    progDlg.setMessage("\nGathering Data From Server...");
                    break;
                case 1:
                    progDlg.setMessage("\nUpdating Employees...");
                    break;
                case 2:
                    progDlg.setMessage("\nUpdating Vehicles...");
                    break;
                case 3:
                    progDlg.setMessage("\nUpdating Providers...");
                    break;
                case 4:
                    progDlg.setMessage("\nUpdating Fuel Fillups...");
                    break;
                case 5:
                    progDlg.setMessage("\nUpdating Oil Changes...");
                    break;
                case 6:
                    progDlg.setTitle("\nAll Caught Up");
                    progDlg.setMessage("\nNothing to Update");
                    break;
            }
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.println(Log.VERBOSE, LOG_TAG, "Dismissing Dialog;");
            progDlg.dismiss();
            // MainActivity.fillPageWithInfo();
            haveDb = true;
            if (closeWhenDone) {
                // exit program
                onBackPressed();
                onBackPressed();
            }
        }
    }
}
