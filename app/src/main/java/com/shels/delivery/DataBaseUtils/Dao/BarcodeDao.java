package com.shels.delivery.DataBaseUtils.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.shels.delivery.Data.Barcode;

import java.util.List;

@Dao
public interface BarcodeDao {

    @Query("SELECT * FROM barcodes WHERE productId = :productId")
    List<Barcode> getBarcodesByProductId(String productId);

    @Query("SELECT * FROM barcodes WHERE barcode = :barcode")
    Barcode getBarcode(String barcode);

    @Insert
    void insertBarcodes(List<Barcode> barcodes);

    @Query("DELETE FROM barcodes")
    void deleteAllBarcodes();

}
