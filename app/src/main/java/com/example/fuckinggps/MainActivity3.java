package com.example.fuckinggps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        RecordsViewModel viewModel = new ViewModelProvider(this).get(RecordsViewModel.class);
        List<Records> recordsList = viewModel.getAllRecords();
        Map<Long, List<Records>> recordsMap = new HashMap<>();
        // 分组
        for (Records records : recordsList) {
            Long time = records.getTime();
            if (recordsMap.containsKey(time)) {
                List<Records> list = Objects.requireNonNull(recordsMap.get(time));
                list.add(records);
            } else {
                List<Records> list = new ArrayList<>();
                list.add(records);
                recordsMap.put(records.getTime(), list);
            }
        }
        List<TotalRecords> totalRecordsList = new ArrayList<>();
        for (List<Records> list : recordsMap.values()) {
            list.sort(Comparator.comparingInt(Records::getDuration));
            float totalDistance = 0f;
            for (Records records : list) {
                totalDistance += records.getDistance();
            }
            totalRecordsList.add(new TotalRecords(list.get(list.size() - 1), totalDistance));
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TotalRecordsAdapter(totalRecordsList, records -> {
            Toast.makeText(this, "" + records.totalDistance, Toast.LENGTH_SHORT).show();
        }));
    }

    private static class TotalRecords {
        private final Records lastRecord;
        private final float totalDistance;

        public TotalRecords(Records lastRecord, float totalDistance) {
            this.lastRecord = lastRecord;
            this.totalDistance = totalDistance;
        }
    }

    private static class TotalRecordsAdapter extends RecyclerView.Adapter<TotalRecordsViewHolder> {

        private List<TotalRecords> items;
        private OnItemClickListener onItemClickListener;

        public TotalRecordsAdapter(List<TotalRecords> items, OnItemClickListener onItemClickListener) {
            this.items = items;
            this.onItemClickListener = onItemClickListener;
        }

        @NonNull
        @Override
        public TotalRecordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_total_records, parent, false);
            TotalRecordsViewHolder viewHolder = new TotalRecordsViewHolder(itemView);
            viewHolder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    onItemClickListener.onItemClick(items.get(position));
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull TotalRecordsViewHolder holder, int position) {
            TotalRecords records = items.get(position);
            holder.distance.setText(String.valueOf(records.totalDistance));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private static class TotalRecordsViewHolder extends RecyclerView.ViewHolder {

        private final TextView distance;

        private TotalRecordsViewHolder(@NonNull View itemView) {
            super(itemView);
            distance = itemView.findViewById(R.id.distance);
        }
    }

    private interface OnItemClickListener {
        void onItemClick(TotalRecords records);
    }
}