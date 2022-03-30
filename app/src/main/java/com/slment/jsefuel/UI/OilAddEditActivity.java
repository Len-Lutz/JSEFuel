package com.slment.jsefuel.UI;

import android.content.Context;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.slment.jsefuel.Adapters.ProviderAdapter;
import com.slment.jsefuel.Database.ApiToOffice;
import com.slment.jsefuel.Database.FuelRepository;
import com.slment.jsefuel.Entities.Fuel;
import com.slment.jsefuel.Entities.OilChange;
import com.slment.jsefuel.Entities.Provider;
import com.slment.jsefuel.Entities.Vehicle;
import com.slment.jsefuel.R;
import com.slment.jsefuel.ViewModel.ProviderViewModel;
import com.slment.jsefuel.utils.DateTimePopups;
import com.slment.jsefuel.utils.PopUps;
import com.slment.jsefuel.utils.Services;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OilAddEditActivity extends AppCompatActivity {
    public static String LOG_TAG = "##### OilAddEditActivity: ";
    private static FuelRepository repository;
    private ProviderViewModel providerViewModel;
    private static ProviderAdapter adapter;
    public static int numProviders;
    private int oilChangeId;
    private int employeeId;
    private int vehicleId;
    private String vehicleNum;
    private TextView tvVehicleNum4;
    private EditText etOilChangeDate;
    private EditText etOilChangeOdometer;
    private EditText etOilChangeCost;
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
    public SimpleDateFormat sdfFromField = new SimpleDateFormat("MM/dd/yyyy");
    private static List<Provider> providerList;
    private static List<Vehicle> vehicleList;
    private static Vehicle  currentVeh = null;
    private static Provider currentPro = null;
    private long dateAsLong;
    private String strResponse = null;
    private String strError = null;
    final boolean[] clearToGo = {true};

    private static final String BASE_URL = "http://lenscameralens.com/JSEFuel/public/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oil_add_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon (R.drawable.ic_baseline_arrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled (true);
        getSupportActionBar().setDisplayShowHomeEnabled (true);
        providerViewModel = new ViewModelProvider(this).get(ProviderViewModel.class);

        tvVehicleNum4 = findViewById(R.id.tvVehicleNum4);
        etOilChangeDate = findViewById(R.id.etOilChangeDate);
        etOilChangeOdometer = findViewById(R.id.etOilChangeOdometer);
        etOilChangeCost = findViewById(R.id.etOilChangeCost);

        repository = new FuelRepository(getApplication());
        providerList = repository.getProviderList();
        vehicleList = repository.getVehicleList();

        oilChangeId = getIntent().getIntExtra("OilchangeId", 0);
        employeeId = getIntent().getIntExtra("EmployeeId", 0);
        vehicleId = getIntent().getIntExtra("VehicleId", 0);
        vehicleNum = getIntent().getStringExtra("VehicleNum");
        tvVehicleNum4.setText("Vehicle: " + vehicleNum);

        for (Vehicle p:vehicleList) {
            if (p.getVehicleId() == vehicleId) {
                currentVeh = p;
            }
        }
        int serviceType = Services.OIL_CHANGE;

        if (oilChangeId > 0){
            setTitle("Edit Oil Change");
            etOilChangeDate.setText(getIntent().getStringExtra("OilChangeDate"));
            etOilChangeOdometer.setText(getIntent().getStringExtra("OilChangeOdemeter"));
            etOilChangeCost.setText(getIntent().getStringExtra("OilChangeCost"));
            currentProId = getIntent().getIntExtra("ProviderId", -1);
        }else{
            setTitle("Add Oil Change");
            workDate = Calendar.getInstance();
            dateStr = sdf.format(workDate.getTime());
            etOilChangeDate.setText(dateStr);
            etOilChangeOdometer.setText("");
            etOilChangeCost.setText("");
        }

        RecyclerView recyclerView = findViewById(R.id.rvOilChangeProviderList);

        adapter = new ProviderAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Provider> filteredProviderList = new ArrayList<>();
        int currItem = 0;
        for(Provider p:providerList) {
            if (p.isActive()) {
                if ((p.getServices() & serviceType) == serviceType) {
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

        etOilChangeDate.setOnClickListener(v -> DateTimePopups.openDatePicker(OilAddEditActivity.this, v,   etOilChangeDate,
                "Oil Change"));

        final Button cancel_oil = (Button) findViewById(R.id.btnOilChangeCancel);
        cancel_oil.setOnClickListener(view -> onBackPressed());

        final Button save_oil = (Button) findViewById(R.id.btnOilChangeSave);
        save_oil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyAndSaveOil()) {
                    onBackPressed();
                }
            }
        });
    }

    private boolean verifyAndSaveOil() {
        Calendar workDate = Calendar.getInstance();
        workDate.setLenient(false);
        OilChange oilChange = new OilChange();
        String dateTime;

        dateTime = etOilChangeDate.getText().toString();
        try {
            workDate.setTime(sdfFromField.parse(dateTime, new ParsePosition(0)));
        } catch (DateTimeParseException e) {
            Log.println(Log.VERBOSE, LOG_TAG, "Unable to Parse Date.");
        }
        OilChange testOil = repository.getOilChangeByVehPlusDate(vehicleId, workDate.getTimeInMillis());
        if (testOil != null) {
            PopUps.okAlert(OilAddEditActivity.this,
                    "Error",
                    "Oil Change for this vehicle with this\n" +
                            " date has already been entered.",
                    "OK");
            return false;
        }

        if (etOilChangeOdometer.getText().toString().isEmpty()) {
            PopUps.okAlert(OilAddEditActivity.this,
                    "Error",
                    "Must Enter Odometer Reading.",
                    "OK");
            return false;
        }

        if (etOilChangeCost.getText().toString().isEmpty()) {
            PopUps.okAlert(OilAddEditActivity.this,
                    "Error",
                    "Must Enter Cost.",
                    "OK");
            return false;
        }

        if (adapter.selectedPosition == -1) {
            PopUps.okAlert(OilAddEditActivity.this,
                    "Error",
                    "Must Select Provider.",
                    "OK");
            return false;
        }

        oilChange.setOilChangeId(0);
        oilChange.setVehicleId(vehicleId);
        oilChange.setEmployeeId(employeeId);
        dateTime = etOilChangeDate.getText().toString();
        try {
            workDate.setTime(sdfFromField.parse(dateTime, new ParsePosition(0)));
        } catch (DateTimeParseException e) {
            Log.println(Log.VERBOSE, LOG_TAG, "Unable to Parse Date.");
        }
        oilChange.setOilChangeDate(workDate.getTime());

        //  verify workdate has correct date  with sdf
        // String chkDate = sdfFromFields.format(workDate.getTime());
        Log.println(Log.VERBOSE, LOG_TAG, "Saving oilChangeDate as: " +
                sdfFromField.format(workDate.getTime()));

        oilChange.setOdometer(Float.parseFloat(etOilChangeOdometer.getText().toString()));
        oilChange.setTotalCost(Float.parseFloat(etOilChangeCost.getText().toString()));
        oilChange.setProviderId(adapter.current.getProviderId());
        workDate =  Calendar.getInstance();
        workDate.set(Calendar.SECOND, 0);
        workDate.set(Calendar.MILLISECOND, 0);
        oilChange.setLastUpdated(workDate.getTime());
        oilChange.setLastUpdatedBy(employeeId);

        //  check if online, if yes, save to Company DB (which calls saveLocalOilData() too)
        //  otherwise, save to Local DB which will automatically save to Company DB when online
        //    again
        if (haveConnection(OilAddEditActivity.this)) {
            return sendOilToCompanyDatabase(oilChange);
        } else {
            saveLocalOilData(oilChange);
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

    public void saveLocalOilData(OilChange oilChange) {
        Log.println(Log.VERBOSE, LOG_TAG, "Updating LOCAL Database.");
        repository.insert(oilChange);

        // update vehicle record with new miles
        currentVeh.setLastOilChange((int) oilChange.getOdometer());
        currentVeh.setLastUpdated(oilChange.getLastUpdated());
        currentVeh.setLastUpdatedBy(oilChange.getLastUpdatedBy());
        repository.update(currentVeh);
        Log.println(Log.VERBOSE, LOG_TAG, "LOCAL Update Complete");
    }

    public boolean sendOilToCompanyDatabase(OilChange oilChange) {
        clearToGo[0] = true;
        String oilChangeDate = sdfymd.format(oilChange.getOilChangeDate());
        String odometer = String.format("%.1f", oilChange.getOdometer());
        String totalCost = String.format("%.2f", oilChange.getTotalCost());
        int providerId = oilChange.getProviderId();
        String lastUpdated = sdfDateTime24.format(oilChange.getLastUpdated());
        int lastUpdatedBy = oilChange.getLastUpdatedBy();

        //  send new oilChange to office database
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiToOffice apiToOffice = retrofit.create(ApiToOffice.class);
        Call<ResponseBody> call = apiToOffice.createOilChange(0, vehicleId, employeeId,
                oilChangeDate, odometer, totalCost, providerId, lastUpdated, lastUpdatedBy);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 201) {
                        strResponse = response.body().string();
                        Log.println(Log.VERBOSE, LOG_TAG, strResponse);
                        try {
                            JSONObject job = new JSONObject(strResponse);
                            oilChange.setOilChangeId(job.getInt("newId"));
                            Log.println(Log.VERBOSE, LOG_TAG,
                                    "oilChangeId set to " + oilChange.getOilChangeId());
                            saveLocalOilData(oilChange);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            strError = e.getMessage();
                            PopUps.okAlert(OilAddEditActivity.this,
                                    "Error",
                                    strError,
                                    "OK");

                            Log.println(Log.VERBOSE, LOG_TAG, "Exception Parsing JSONObject.");
                            Log.println(Log.VERBOSE, LOG_TAG, strError);
                            clearToGo[0] = false;
                        }
                    } else if(response.code() == 409) {
                        if (oilChange.getOilChangeId() == 0) {
                            strResponse = response.errorBody().string();
                            Log.println(Log.VERBOSE, LOG_TAG, strResponse);
                            try {
                                JSONObject job = new JSONObject(strResponse);
                                oilChange.setOilChangeId(job.getInt("newId"));
                                Log.println(Log.VERBOSE, LOG_TAG,
                                        "OilChangeId set to " + oilChange.getOilChangeId());
                                repository.update(oilChange);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                strError = e.getMessage();

                                Log.println(Log.VERBOSE, LOG_TAG, "Exception Parsing JSONObject.");
                                Log.println(Log.VERBOSE, LOG_TAG, strError);
                                clearToGo[0] = false;
                            }
                        }
                        PopUps.okAlert(OilAddEditActivity.this,
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
                            PopUps.okAlert(OilAddEditActivity.this,
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
                            PopUps.okAlert(OilAddEditActivity.this,
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
                    PopUps.okAlert(OilAddEditActivity.this,
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
                PopUps.okAlert(OilAddEditActivity.this,
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
        getMenuInflater().inflate(R.menu.menu_oil_add_edit, menu);
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