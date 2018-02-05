package com.jack.buy4u;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Jack_Wang on 2018/1/14.
 */
@Dao
public interface ItemDao {

    @Query("select * from " + Item.TABLE)
    public List<Item> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 如果有重複ID 取代掉值
    public void insert(Item item);

    @Delete
    public void delete(Item item);


}
