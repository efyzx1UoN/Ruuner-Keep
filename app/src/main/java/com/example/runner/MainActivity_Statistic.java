package com.example.runner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;




import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainActivity_Statistic extends AppCompatActivity {
    private TextView total_distance;
    private TextView farthest_distance;
    private float t_distance;
    private float t_f_distance =(float) Integer.MIN_VALUE;
    private long timeMillis = System.currentTimeMillis();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_statistic);

        RecordsViewModel viewModel = new ViewModelProvider(this).get(RecordsViewModel.class);

        total_distance=findViewById(R.id.text_total_distance);
        farthest_distance=findViewById(R.id.text_farthest_distance);

        List<Records> recordsList = viewModel.getAllRecords();
        Map<Long, List<Records>> recordsMap = new ConcurrentHashMap<>();
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
        //filter data
        MapChecked(recordsMap);
        String date = dateCheck(timeMillis);

        List<TodayRecords> t_list= new CopyOnWriteArrayList<>();
        //filter data
        Set<Map.Entry<Long, List<Records>>> entries = recordsMap.entrySet();
        for (Map.Entry<Long, List<Records>> entry: entries ) {
            if (dateCheck(entry.getKey()).equals(date)){
                 t_list.add(new TodayRecords(entry.getValue().get(entry.getValue().size()-1)));
            }
        }
        //filter data
        for (TodayRecords todayRecords: t_list) {
            if(todayRecords.t_records.getDuration()==0){
                t_list.remove(todayRecords);
            }else if(todayRecords.t_records.getDistance()==0){
                t_list.remove(todayRecords);
            }
        }
        //calculation
        for(TodayRecords todayRecords: t_list){
            t_distance += todayRecords.t_records.getDistance();
            t_f_distance=Math.max(t_f_distance,todayRecords.t_records.getDistance());
        }
        //shown on screen
        if(t_distance==0){
            total_distance.setText("Total Distance of today is: " +0+" Meter");
            farthest_distance.setText("Longest Distance of today is: "+0+" Meter");
        }else{
            String d = new DecimalFormat("###,###,###").format(t_distance);
            String f = new DecimalFormat("###,###,###").format(t_f_distance);
            total_distance.setText("Total Distance of today is: " +d+" Meter");
            farthest_distance.setText("Longest Distance of today is: "+f+" Meter");
        }
        //shown by recycler view
        RecyclerView recyclerView = findViewById(R.id.recyclerView5);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TodayRecordsAdapter(t_list));
    }
    private static class TodayRecordsAdapter extends RecyclerView.Adapter<TodayRecordsViewHolder> {

        private List<TodayRecords> items;


        public TodayRecordsAdapter(List<TodayRecords> items) {
            this.items = items;

        }


        @NonNull
        @Override
        public TodayRecordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_statistic_row ,null);
            TodayRecordsViewHolder viewHolder = new TodayRecordsViewHolder(itemView);


            return viewHolder;
        }


        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull TodayRecordsViewHolder holder, int position) {
            TodayRecords records = items.get(position);
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = dateformat.format(records.t_records.getTime());
            String t = String.valueOf(records.t_records.getDuration());
            String d = new DecimalFormat("###,###,###").format(records.t_records.getDistance());
            String s = new DecimalFormat("###,###,###.#").format(records.t_records.getSpeed());
            holder.s_date.setText(dateStr);
            holder.s_distance.setText("Distance :"+" "+d+" Meter");
            holder.s_time.setText("Time :"+" "+t+" S");
            holder.s_speed.setText("AverageSpeed :"+" "+s+" M/S");

        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private static class TodayRecordsViewHolder extends RecyclerView.ViewHolder {

        private final TextView s_distance;
        private final TextView s_time;
        private final TextView s_speed;
        private final TextView s_date;

        private TodayRecordsViewHolder(@NonNull View itemView) {
            super(itemView);
            s_distance = itemView.findViewById(R.id.text_s_distance);
            s_time = itemView.findViewById(R.id.text_s_time);
            s_speed = itemView.findViewById(R.id.text_s_speed);
            s_date=itemView.findViewById(R.id.text_s_date);
        }
    }
    private static class TodayRecords{
        private final Records t_records;
        public TodayRecords(Records t_records){
            this.t_records=t_records;
        }
    }

    private void MapChecked(Map<Long, List<Records>> recordsMap){
        Set<Long> longs = recordsMap.keySet();
        for (Long l: longs) {
            if(recordsMap.get(l).size()==1){
                recordsMap.remove(l);
            }
        }
    }
    private String dateCheck(long time){
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = date_format.format(time);
        return dateStr;
    }
}