package com.example.fuckinggps;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class RecordsViewModel extends AndroidViewModel {
    private MyRepository repository;
    private final List<Records> allRecords;
    private final List<Records> last_records;

    public RecordsViewModel(Application application) {
        super(application);
        repository = new MyRepository(application);
        allRecords = repository.getAllRecords();
        last_records = repository.getLast_records();

    }

    List<Records> getAllRecords() {
        return allRecords;
    }

    List<Records> getAllNewRecords() {
        return repository.getNewAllRecords();
    }

    List<Records> getLast_records() {
        return last_records;
    }

    List<Records> getLocation_records(long time) {
        return repository.getLocation_records(time);
    }

    public void insert(Records records) {
        repository.insert(records);
    }

    public void update(Records records) {
        repository.update(records);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void delete(long time) {
        repository.delete(time);
    }
}
