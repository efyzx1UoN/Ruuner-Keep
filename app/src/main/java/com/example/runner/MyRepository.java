package com.example.runner;

import android.app.Application;

import java.util.List;

public class MyRepository {
    private RecordsDao recordsDao;
    private List<Records> allRecords;
    private List<Records> last_records;

    MyRepository(Application application) {
        MyRoomDatabase db = MyRoomDatabase.getDatabase(application);

        recordsDao = db.recordsDao();

        allRecords = recordsDao.getAlphabetizedRecords();

        last_records = recordsDao.getLast_records();

    }

    List<Records> getAllRecords() {
        return allRecords;
    }

    List<Records> getNewAllRecords() {
        allRecords = recordsDao.getAlphabetizedRecords();
        return allRecords;
    }

    List<Records> getLast_records() {
        return last_records;
    }

    List<Records> getLocation_records(long time) {
        return recordsDao.getLocation_records(time);
    }

    void insert(Records records) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            recordsDao.insert(records);
        });
    }

    void update(Records records) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            recordsDao.update(records);
        });
    }

    void deleteAll() {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            recordsDao.deleteAll();
        });
    }

    void delete(long time) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            recordsDao.delete(time);
        });
    }
}


