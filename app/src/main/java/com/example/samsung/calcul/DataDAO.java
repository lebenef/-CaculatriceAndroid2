package com.example.samsung.calcul;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DataDAO {
    @Insert
    void insertData(Data d);

    @Delete
    void deleteData(Data d);

    @Query("select * from Data")
    List<Data> getAll();
}
