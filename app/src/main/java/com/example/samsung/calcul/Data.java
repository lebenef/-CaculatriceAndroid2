package com.example.samsung.calcul;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Data {

    @ColumnInfo
    String calcul;
    @ColumnInfo
    String resultat;
    @PrimaryKey
    int id;




}
