package com.example.samsung.calcul;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Data {
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "calcul")
    String calcul;

    @ColumnInfo(name = "resultat")
    String resultat;





}
