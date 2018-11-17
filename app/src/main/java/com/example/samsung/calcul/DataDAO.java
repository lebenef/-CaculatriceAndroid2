package com.example.samsung.calcul;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DataDAO {
    @Insert
    void insertData(Data d);

    @Query("delete from Data where calcul = :calcul")
    void deleteData(String calcul);

    @Query("delete from Data")
    void deleteAll();

    @Query("select * from Data")
    LiveData<List<Data>> getAll();
}