package com.slment.jsefuel.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.slment.jsefuel.Adapters.VehicleAdapter;
import com.slment.jsefuel.Database.FuelRepository;
import com.slment.jsefuel.Database.UpdateDatabase;
import com.slment.jsefuel.Entities.Employee;
import com.slment.jsefuel.Entities.OilChange;
import com.slment.jsefuel.Entities.Vehicle;
import com.slment.jsefuel.R;
import com.slment.jsefuel.utils.NetworkMonitor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class MainActivity extends AppCompatActivity {
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;
    public static String LOG_TAG = "##### MainActivity: ";
    public static String phoneNumber = null;
    public static String trimPhone = null;
    public static int vehicleId;
    public VehicleAdapter adapter = null;
    boolean hasPhonePermissions = false;
    boolean hasInternetPermissions = false;
    boolean hasLocationPermissions = false;
    @SuppressWarnings("SpellCheckingInspection")
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdftime24 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    List<Vehicle> vehicleList;
    List<OilChange> oilChangeList;
    Employee currentEmp = null;
    Vehicle currentVeh = null;
    UpdateDatabase ud = new UpdateDatabase();
    String SSID = null;
    TextView tvWorkPhone;
    TextView tvUserName;
    RecyclerView recyclerView = null;
    private FuelRepository repository;

    @SuppressLint({"MissingPermission", "HardwareIds"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvWorkPhone = findViewById(R.id.tvMainWorkPhone);
        tvUserName = findViewById(R.id.tvMainUserNameId);

        hasPhonePermissions = false;
        hasInternetPermissions = false;
        hasLocationPermissions = false;
        if (!testPermissions()) {
            Toast.makeText(this, "Required Permissions Not Granted", Toast.LENGTH_LONG).show();
            Log.println(Log.VERBOSE, LOG_TAG, "Missing Permissions.");
            Log.println(Log.VERBOSE, LOG_TAG, "Ending Program");
//            onBackPressed();
//            onBackPressed();
        }

        registerReceiver(new NetworkMonitor(),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = tMgr.getLine1Number();
        showNumber(phoneNumber);

        WifiManager wMgr = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (wMgr.isWifiEnabled()) {
            WifiInfo wifiInfo = wMgr.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED ||
                        state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    SSID = wifiInfo.getSSID();
                    Toast.makeText(this, "Connected to Wifi: " + SSID,
                            Toast.LENGTH_LONG).show();
                    Log.println(Log.VERBOSE, LOG_TAG, "SSID = " + SSID);
                }
            }
        }

        final Button to_fuel = (Button) findViewById(R.id.btnMainAddFuel);
        to_fuel.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FuelAddEditActivity.class);
            // set up variables to pass to add/edit screen, then go there
            intent.putExtra("EmployeeId", currentEmp.getEmployeeId());
            if (adapter.selectedPosition != RecyclerView.NO_POSITION) {
                currentVeh = adapter.vehicleList.get(adapter.selectedPosition);
            }
            intent.putExtra("VehicleId", currentVeh.getVehicleId());
            intent.putExtra("VehicleNum", currentVeh.getVehicleNum());
            startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
        });

        final Button to_oil = (Button) findViewById(R.id.btnMainAddOilChange);
        to_oil.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, OilAddEditActivity.class);
            // set up variables to pass to add/edit screen, then go there
            intent.putExtra("EmployeeId", currentEmp.getEmployeeId());
            if (adapter.selectedPosition != RecyclerView.NO_POSITION) {
                currentVeh = adapter.vehicleList.get(adapter.selectedPosition);
            }
            intent.putExtra("VehicleId", currentVeh.getVehicleId());
            intent.putExtra("VehicleNum", currentVeh.getVehicleNum());
            startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
        });

        final Button first_run = (Button) findViewById(R.id.btnFirstRun);
        first_run.setOnClickListener(view -> {
            Log.println(Log.VERBOSE, LOG_TAG, "No records in OilChange, attempting to create database");
            if (haveConnection()) {
                ud.doUpdate(MainActivity.this, "2019-01-01 01:01:01",
                        true, trimPhone);
                first_run.setVisibility(View.INVISIBLE);
            } else {
                Log.println(Log.VERBOSE, LOG_TAG, "No internet connection, aborting program.");
                // exit program
                onBackPressed();
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.rvVehiclesList);

        repository = new FuelRepository(getApplication());
        oilChangeList = repository.getOilChangeList();

        if ((oilChangeList == null) || (oilChangeList.size() == 0)) {
            first_run.setVisibility(View.VISIBLE);
        } else {
            fillPageWithInfo();
        }
    }

    public void fillPageWithInfo() {
        Log.println(Log.VERBOSE, LOG_TAG, "Checking for current employee.");
        currentEmp = repository.getEmployeeByWorkCell(trimPhone);

        // uncomment next line to test for unauthorized user
        // currentEmp = repository.getEmployeeByWorkCell("5555555555");

        if (currentEmp == null) {
            Log.println(Log.VERBOSE, LOG_TAG, "Employee not found");
            currentEmp = repository.getEmployeeByWorkCell("5555215554");
            if (currentEmp == null) {
                Log.println(Log.VERBOSE, LOG_TAG, "Known employee not found, database must not exist?");
                Toast.makeText(this, "Employee not found, ending program.", Toast.LENGTH_LONG).show();
                // exit program
                onBackPressed();
                onBackPressed();
            }
            phoneNumber = "5555215554";
            showNumber(phoneNumber);

            Log.println(Log.VERBOSE, LOG_TAG, "Getting AlertDialog for Unauthorized Phone.");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String msgStr = "This device is not authorized to run this Application.  " +
                    "(phone number not found in Employee database)  " +
                    "For testing purposes, you will be assigned " +
                    " a default phone number which will allow you access as 'John Jones' " +
                    "with phone number 555-521-5554.";
            builder.setMessage(msgStr)
                    .setTitle("UNAUTHORIZED USER!");
            // Add the buttons
            builder.setPositiveButton("OK", (dialog, id) -> {
                // User clicked OK button
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        String userName = currentEmp.getFirstName() + " " + currentEmp.getLastName();
        tvUserName.setText(userName);
        vehicleList = repository.getVehicleList();

        adapter = new VehicleAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // if current user has CDL, show all vehicles, else don't show vehicles requiring CDL
        List<Vehicle> filteredVehicleList = new ArrayList<>();
        int currItem = 0;
        for (Vehicle p : vehicleList) {
            if (p.isActive()) {
                if (!p.getRequiresCdl()) {
                    filteredVehicleList.add(p);
                    if (p.getVehicleId() == currentEmp.getVehicleId()) {
                        adapter.selectedPosition = currItem;
                        currentVeh = p;
                    }
                    currItem++;
                } else {
                    if (currentEmp.isLicenseCdl()) {
                        filteredVehicleList.add(p);
                        if (p.getVehicleId() == currentEmp.getVehicleId()) {
                            adapter.selectedPosition = currItem;
                            currentVeh = p;
                        }
                        currItem++;
                    }
                }
            }
        }
        adapter.setWords(filteredVehicleList);
    }

    boolean haveConnection() {
        // check for internet or wifi connectivity, inform and return if not available
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            Toast.makeText(this, "No Internet Connection available...\n Unable to get data",
                    Toast.LENGTH_LONG).show();
            Log.println(Log.VERBOSE, LOG_TAG, "########## You are not online!!!!");
            return false;
        }
        return true;
    }

    public boolean testPermissions() {
        // check LOCATION permission - Need it to access SSID since API 27
        if ((hasLocationPermissions) ||
                (ActivityCompat.checkSelfPermission(this, ACCESS_LOCATION_EXTRA_COMMANDS)
                         == PackageManager.PERMISSION_GRANTED
                         && ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)) {
            hasLocationPermissions = true;
        } else {
            requestPhonePermission(300);
        }

        // check PHONE permissions
        if ((hasPhonePermissions) ||
                (ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS)
                         == PackageManager.PERMISSION_GRANTED
                         && ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE)
                        == PackageManager.PERMISSION_GRANTED)) {
            hasPhonePermissions = true;
        } else {
            requestPhonePermission(100);
        }

        // check INTERNET permissions
        if ((hasInternetPermissions) ||
                (ActivityCompat.checkSelfPermission(this, INTERNET)
                        == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, ACCESS_WIFI_STATE)
                        == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, ACCESS_NETWORK_STATE)
                        == PackageManager.PERMISSION_GRANTED)) {
            hasInternetPermissions = true;
        } else {
            requestPhonePermission(200);
        }

        return (hasPhonePermissions && hasInternetPermissions && hasLocationPermissions);
    }

    private void requestPhonePermission(int requestCode) {

        requestPermissions(new String[]{READ_PHONE_STATE, READ_PHONE_NUMBERS,
                READ_SMS, INTERNET, ACCESS_WIFI_STATE,
                ACCESS_NETWORK_STATE, ACCESS_LOCATION_EXTRA_COMMANDS,
                ACCESS_FINE_LOCATION}, requestCode);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            switch (requestCode) {
//                case 100:
//                    requestPermissions(new String[]{READ_PHONE_STATE, READ_PHONE_NUMBERS,
//                            READ_SMS}, requestCode);
//                    break;
//                case 200:
//                    requestPermissions(new String[]{INTERNET, ACCESS_WIFI_STATE,
//                            ACCESS_NETWORK_STATE}, requestCode);
//                    break;
//                case 300:
//                    requestPermissions(new String[]{ACCESS_LOCATION_EXTRA_COMMANDS, ACCESS_FINE_LOCATION}, requestCode);
//                    break;
//            }
//        }
    }

    // format and show the phone number on the screen
    public void showNumber(String workNumber) {
        StringBuilder outPhone = new StringBuilder("000-000-0000");
        int i = workNumber.length() - 1;
        int numPos = 11;

        while ((i >= 0) && (numPos >= 0)) {
            if ((workNumber.charAt(i) >= '0') && (workNumber.charAt(i) <= '9')) {
                outPhone.setCharAt(numPos, workNumber.charAt(i));
                if ((--numPos == 7) || (numPos == 3)) {     // leave the dashes in the string
                    --numPos;
                }
            }
            --i;
        }

        phoneNumber = outPhone.toString();
        trimPhone = phoneNumber.substring(0, 3) + phoneNumber.substring(4, 7) + phoneNumber.substring(8);
        tvWorkPhone.setText(phoneNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_fuel) {
            Intent intent = new Intent(MainActivity.this, FuelAddEditActivity.class);
            // set up variables to pass to add/edit screen, then go there
            intent.putExtra("EmployeeId", currentEmp.getEmployeeId());
            if (adapter.selectedPosition != RecyclerView.NO_POSITION) {
                currentVeh = adapter.vehicleList.get(adapter.selectedPosition);
            }
            intent.putExtra("VehicleId", currentVeh.getVehicleId());
            intent.putExtra("VehicleNum", currentVeh.getVehicleNum());
            startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
            return true;
        }

        if (id == R.id.action_add_oil) {
            Intent intent = new Intent(MainActivity.this, OilAddEditActivity.class);
            // set up variables to pass to add/edit screen, then go there
            intent.putExtra("EmployeeId", currentEmp.getEmployeeId());
            if (adapter.selectedPosition != RecyclerView.NO_POSITION) {
                currentVeh = adapter.vehicleList.get(adapter.selectedPosition);
            }
            intent.putExtra("VehicleId", currentVeh.getVehicleId());
            intent.putExtra("VehicleNum", currentVeh.getVehicleNum());
            startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
            return true;
        }

        if (id == R.id.action_view_fuel) {
            Intent intent = new Intent(MainActivity.this, FuelActivity.class);
            // set up variables to pass to add/edit screen, then go there
            intent.putExtra("EmployeeId", currentEmp.getEmployeeId());
            if (adapter.selectedPosition != RecyclerView.NO_POSITION) {
                currentVeh = adapter.vehicleList.get(adapter.selectedPosition);
            }
            intent.putExtra("VehicleId", currentVeh.getVehicleId());
            intent.putExtra("VehicleNum", currentVeh.getVehicleNum());
            startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
            return true;
        }

        if (id == R.id.action_view_oil) {
            Intent intent = new Intent(MainActivity.this, OilActivity.class);
            // set up variables to pass to add/edit screen, then go there
            intent.putExtra("EmployeeId", currentEmp.getEmployeeId());
            if (adapter.selectedPosition != RecyclerView.NO_POSITION) {
                currentVeh = adapter.vehicleList.get(adapter.selectedPosition);
            }
            intent.putExtra("VehicleId", currentVeh.getVehicleId());
            intent.putExtra("VehicleNum", currentVeh.getVehicleNum());
            startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
            return true;
        }

        if (id == R.id.action_exit) {
            onBackPressed();
            return true;
        }

        if (id == R.id.action_update_db) {
            if (haveConnection()) {
                // if we have run an update previously this session, currentEmp may not
                //   have been updated, so we update it here
                currentEmp = repository.getEmployeeByWorkCell(trimPhone);

                String lastUpdate = sdftime24.format(currentEmp.getLastDataSync());
                ud.currentEmp = currentEmp;
                ud.doUpdate(this, lastUpdate, false, trimPhone);

                //  we TRY to update currentEmp here but, because the update runs on a different
                //    thread, it may not have completed yet.  that is why we call it a few
                //    lines above here, in case the user decides to do another update
                currentEmp = repository.getEmployeeByWorkCell(trimPhone);

            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
