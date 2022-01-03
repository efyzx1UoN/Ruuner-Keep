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

    List<Records> getLast_records() {
        return last_records;
    }

    public void insert(Records records) {
        repository.insert(records);
    }

    public void delete() {
        repository.delete();
    }
}
