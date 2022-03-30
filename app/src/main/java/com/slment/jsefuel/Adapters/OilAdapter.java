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

import com.slment.jsefuel.Entities.OilChange;
import com.slment.jsefuel.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class OilAdapter extends RecyclerView.Adapter<OilAdapter.OilViewHolder> {
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
    public OilChange current = null;
    class OilViewHolder extends RecyclerView.ViewHolder {
        private final TextView oilListView1;
        private final TextView oilListView2;
        private final String LOG_TAG = "OilAdapterLog";

        private OilViewHolder(View itemView) {
            super(itemView);
            oilListView1 = itemView.findViewById(R.id.tvOil1);
            oilListView2 = itemView.findViewById(R.id.tvOil2);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(getAdapterPosition() == RecyclerView.NO_POSITION) return;
//                    notifyItemChanged(selectedPosition);
//                    selectedPosition = getAdapterPosition();
//                    notifyItemChanged(selectedPosition);
//                    current = oilList.get(selectedPosition);
//                }
//            });
        }
    }

    private final LayoutInflater inflater;
    private final Context context;
    private List<OilChange> oilList;

    public OilAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public OilViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_oil, parent, false);
        return new OilViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OilViewHolder holder, int position) {
        if (oilList != null) {
            String buildString1, buildString2;
            final OilChange current = oilList.get(position);
            if (current.getOilChangeId() == -1) {
                selectedPosition = position;
                buildString1 = "Avg. Cost and Miles Between Changes";
                buildString2 = String.format("   Cost: $%.2f,   Miles: %.0f",
                        current.getTotalCost(), current.getOdometer());
            } else {
                workDate.setTime(current.getOilChangeDate());
                timeStr = sdf.format(workDate.getTime());
                buildString1 = String.format("Oil Change Date: %s",
                        timeStr);
                buildString2 = String.format("Miles: %.0f,  Cost: %.2f",
                        current.getOdometer(), current.getTotalCost());
            }
            holder.itemView.setBackgroundColor(selectedPosition == position ?
                    Color.parseColor("#0B29B1") : Color.parseColor("#FFB4D8F3"));
            holder.oilListView1.setBackgroundColor(selectedPosition == position ?
                    Color.parseColor("#0B29B1") : Color.parseColor("#FFB4D8F3"));
            holder.oilListView2.setBackgroundColor(selectedPosition == position ?
                    Color.parseColor("#0B29B1") : Color.parseColor("#FFB4D8F3"));
            holder.oilListView1.setTextColor(selectedPosition == position ?
                    Color.WHITE : Color.BLACK);
            holder.oilListView2.setTextColor(selectedPosition == position ?
                    Color.WHITE : Color.BLACK);
            holder.oilListView1.setText(buildString1);
            holder.oilListView2.setText(buildString2);
        } else {
            // covers the case of data not being ready
            holder.oilListView1.setText("No Oil Changes Found");
        }
    }

    public void setWords(List<OilChange> oils) {
        oilList = oils;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (oilList != null) {
            return oilList.size();
        }
        else return 0;
    }
}
