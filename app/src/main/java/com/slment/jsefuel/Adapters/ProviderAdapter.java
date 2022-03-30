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

import com.slment.jsefuel.Entities.Provider;
import com.slment.jsefuel.R;

import java.util.List;
import java.text.SimpleDateFormat;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder> {
    public int selectedPosition = -1;
    public Provider current = null;
    class ProviderViewHolder extends RecyclerView.ViewHolder {
        private final TextView providerListView1;
        private final TextView providerListView2;
        private final String LOG_TAG = "ProviderAdapterLog";

        private ProviderViewHolder(View itemView) {
            super(itemView);
            providerListView1 = itemView.findViewById(R.id.tvProvider1);
            providerListView2 = itemView.findViewById(R.id.tvProvider2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition() == RecyclerView.NO_POSITION) return;
                    notifyItemChanged(selectedPosition);
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(selectedPosition);
                    current = providerList.get(selectedPosition);
                }
            });
        }
    }

    private final LayoutInflater inflater;
    private final Context context;
    private List<Provider> providerList;

    public ProviderAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ProviderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_provider, parent, false);
        return new ProviderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProviderViewHolder holder, int position) {
        if (providerList != null) {
            String buildString1, buildString2;
            final Provider current = providerList.get(position);
            buildString1 = current.getName();
            holder.providerListView1.setText(buildString1);
            String tempStr = current.getAddress().trim() + ", " + current.getCity().trim();
            if (tempStr.compareTo(", ") == 0) {
                buildString2 = "  ";
            } else {
                buildString2 = tempStr;
            }
            holder.providerListView2.setText(buildString2);
            holder.itemView.setBackgroundColor(selectedPosition == position ?
                    Color.parseColor("#0B29B1") : Color.parseColor("#FFB4D8F3"));
            holder.providerListView1.setTextColor(selectedPosition == position ?
                    Color.WHITE : Color.BLACK);
            holder.providerListView2.setTextColor(selectedPosition == position ?
                    Color.WHITE : Color.BLACK);
            holder.providerListView1.setBackgroundColor(selectedPosition == position ?
                    Color.parseColor("#0B29B1") : Color.parseColor("#FFB4D8F3"));
            holder.providerListView2.setBackgroundColor(selectedPosition == position ?
                    Color.parseColor("#0B29B1") : Color.parseColor("#FFB4D8F3"));
        } else {
            // covers the case of data not being ready
            holder.providerListView1.setText("No Providers Found");
        }
    }

    public void setWords(List<Provider> providers) {
        providerList = providers;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (providerList != null) {
            return providerList.size();
        }
        else return 0;
    }
}
