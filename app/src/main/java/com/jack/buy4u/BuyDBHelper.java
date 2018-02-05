package com.jack.buy4u;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jack_Wang on 2018/1/7.
 */

public class BuyDBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static BuyDBHelper instance;

    public static BuyDBHelper getInstance(Context context){
        if (instance == null){
            instance = new BuyDBHelper(context,"buy4u.db",null,DB_VERSION);
        }
        return instance;
    }
    private BuyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table groups ( " +
                "_id INTEGER PRIMARY KEY, " +
                "name VARCHAR)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
