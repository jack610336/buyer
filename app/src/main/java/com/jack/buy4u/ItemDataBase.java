package com.jack.buy4u;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by Jack_Wang on 2018/1/14.
 */

//entities {Item.class , price.class 可以放多個}
@Database(entities = {Item.class},version = 1)
public abstract class ItemDataBase extends RoomDatabase {

    private static ItemDataBase instance;

    public abstract ItemDao itemDao();

    public static ItemDataBase getDatabase(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context,
                    ItemDataBase.class,"Buy4uu").build();
        }
        return instance;
    }
}
