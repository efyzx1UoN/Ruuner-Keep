package com.example.fuckinggps;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecordsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Records records);

    @Query("DELETE FROM runner_tracker")
    void deleteAll();

    @Query("SELECT * FROM runner_tracker ORDER BY id ASC")
   List<Records> getAlphabetizedRecords();

    @Query("SELECT * FROM runner_tracker where id=(select max(id) from runner_tracker)")
    List<Records> getLast_records();
}
