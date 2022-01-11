package com.example.fuckinggps;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecordsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Records records);

    @Update
    void update(Records records);

    @Query("DELETE FROM runner_tracker")
    void deleteAll();

    @Query("SELECT * FROM runner_tracker ORDER BY id ASC")
    List<Records> getAlphabetizedRecords();

    @Query("SELECT * FROM runner_tracker where id=(select max(id) from runner_tracker)")
    List<Records> getLast_records();

    @Query("SELECT * FROM runner_tracker where time = :time1")
    List<Records> getLocation_records(long time1);

    @Query("DELETE FROM runner_tracker where time = :time1")
    void delete(long time1);
}
