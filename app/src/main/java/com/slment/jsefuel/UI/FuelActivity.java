package com.slment.jsefuel.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.slment.jsefuel.Adapters.FuelAdapter;
import com.slment.jsefuel.Database.FuelRepository;
import com.slment.jsefuel.Entities.Fuel;
import com.slment.jsefuel.R;
import com.slment.jsefuel.ViewModel.FuelViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FuelActivity extends AppCompatActivity {
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;
    public static String LOG_TAG = "##### FuelActivity: ";
    private static FuelRepository repository;
    private FuelViewModel fuelViewModel;
    private static FuelAdapter adapter;
    public static int numFuelFills;
    private int fuelFillId;
    private int employeeId;
    private int vehicleId;
    private String vehicleNum;
    private TextView tvVehicleNum1;
    public SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    public SimpleDateFormat sdfymd = new SimpleDateFormat("yyyy-MM-dd");
    public SimpleDateFormat sdfDateTime24 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public SimpleDateFormat sdfTime12 = new SimpleDateFormat("hh:mm aa");
    private static List<Fuel> fuelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon (R.drawable.ic_baseline_arrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled (true);
        getSupportActionBar().setDisplayShowHomeEnabled (true);
        fuelViewModel = new ViewModelProvider(this).get(FuelViewModel.class);
        tvVehicleNum1 = findViewById(R.id.tvVehicleNum1);

        repository = new FuelRepository(getApplication());
        fuelList = repository.getFuelList();

        fuelFillId = getIntent().getIntExtra("FuelFillId", 0);
        employeeId = getIntent().getIntExtra("EmployeeId", 0);
        vehicleId = getIntent().getIntExtra("VehicleId", 0);
        vehicleNum = getIntent().getStringExtra("VehicleNum");
        tvVehicleNum1.setText("Vehicle: " + vehicleNum);

        RecyclerView recyclerView = findViewById(R.id.rvFuelHistoryList);

        adapter = new FuelAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Fuel> filteredFuelList = new ArrayList<>();
        int currItem = 0;
        float totalMiles = (float) 0.0;
        float totalQty = (float) 0.0;
        for (Fuel p:fuelList) {
            if (p.getVehicleId() == vehicleId) {
                filteredFuelList.add(p);
                totalMiles += p.getMiles();
                totalQty += p.getQuantity();
                currItem++;
            }
        }
        if (currItem > 0) {
            Fuel tempFuel = new Fuel();
            tempFuel.setMiles(totalMiles);
            tempFuel.setQuantity(totalQty);
            tempFuel.setFuelId(-1);
            filteredFuelList.add(tempFuel);
            currItem++;
        }
        adapter.setWords(filteredFuelList);

        final Button add_fuel = (Button) findViewById(R.id.btnFuelHistDelete);
        add_fuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FuelActivity.this, FuelAddEditActivity.class);
                // todon't  set up variables to pass to add/edit screen
                startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
            }
        });

        final Button edit_fuel = (Button) findViewById(R.id.btnFuelHistEdit);
        edit_fuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FuelActivity.this, FuelAddEditActivity.class);
                // todon't  set up variables to pass to add/edit screen
                startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
            }
        });

        final Button fuel_done = (Button) findViewById(R.id.btnFuelHistDone);
        fuel_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fuel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        if (id == R.id.action_from_fuel_add_fuel) {
//            Intent intent = new Intent(FuelActivity.this, FuelAddEditActivity.class);
//            // todon't  set up variables to pass to add/edit screen
//            startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
//            return true;
//        }
//
//        if(id == R.id.action_view_oil) {
//            Intent intent = new Intent(FuelActivity.this, OilActivity.class);
//            // todon't  set up variables to pass to add/edit screen
//            startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
//            return true;
//        }

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