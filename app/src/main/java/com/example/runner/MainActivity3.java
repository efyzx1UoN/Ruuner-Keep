package com.example.runner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;




import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainActivity3 extends AppCompatActivity {
    private String TAG = "MainActivity3";
    private Map<Long, List<Records>> recordsMap;
    private RecordsViewModel viewModel;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        viewModel = new ViewModelProvider(this).get(RecordsViewModel.class);
        initial();
    }
    private void initial(){
        List<Records> recordsList = viewModel.getAllRecords();
        recordsMap = new ConcurrentHashMap<>();
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
        MapChecked(recordsMap);
        List<LastRecords> totalRecordsList = new CopyOnWriteArrayList<>();
        for (List<Records> list : recordsMap.values()) {
            list.sort(Comparator.comparingInt(Records::getDuration));
            totalRecordsList.add(new LastRecords(list.get(list.size() - 1)));
        }
        for (LastRecords lastRecords : totalRecordsList) {
            if (lastRecords.lastRecord.getDistance() == 0) {
                totalRecordsList.remove(lastRecords);
            } else if (lastRecords.lastRecord.getDuration() == 0) {
                totalRecordsList.remove(lastRecords);
            }

        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new LastRecordsAdapter(viewModel, totalRecordsList, records -> {
            Intent intent = new Intent(MainActivity3.this, MainActivity_detail.class);
            long time = records.lastRecord.getTime();
            intent.putExtra("time", time);
            startActivity(intent);
        }));
    }
    private void back_activity(){
        RecordsViewModel viewModel = new ViewModelProvider(this).get(RecordsViewModel.class);
        List<Records> recordsList = viewModel.getAllNewRecords();
        recordsMap = new ConcurrentHashMap<>();
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
        MapChecked(recordsMap);
        List<LastRecords> totalRecordsList = new CopyOnWriteArrayList<>();
        for (List<Records> list : recordsMap.values()) {
            list.sort(Comparator.comparingInt(Records::getDuration));

            totalRecordsList.add(new LastRecords(list.get(list.size() - 1)));
        }
        for (LastRecords lastRecords : totalRecordsList) {
            if (lastRecords.lastRecord.getDistance() == 0) {
                totalRecordsList.remove(lastRecords);
            } else if (lastRecords.lastRecord.getDuration() == 0) {
                totalRecordsList.remove(lastRecords);
            }

        }
        recyclerView.setAdapter(new LastRecordsAdapter(viewModel, totalRecordsList, records -> {
            Intent intent = new Intent(MainActivity3.this, MainActivity_detail.class);
            long time = records.lastRecord.getTime();
            String notes = records.lastRecord.getNotes();
            intent.putExtra("time", time);
            startActivity(intent);
        }));
    }

    private void MapChecked(Map<Long, List<Records>> recordsMap) {
        Set<Long> longs = recordsMap.keySet();
        for (Long l : longs) {
            if (recordsMap.get(l).size() == 1) {
                recordsMap.remove(l);
            }
        }
    }

    private static class LastRecords {
        private final Records lastRecord;

        public LastRecords(Records lastRecord) {
            this.lastRecord = lastRecord;

        }
    }

    private static class LastRecordsAdapter extends RecyclerView.Adapter<LastRecordsViewHolder> {
        private RecordsViewModel viewModel;
        private List<LastRecords> items;
        private OnItemClickListener onItemClickListener;

        public LastRecordsAdapter(RecordsViewModel viewModel, List<LastRecords> items, OnItemClickListener onItemClickListener) {
            this.viewModel = viewModel;
            this.items = items;
            this.onItemClickListener = onItemClickListener;
        }

        @NonNull
        @Override
        public LastRecordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity3_row, parent, false);
            LastRecordsViewHolder viewHolder = new LastRecordsViewHolder(itemView);

            viewHolder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    onItemClickListener.onItemClick(items.get(position));
                }
            });

            viewHolder.bar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                int position = viewHolder.getAdapterPosition();
                LastRecords item = items.get(position);
                item.lastRecord.setRating(rating);
                viewModel.update(item.lastRecord);
            });

            return viewHolder;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull LastRecordsViewHolder holder, int position) {
            LastRecords records = items.get(position);
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = dateformat.format(records.lastRecord.getTime());
            String t = String.valueOf(records.lastRecord.getDuration());
            String d = new DecimalFormat("###,###,###").format(records.lastRecord.getDistance());
            String s = new DecimalFormat("###,###,###.#").format(records.lastRecord.getSpeed());
            holder.date.setText(dateStr);
            holder.distance.setText("Distance :" + " " + d + " Meters");
            holder.time.setText("Time :" + " " + t + " Seconds");
            holder.speed.setText("AverageSpeed :" + " " + s + " M/S");
            holder.bar.setRating(records.lastRecord.getRating());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private static class LastRecordsViewHolder extends RecyclerView.ViewHolder {

        private final TextView distance;
        private final TextView time;
        private final TextView speed;
        private final TextView date;
        private RatingBar bar;

        private LastRecordsViewHolder(@NonNull View itemView) {
            super(itemView);
            distance = itemView.findViewById(R.id.text_distance);
            time = itemView.findViewById(R.id.text_time);
            speed = itemView.findViewById(R.id.text_speed);
            bar = itemView.findViewById(R.id.niceRatingBar);
            date = itemView.findViewById(R.id.text_date);
        }
    }

    private interface OnItemClickListener {
        void onItemClick(LastRecords records);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "Restart");
        back_activity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroy");
    }
}