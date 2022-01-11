package com.example.fuckinggps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity_location_detail extends AppCompatActivity {
    private List<Records> location_records;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_location_detail);
        RecordsViewModel viewModel = new ViewModelProvider(this).get(RecordsViewModel.class);
        Intent intent =getIntent();
        long time = intent.getLongExtra("time", 1);
         location_records = viewModel.getLocation_records(time);
        List<LocationRecords> RecordsList = new ArrayList<>();
        for (int i = 0; i <location_records.size(); i++) {
            if(location_records.get(i).getLongitude()!=0){
                RecordsList.add(new LocationRecords(location_records.get(i)));
            }
         }

        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new LocationRecordsAdapter(RecordsList));
    }
    private static class LocationRecords {
        private final Records locationRecord;
        public LocationRecords(Records locationRecord) {
            this.locationRecord = locationRecord;
        }
    }
    private static class LocationRecordsAdapter extends RecyclerView.Adapter<LocationRecordsViewHolder> {
        private List<LocationRecords> items;
        public LocationRecordsAdapter(List<LocationRecords> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public LocationRecordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_location_detail_row,null);
            LocationRecordsViewHolder viewHolder = new LocationRecordsViewHolder(itemView);
            return viewHolder;
        }
        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull LocationRecordsViewHolder holder, int position) {
            LocationRecords records = items.get(position);
            holder.longitude.setText("Longitude :"+records.locationRecord.getLongitude());
            holder.latitude.setText("Latitude :"+records.locationRecord.getLatitude());
        }
        @Override
        public int getItemCount() {
            return items.size();
        }
    }
    private static class LocationRecordsViewHolder extends RecyclerView.ViewHolder {

        private final TextView longitude;
        private final TextView latitude;

        private LocationRecordsViewHolder(@NonNull View itemView) {
            super(itemView);
            longitude = itemView.findViewById(R.id.text_longitude);
            latitude = itemView.findViewById(R.id.text_latitude);
        }
    }
}