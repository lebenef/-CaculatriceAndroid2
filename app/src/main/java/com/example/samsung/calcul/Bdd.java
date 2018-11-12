package com.example.samsung.calcul;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Data.class}, version= 1)

public abstract class Bdd extends RoomDatabase {

    private static Bdd instance = null;
    public abstract DataDAO data();

    public static Bdd getInstance(Context context ){

        if(instance == null)
        {
            instance = Room.databaseBuilder(context, Bdd.class, "histo").build();
        }

        return instance;
    }


}
