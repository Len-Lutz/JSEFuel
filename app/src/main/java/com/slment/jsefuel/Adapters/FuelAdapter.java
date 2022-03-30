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

import com.slment.jsefuel.Entities.Fuel;
import com.slment.jsefuel.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class FuelAdapter extends RecyclerView.Adapter<FuelAdapter.FuelViewHolder> {
    private Calendar workDate  = Calendar.getInstance();;
    private String dateStr;
    private String timeStr;
    private int hour, minute;
    public SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    public SimpleDateFormat sdfymd = new SimpleDateFormat("yyyy-MM-dd");
    public SimpleDateFormat sdfDateTime24 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public SimpleDateFormat sdfDateTime12 = new SimpleDateFormat("MM/dd/yyyy  hh:mm aa");
    public SimpleDateFormat sdfTime12 = new SimpleDateFormat("hh:mm aa");
    public int selectedPosition = -1;
    public Fuel current = null;
    class FuelViewHolder extends RecyclerView.ViewHolder {
        private final TextView fuelListView1;
        private final TextView fuelListView2;
        private final String LOG_TAG = "FuelAdapterLog";

        private FuelViewHolder(View itemView) {
            super(itemView);
            fuelListView1 = itemView.findViewById(R.id.tvFuel1);
            fuelListView2 = itemView.findViewById(R.id.tvFuel2);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(getAdapterPosition() == RecyclerView.NO_POSITION) return;
//                    notifyItemChanged(selectedPosition);
//                    selectedPosition = getAdapterPosition();
//                    notifyItemChanged(selectedPosition);
//                    current = fuelList.get(selectedPosition);
//                }
//            });
        }
    }

    private final LayoutInflater inflater;
    private final Context context;
    private List<Fuel> fuelList;

    public FuelAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public FuelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_fuel, parent, false);
        return new FuelViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FuelViewHolder holder, int position) {
        if (fuelList != null) {
            String buildString1, buildString2;
            final Fuel current = fuelList.get(position);
            if (current.getFuelId() == -1) {
                buildString1 = "      Vehicle Totals and Avg MPG.";
                selectedPosition = position;
            } else {
                workDate.setTime(current.getFillDate());
                timeStr = sdfDateTime12.format(workDate.getTime());
                buildString1 = "Fill Date:  " + timeStr;
            }
            holder.fuelListView1.setText(buildString1);
            buildString2 =  String.format("Miles: %.1f,  Gals: %.3f,  MPG: %.2f",
                            current.getMiles(),
                            current.getQuantity(),
                            current.getMiles() / current.getQuantity());
            holder.fuelListView2.setText(buildString2);
            holder.itemView.setBackgroundColor(selectedPosition == position ?
                    Color.parseColor("#0B29B1") : Color.parseColor("#FFB4D8F3"));
            holder.fuelListView1.setTextColor(selectedPosition == position ?
                    Color.WHITE : Color.BLACK);
            holder.fuelListView2.setTextColor(selectedPosition == position ?
                    Color.WHITE : Color.BLACK);
            holder.fuelListView1.setBackgroundColor(selectedPosition == position ?
                    Color.parseColor("#0B29B1") : Color.parseColor("#FFB4D8F3"));
            holder.fuelListView2.setBackgroundColor(selectedPosition == position ?
                    Color.parseColor("#0B29B1") : Color.parseColor("#FFB4D8F3"));
        } else {
            // covers the case of data not being ready
            holder.fuelListView1.setText("No Fuels Found");
        }
    }

    public void setWords(List<Fuel> fuels) {
        fuelList = fuels;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (fuelList != null) {
            return fuelList.size();
        }
        else return 0;
    }
}
