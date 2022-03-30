package com.slment.jsefuel.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.slment.jsefuel.Adapters.ProviderAdapter;
import com.slment.jsefuel.Database.ApiToOffice;
import com.slment.jsefuel.Database.FuelRepository;
import com.slment.jsefuel.Entities.Fuel;
import com.slment.jsefuel.Entities.Provider;
import com.slment.jsefuel.Entities.Vehicle;
import com.slment.jsefuel.R;
import com.slment.jsefuel.ViewModel.ProviderViewModel;
import com.slment.jsefuel.utils.DateTimePopups;
import com.slment.jsefuel.utils.PopUps;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FuelAddEditActivity extends AppCompatActivity {
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;
    public static final String btnOK = "OK";
    public static final String dlgMessage = "Message To Display";
    public static String LOG_TAG = "##### FuelAddEditActivity: ";
    private static FuelRepository repository = null;
    private ProviderViewModel providerViewModel;
    private ProviderAdapter adapter;
    public static int numProviders;
    private int fuelFillId;
    private int employeeId;
    private int vehicleId;
    private String vehicleNum;
    private TextView tvVehicleNum2;
    private EditText etFuelFillDate;
    private EditText etFuelFillTime;
    private EditText etFuelFillOdometer;
    private EditText etFuelFillMiles;
    private EditText etFuelFillGallons;
    private EditText etFuelFillCost;
    private int position;
    private int currentProId = -1;
    private Calendar workDate;
    private String dateStr;
    private String timeStr;
    private int hour, minute;
    public SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    public SimpleDateFormat sdfymd = new SimpleDateFormat("yyyy-MM-dd");
    public SimpleDateFormat sdfDateTime24 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public SimpleDateFormat sdfTime12 = new SimpleDateFormat("hh:mm aa");
    public SimpleDateFormat sdfFromFields = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
    private static List<Provider> providerList;
    private static List<Vehicle> vehicleList;
    private static Vehicle  currentVeh = null;
    private static Provider currentPro = null;
    private long dateAsLong;
    private String strResponse = null;
    private String strError = null;
    final boolean[] clearToGo = {true};

    private static String BASE_URL = "http://lenscameralens.com/JSEFuel/public/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_add_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon (R.drawable.ic_baseline_arrow_back_24);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled (true);
        getSupportActionBar().setDisplayShowHomeEnabled (true);
        providerViewModel = new ViewModelProvider(this).get(ProviderViewModel.class);

        tvVehicleNum2 = findViewById(R.id.tvVehicleNum2);
        etFuelFillDate = findViewById(R.id.etFuelFillDate);
        etFuelFillTime = findViewById(R.id.etFuelFillTime);
        etFuelFillOdometer = findViewById(R.id.etFuelFillOdometer);
        etFuelFillMiles = findViewById(R.id.etFuelFillMiles);
        etFuelFillGallons = findViewById(R.id.etFuelFillGallons);
        etFuelFillCost = findViewById(R.id.etFuelFillCost);

        repository = new FuelRepository(getApplication());
        providerList = repository.getProviderList();
        vehicleList = repository.getVehicleList();

        fuelFillId = getIntent().getIntExtra("FuelFillId", 0);
        employeeId = getIntent().getIntExtra("EmployeeId", 0);
        vehicleId = getIntent().getIntExtra("VehicleId", 0);
        vehicleNum = getIntent().getStringExtra("VehicleNum");
        tvVehicleNum2.setText("Vehicle: " + vehicleNum);

        for (Vehicle p:vehicleList) {
            if (p.getVehicleId() == vehicleId) {
                currentVeh = p;
            }
        }
        int fuelType = currentVeh.getFuelType();

        if (fuelFillId > 0){
            setTitle("Edit Fuel Fill");
            etFuelFillDate.setText(getIntent().getStringExtra("FillDate"));
            etFuelFillTime.setText(getIntent().getStringExtra("FillTime"));
            etFuelFillOdometer.setText(getIntent().getStringExtra("FillOdemeter"));
            etFuelFillMiles.setText(getIntent().getStringExtra("FillMiles"));
            etFuelFillGallons.setText(getIntent().getStringExtra("FillGallons"));
            etFuelFillCost.setText(getIntent().getStringExtra("FillCost"));
            currentProId = getIntent().getIntExtra("ProviderId", -1);
        }else{
            setTitle("Add Fuel Fill");
            workDate = Calendar.getInstance();
            dateStr = sdf.format(workDate.getTime());
            etFuelFillDate.setText(dateStr);
            hour = workDate.get(Calendar.HOUR_OF_DAY);
            minute = workDate.get(Calendar.MINUTE);
            if (minute < 8) {
                minute = 0;
            } else if (minute < 23){
                minute = 15;
            } else if (minute < 38) {
                minute = 30;
            } else if (minute < 53) {
                minute = 45;
            } else {
                minute = 0;
                hour++;
            }
            if (hour > 23) hour = 0;
            workDate.clear();
            workDate.set(2019, Calendar.JANUARY, 1, hour, minute, 0);
            timeStr = sdfTime12.format(workDate.getTime());
            etFuelFillTime.setText(timeStr);
            etFuelFillOdometer.setText("");
            etFuelFillMiles.setText("");
            etFuelFillGallons.setText("");
            etFuelFillCost.setText("");
        }

        RecyclerView recyclerView = findViewById(R.id.rvFuelFillProviderList);

        adapter = new ProviderAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Provider> filteredProviderList = new ArrayList<>();
        int currItem = 0;
        for(Provider p:providerList) {
            if (p.isActive()) {
                if ((p.getFuelTypes() & fuelType) == fuelType) {
                    filteredProviderList.add(p);
                    if (p.getProviderId() == currentProId) {
                        adapter.selectedPosition = currItem;
                        currentPro = p;
                    }
                    currItem++;
                }
            }
        }
        adapter.setWords(filteredProviderList);

        etFuelFillDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePopups.openDatePicker(FuelAddEditActivity.this, v,   etFuelFillDate,
                        "Fillup");
            }
        });

        etFuelFillTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePopups.openTimePicker(FuelAddEditActivity.this, v,   etFuelFillTime,
                        "Fillup", 15);
            }
        });

        etFuelFillOdometer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    if (!etFuelFillOdometer.getText().toString().isEmpty()
                            && etFuelFillMiles.getText().toString().isEmpty()) {
                        float odometer = Float.parseFloat(etFuelFillOdometer.getText().toString());
                        float miles = odometer - (currentVeh.getLastFillMiles());
                        etFuelFillMiles.setText(String.format("%.1f", miles));
                    }
                }
            }
        });

        etFuelFillMiles.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    if (!etFuelFillMiles.getText().toString().isEmpty()
                            && etFuelFillOdometer.getText().toString().isEmpty()) {
                        float miles = Float.parseFloat(etFuelFillMiles.getText().toString());
                        float odometer = miles + (currentVeh.getLastFillMiles());
                        etFuelFillOdometer.setText(String.format("%.1f", odometer));
                    }
                }
            }
        });

        final Button cancel_fuel = (Button) findViewById(R.id.btnFuelFillCancel);
        cancel_fuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        final Button save_fuel = (Button) findViewById(R.id.btnFuelFillSave);
        save_fuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyAndSaveFuel() == true) {
                    onBackPressed();
                }
            }
        });
    }

    private boolean verifyAndSaveFuel() {
        Calendar workDate = Calendar.getInstance();
        workDate.setLenient(false);
        Fuel fuel = new Fuel();
        float testValue = 0.0f;
        String dateTime;

        dateTime = etFuelFillDate.getText().toString() + " " + etFuelFillTime.getText().toString();
        try {
            workDate.setTime(sdfFromFields.parse(dateTime, new ParsePosition(0)));
        } catch (DateTimeParseException e) {
            Log.println(Log.VERBOSE, LOG_TAG, "Unable to Parse Date and Time.");
        }
        Fuel testFuel = repository.getFuelByVehPlusDateTime(vehicleId, workDate.getTimeInMillis());
        if (testFuel != null) {
            PopUps.okAlert(FuelAddEditActivity.this,
                    "Error",
                    "Fuel Fill for this vehicle with this date\n" +
                            " and time has already been entered.",
                    "OK");
            return false;
        }

        if (etFuelFillOdometer.getText().toString().isEmpty() &&
                etFuelFillMiles.getText().toString().isEmpty()) {

            strError = "Both are empty!";

            PopUps.okAlert(FuelAddEditActivity.this,
                    "Error",
                    "Must Enter Either Odometer or Miles.",
                    "OK");
            return false;
        }

        testValue = Float.parseFloat(etFuelFillOdometer.getText().toString());
        if (testValue < currentVeh.getLastFillMiles()) {
            // setup the alert builder
            AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(this);
            dlgBuilder.setTitle("Notice");
            dlgBuilder.setMessage("Current Records Show Odometer Already Higher Then What You " +
                    "Have Entered! \n\nPlease Verify.");

            // add the buttons
            dlgBuilder.setPositiveButton("Use " + testValue, null);
            dlgBuilder.setNegativeButton("Re-enter Miles", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String tmpStr = String.format("%.1f", currentVeh.getLastFillMiles());
                    etFuelFillOdometer.setText(tmpStr);
                    etFuelFillOdometer.selectAll();
                    etFuelFillOdometer.requestFocus();
                    clearToGo[0] = false;
                }
            });

            // create and show the alert dialog
            AlertDialog dialog = dlgBuilder.create();
            dialog.show();

            if (clearToGo[0] == false) {
                clearToGo[0] = true;
                return false;
            }
        }

        if (etFuelFillGallons.getText().toString().isEmpty()) {
            PopUps.okAlert(FuelAddEditActivity.this,
                    "Error",
                    "Must Enter Gallons Purchased.",
                    "OK");

            etFuelFillGallons.selectAll();
            etFuelFillGallons.requestFocus();
            return false;
        }

        testValue = Float.parseFloat(etFuelFillGallons.getText().toString());
        if (testValue > ((currentVeh.getFuelCapacity() + 5) * 1.0f)) {
            PopUps.okAlert(FuelAddEditActivity.this,
                    "Error",
                    "" +
                            "Gallons Entered is More Than This Vehicle's Tank Can Hold!" +
                            "\n\nPlease Adjust.",
                    "OK");
            etFuelFillGallons.selectAll();
            etFuelFillGallons.requestFocus();

            return false;
        }

        if (etFuelFillCost.getText().toString().isEmpty()) {
            PopUps.okAlert(FuelAddEditActivity.this,
                    "Error",
                    "Must Enter Cost of Purchase.",
                    "OK");
            etFuelFillCost.selectAll();
            etFuelFillCost.requestFocus();
            return false;
        }

        if (adapter.selectedPosition == -1) {
            PopUps.okAlert(FuelAddEditActivity.this,
                    "Error",
                    "Must Select Provider.",
                    "OK");
            return false;
        }

        fuel.setFuelId(0);
        fuel.setVehicleId(vehicleId);
        fuel.setEmployeeId(employeeId);
        dateTime = etFuelFillDate.getText().toString() + " " + etFuelFillTime.getText().toString();
        try {
            workDate.setTime(sdfFromFields.parse(dateTime, new ParsePosition(0)));
        } catch (DateTimeParseException e) {
            Log.println(Log.VERBOSE, LOG_TAG, "Unable to Parse Date and Time.");
        }
        fuel.setFillDate(workDate.getTime());

        //  verify workdate has correct date  with sdf
        // String chkDate = sdfFromFields.format(workDate.getTime());
        Log.println(Log.VERBOSE, LOG_TAG, "Saving fillDate as: " +
                sdfFromFields.format(workDate.getTime()));

        fuel.setMiles(Float.parseFloat(etFuelFillMiles.getText().toString()));
        fuel.setOdometer(Float.parseFloat(etFuelFillOdometer.getText().toString()));
        fuel.setQuantity(Float.parseFloat(etFuelFillGallons.getText().toString()));
        fuel.setFillCost(Float.parseFloat(etFuelFillCost.getText().toString()));
        fuel.setProviderId(adapter.current.getProviderId());
        workDate = Calendar.getInstance();
        workDate.set(Calendar.SECOND, 0);
        workDate.set(Calendar.MILLISECOND, 0);
        fuel.setLastUpdated(workDate.getTime());
        fuel.setLastUpdatedBy(employeeId);

        //  check if online, if yes, save to Company DB (which calls saveLocalFuelData() too)
        //  otherwise, save to Local DB which will automatically save to Company DB when online
        //    again
        if (haveConnection(FuelAddEditActivity.this)) {
            return sendFuelToCompanyDatabase(fuel);
        } else {
            saveLocalFuelData(fuel);
        }
        return true;
    }

    //  check if phone is currently connected to internet
    public boolean haveConnection(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return ((networkInfo != null) && (networkInfo.isConnected()));
    }

    public void saveLocalFuelData(Fuel fuel) {
        //  insert new fuelfill into local database
        Log.println(Log.VERBOSE, LOG_TAG, "Updating LOCAL database.");
        repository.insert(fuel);

        // update vehicle record with new miles
        currentVeh.setLastFillMiles(fuel.getOdometer());
        currentVeh.setLastUpdated(fuel.getLastUpdated());
        currentVeh.setLastUpdatedBy(fuel.getLastUpdatedBy());
        repository.update(currentVeh);
    }

    public boolean sendFuelToCompanyDatabase(Fuel fuel) {
        clearToGo[0] = true;
        String fillDate = sdfDateTime24.format(fuel.getFillDate());
        String miles = String.format("%.1f", fuel.getMiles());
        String odometer = String.format("%.1f", fuel.getOdometer());
        String quantity = String.format("%.3f", fuel.getQuantity());
        String fillCost = String.format("%.2f", fuel.getFillCost());
        int providerId = fuel.getProviderId();

        String lastUpdated = sdfDateTime24.format(fuel.getLastUpdated());
        int lastUpdatedBy = fuel.getLastUpdatedBy();

        //  send new fuel fill to office database
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiToOffice apiToOffice = retrofit.create(ApiToOffice.class);
        Call<ResponseBody> call = apiToOffice.createFuelFill(0, vehicleId, employeeId, fillDate,
                miles, odometer, quantity, fillCost, providerId, lastUpdated, lastUpdatedBy);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                clearToGo[0] = true;
                try {
                    if (response.code() == 201) {
                        strResponse = response.body().string();
                        Log.println(Log.VERBOSE, LOG_TAG, strResponse);
                        try {
                            JSONObject job = new JSONObject(strResponse);
                            fuel.setFuelId(job.getInt("newId"));
                            Log.println(Log.VERBOSE, LOG_TAG,
                                    "FuelId set to " + fuel.getFuelId());
                            saveLocalFuelData(fuel);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            strError = e.getMessage();
                            PopUps.okAlert(FuelAddEditActivity.this,
                                    "Error",
                                    strError,
                                    "OK");

                            Log.println(Log.VERBOSE, LOG_TAG, "Exception Parsing JSONObject.");
                            Log.println(Log.VERBOSE, LOG_TAG, strError);
                            clearToGo[0] = false;
                        }
                    } else if(response.code() == 409) {
                        clearToGo[0] = false;
                        if (fuel.getFuelId() == 0) {
                            strResponse = response.errorBody().string();
                            Log.println(Log.VERBOSE, LOG_TAG, strResponse);
                            try {
                                JSONObject job = new JSONObject(strResponse);
                                fuel.setFuelId(job.getInt("newId"));
                                Log.println(Log.VERBOSE, LOG_TAG,
                                        "FuelId set to " + fuel.getFuelId());
                                repository.update(fuel);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                strError = e.getMessage();

                                Log.println(Log.VERBOSE, LOG_TAG, "Exception Parsing JSONObject.");
                                Log.println(Log.VERBOSE, LOG_TAG, strError);
                            }
                        }
                        PopUps.okAlert(FuelAddEditActivity.this,
                                "Error",
                                "Duplicate Entry Attempted.\n\n" +
                                        "There is already an entry for this fillup.",
                                "OK");
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }
                    } else {
                        strResponse = response.errorBody().string();
                        Log.println(Log.VERBOSE, LOG_TAG, strResponse);
                        try {
                            JSONObject job = new JSONObject(strResponse);
                            strResponse = job.getString("message");
                            PopUps.okAlert(FuelAddEditActivity.this,
                                    "Error",
                                    strResponse,
                                    "OK");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ie) {
                                ie.printStackTrace();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            strError = e.getMessage();
                            PopUps.okAlert(FuelAddEditActivity.this,
                                    "Error",
                                    strError,
                                    "OK");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ie) {
                                ie.printStackTrace();
                            }

                            Log.println(Log.VERBOSE, LOG_TAG, "Exception Parsing JSONObject.");
                            Log.println(Log.VERBOSE, LOG_TAG, strError);
                            clearToGo[0] = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    strError = e.getMessage();
                    PopUps.okAlert(FuelAddEditActivity.this,
                            "Error",
                            strError,
                            "OK");
                    Log.println(Log.VERBOSE, LOG_TAG, "Exception Creating Response String.");
                    Log.println(Log.VERBOSE, LOG_TAG, strError);
                    clearToGo[0] = false;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                strError = t.getMessage();
                PopUps.okAlert(FuelAddEditActivity.this,
                        "Error",
                        strError,
                        "OK");

                Log.println(Log.VERBOSE, LOG_TAG, t.getMessage());
                clearToGo[0] = false;
            }
        });

        if (clearToGo[0] = false) {
            clearToGo[0] = true;
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fuel_add_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_exit) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}