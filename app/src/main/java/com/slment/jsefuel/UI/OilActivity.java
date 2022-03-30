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
import com.slment.jsefuel.Adapters.OilAdapter;
import com.slment.jsefuel.Database.FuelRepository;
import com.slment.jsefuel.Entities.Fuel;
import com.slment.jsefuel.Entities.OilChange;
import com.slment.jsefuel.R;
import com.slment.jsefuel.ViewModel.FuelViewModel;
import com.slment.jsefuel.ViewModel.OilViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OilActivity extends AppCompatActivity {
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;
    public static String LOG_TAG = "##### OilActivity";
    private static FuelRepository repository;
    private OilViewModel oilViewModel;
    private static OilAdapter adapter;
    public static int numOilChanges;
    private int oilChangeId;
    private int employeeId;
    private int vehicleId;
    private String vehicleNum;
    private TextView tvVehicleNum3;
    public SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    public SimpleDateFormat sdfymd = new SimpleDateFormat("yyyy-MM-dd");
    public SimpleDateFormat sdfDateTime24 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public SimpleDateFormat sdfTime12 = new SimpleDateFormat("hh:mm aa");
    private static List<OilChange> oilChangeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oil);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon (R.drawable.ic_baseline_arrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled (true);
        getSupportActionBar().setDisplayShowHomeEnabled (true);
        oilViewModel = new ViewModelProvider(this).get(OilViewModel.class);
        tvVehicleNum3 = findViewById(R.id.tvVehicleNum3);

        repository = new FuelRepository(getApplication());
        oilChangeList = repository.getOilChangeList();

        oilChangeId = getIntent().getIntExtra("OilChangeId", 0);
        employeeId = getIntent().getIntExtra("EmployeeId", 0);
        vehicleId = getIntent().getIntExtra("VehicleId", 0);
        vehicleNum = getIntent().getStringExtra("VehicleNum");
        tvVehicleNum3.setText("Vehicle: " + vehicleNum);

        RecyclerView recyclerView = findViewById(R.id.rvOilHistoryList);

        adapter = new OilAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<OilChange> filteredOilList = new ArrayList<>();
        int currItem = 0;
        float prevOdometer = (float) 0.0;
        float currOdometer = (float) 0.0;
        float miles = (float) 0.0;
        float totalCost = (float) 0.0;
        for (OilChange p:oilChangeList) {
            if (p.getVehicleId() == vehicleId) {
                currOdometer =  p.getOdometer();
                miles = currOdometer - prevOdometer;
                prevOdometer = currOdometer;
                p.setOdometer(miles);
                totalCost += p.getTotalCost();
                filteredOilList.add(p);
                currItem++;
            }
        }
        if (currItem > 0) {
            OilChange tempOil = new OilChange();
            tempOil.setOdometer(currOdometer / currItem);
            tempOil.setTotalCost(totalCost / currItem);
            tempOil.setOilChangeId(-1);
            filteredOilList.add(tempOil);
            currItem++;
        }
        adapter.setWords(filteredOilList);

        final Button add_oil = (Button) findViewById(R.id.btnOilHistAddOil);
        add_oil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OilActivity.this, OilAddEditActivity.class);
                // todon't  set up variables to pass to add/edit screen
                startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
            }
        });

        final Button edit_oil = (Button) findViewById(R.id.btnOilHistEdit);
        edit_oil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OilActivity.this, OilAddEditActivity.class);
                // todon't  set up variables to pass to add/edit screen
                startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
            }
        });

        final Button oil_done = (Button) findViewById(R.id.btnOilHistDone);
        oil_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_oil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        if (id == R.id.action_from_oil_add_oil) {
//            Intent intent = new Intent(OilActivity.this, OilAddEditActivity.class);
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