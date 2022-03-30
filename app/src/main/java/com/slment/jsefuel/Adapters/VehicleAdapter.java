package com.slment.jsefuel.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.slment.jsefuel.Entities.Vehicle;
import com.slment.jsefuel.R;

import java.util.List;
import java.text.SimpleDateFormat;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {
    public int selectedPosition = 0;
    public Vehicle current = null;
    class VehicleViewHolder extends RecyclerView.ViewHolder {
        private final TextView vehicleListView1;
        private final TextView vehicleListView2;
        private final String LOG_TAG = "VehicleAdapterLog";

        private VehicleViewHolder(View itemView) {
            super(itemView);
            vehicleListView1 = itemView.findViewById(R.id.tvVehicle1);
            vehicleListView2 = itemView.findViewById(R.id.tvVehicle2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition() == RecyclerView.NO_POSITION) return;
                    notifyItemChanged(selectedPosition);
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(selectedPosition);
                    current = vehicleList.get(selectedPosition);
                }
            });
        }
    }

    private final LayoutInflater inflater;
    private final Context context;
    public List<Vehicle> vehicleList;

    public VehicleAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public VehicleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_vehicle, parent, false);
        return new VehicleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VehicleViewHolder holder, int position) {
        if (vehicleList != null) {
            String buildString1, buildString2;
            final Vehicle current = vehicleList.get(position);
            buildString1 = current.getVehicleNum();
            holder.vehicleListView1.setText(buildString1);
            buildString2 = current.getModelYear() + " - " + current.getMake() + "   " + current.getModel();
            holder.vehicleListView2.setText(buildString2);
            holder.itemView.setBackgroundColor(selectedPosition == position ?
                    Color.parseColor("#0B29B1") : Color.parseColor("#FFB4D8F3"));
            holder.vehicleListView1.setTextColor(selectedPosition == position ?
                    Color.WHITE : Color.BLACK);
            holder.vehicleListView2.setTextColor(selectedPosition == position ?
                    Color.WHITE : Color.BLACK);
            holder.vehicleListView1.setBackgroundColor(selectedPosition == position ?
                    Color.parseColor("#0B29B1") : Color.parseColor("#FFB4D8F3"));
            holder.vehicleListView2.setBackgroundColor(selectedPosition == position ?
                    Color.parseColor("#0B29B1") : Color.parseColor("#FFB4D8F3"));
        } else {
            // covers the case of data not being ready
            holder.vehicleListView1.setText("No Vehicles Found");
        }
    }

    public void setWords(List<Vehicle> vehicles) {
        vehicleList = vehicles;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (vehicleList != null) {
            return vehicleList.size();
        }
        else return 0;
    }
}
