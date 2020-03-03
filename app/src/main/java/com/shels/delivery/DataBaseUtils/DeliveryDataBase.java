package com.shels.delivery.DataBaseUtils;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.shels.delivery.Data.Barcode;
import com.shels.delivery.Data.DeliveryAct;
import com.shels.delivery.Data.Product;
import com.shels.delivery.DataBaseUtils.Dao.BarcodeDao;
import com.shels.delivery.DataBaseUtils.Dao.DocumentsDao;
import com.shels.delivery.DataBaseUtils.Dao.ProductDao;

@Database(entities = {DeliveryAct.class, Barcode.class, Product.class}, version = 1, exportSchema = false)
public abstract class DeliveryDataBase extends RoomDatabase {
    private static DeliveryDataBase dataBase;
    private static final String DB_NAME = "delivery.db";
    private static final Object LOCK = new Object();

    public static DeliveryDataBase getInstance(Context context){
        synchronized (LOCK) {
            if (dataBase == null) {
                dataBase = Room.databaseBuilder(context, DeliveryDataBase.class, DB_NAME).build();
            }
        }
        return dataBase;
    }

    public abstract DocumentsDao documentsDao();
    public abstract BarcodeDao barcodeDao();
    public abstract ProductDao productDao();
}
