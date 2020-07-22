package com.shels.delivery.Data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "barcodes")
public class Barcode {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String barcode;
    private String productId;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
