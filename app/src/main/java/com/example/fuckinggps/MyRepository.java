package com.example.fuckinggps;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MyRepository {
    private RecordsDao recordsDao;
    private List<Records> allRecords;
    private List<Records> last_records;
    MyRepository(Application application) {
        MyRoomDatabase  db = MyRoomDatabase.getDatabase(application);

        recordsDao = db.recordsDao();

        allRecords=recordsDao.getAlphabetizedRecords();

        last_records=recordsDao.getLast_records();
    }
    List<Records> getAllRecords() {
        return allRecords;
    }

    List<Records> getLast_records(){ return  last_records;}

    void insert(Records records) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            recordsDao.insert(records);
        });
    }
    void delete(){
        MyRoomDatabase.databaseWriteExecutor.execute(() ->{
            recordsDao.deleteAll();
        });
    }

}
