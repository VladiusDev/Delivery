package com.shels.delivery.DataBaseUtils.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.shels.delivery.Data.DeliveryAct;

import java.util.List;

@Dao
public interface DocumentsDao {

    @Query("SELECT * FROM deliveryActs ORDER BY time")
    LiveData<List<DeliveryAct>> getAllDeliveryActs();

    @Query("SELECT * FROM deliveryActs WHERE id = :id")
    DeliveryAct getDeliveryActById(String id);

    @Query("SELECT * FROM deliveryActs WHERE barcode = :barcode LIMIT 1")
    DeliveryAct getDeliveryActByBarcode(String barcode);

    @Query("SELECT * FROM deliveryActs WHERE client LIKE  '%' || :filter || '%' OR time LIKE  '%' || :filter || '%' OR type LIKE  '%' || :filter || '%' OR clientPhone LIKE  '%' || :filter || '%'")
    List<DeliveryAct> getDeliveryActsByFilter(String filter);

    @Query("SELECT COUNT (*) FROM deliveryActs WHERE status = 1 OR status = 2")
    int getCompletedDeliveryActs();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertDeliveryActs(List<DeliveryAct> deliveryActs);

    @Update
    void updateDeliveryAct(DeliveryAct deliveryAct);

    @Query("DELETE FROM deliveryActs")
    void deleteAllDeliveryActs();
}
