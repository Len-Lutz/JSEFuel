package com.slment.jsefuel.utils;

import android.app.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.slment.jsefuel.Database.ApiToOffice;
import com.slment.jsefuel.Database.FuelRepository;
import com.slment.jsefuel.Entities.Fuel;
import com.slment.jsefuel.Entities.OilChange;
import com.slment.jsefuel.UI.FuelAddEditActivity;
import com.slment.jsefuel.Database.ApiToOffice;
import com.slment.jsefuel.UI.OilAddEditActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkMonitor extends BroadcastReceiver {
    private static String BASE_URL = "http://lenscameralens.com/JSEFuel/public/";
    public SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    public SimpleDateFormat sdfymd = new SimpleDateFormat("yyyy-MM-dd");
    public SimpleDateFormat sdfDateTime24 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public SimpleDateFormat sdfTime12 = new SimpleDateFormat("hh:mm aa");
    public SimpleDateFormat sdfFromFields = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
    public String strResponse = null;
    public String strError = null;
    public static String LOG_TAG = "##### NetworkMonitor: ";
    public static Context context;
    public FuelRepository repository;
    public List<Fuel> newFuelList;
    public List<OilChange> newOilChangeList;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        if (haveConnection(context)) {
            repository = new FuelRepository(((Activity) context).getApplication());
            newFuelList = repository.getNewFuels();
            newOilChangeList = repository.getNewOilChanges();

            if((newFuelList != null) && (newFuelList.size() > 0)) {
                for(Fuel p:newFuelList) {
                    sendFuelToCompanyDatabase(p);
                }
            }

            if((newOilChangeList != null) && (newOilChangeList.size() > 0)) {
                for(OilChange p:newOilChangeList) {
                    sendOilToCompanyDatabase(p);
                }
            }
        }
    }

    public boolean haveConnection(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return ((networkInfo != null) && (networkInfo.isConnected()));
    }

    public void sendFuelToCompanyDatabase(Fuel fuel) {
        int fuelId = fuel.getFuelId();
        int vehicleId = fuel.getVehicleId();
        int employeeId = fuel.getEmployeeId();
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
        Call<ResponseBody> call = apiToOffice.createFuelFill(fuelId, vehicleId, employeeId, fillDate,
                miles, odometer, quantity, fillCost, providerId, lastUpdated, lastUpdatedBy);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 201) {
                        strResponse = response.body().string();
                        Log.println(Log.VERBOSE, LOG_TAG, strResponse);
                        try {
                            JSONObject job = new JSONObject(strResponse);
                            int newId = job.getInt("newId");
                            fuel.setFuelId(newId);
                            Log.println(Log.VERBOSE, LOG_TAG,
                                    "FuelId set to " + newId);
                            if(newId > 0) repository.update(fuel);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if(response.code() == 409) {
                        if (fuel.getFuelId() == 0) {
                            strResponse = response.errorBody().string();
                            Log.println(Log.VERBOSE, LOG_TAG, strResponse);
                            try {
                                JSONObject job = new JSONObject(strResponse);
                                int newId = job.getInt("newId");
                                fuel.setFuelId(newId);
                                Log.println(Log.VERBOSE, LOG_TAG,
                                        "FuelId set to " + newId);
                                if(newId > 0) repository.update(fuel);
                            } catch (JSONException e) {
                                e.printStackTrace();
                           }
                        }
                    } else {
                        strResponse = response.errorBody().string();
                        Log.println(Log.VERBOSE, LOG_TAG, strResponse);
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                strError = t.getMessage();
                Log.println(Log.VERBOSE, LOG_TAG, t.getMessage());
            }
        });
    }

    public void sendOilToCompanyDatabase(OilChange oilChange) {
        int oilChangeId = oilChange.getOilChangeId();
        int vehicleId = oilChange.getVehicleId();
        int employeeId = oilChange.getEmployeeId();
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
        Call<ResponseBody> call = apiToOffice.createOilChange(oilChangeId, vehicleId, employeeId,
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
                            int newId = job.getInt("newId");
                            oilChange.setOilChangeId(newId);
                            Log.println(Log.VERBOSE, LOG_TAG,
                                    "oilChangeId set to " + newId);
                            if(newId > 0) repository.update(oilChange);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if(response.code() == 409) {
                        if (oilChange.getOilChangeId() == 0) {
                            strResponse = response.errorBody().string();
                            Log.println(Log.VERBOSE, LOG_TAG, strResponse);
                            try {
                                JSONObject job = new JSONObject(strResponse);
                                int newId = job.getInt("newId");
                                oilChange.setOilChangeId(newId);
                                Log.println(Log.VERBOSE, LOG_TAG,
                                        "oilChangeId set to " + newId);
                                if(newId > 0) repository.update(oilChange);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        strResponse = response.errorBody().string();
                        Log.println(Log.VERBOSE, LOG_TAG, strResponse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                strError = t.getMessage();
                Log.println(Log.VERBOSE, LOG_TAG, t.getMessage());
            }
        });
    }
}
