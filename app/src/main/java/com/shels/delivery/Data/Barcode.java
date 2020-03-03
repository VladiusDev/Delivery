package com.shels.delivery.Data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "actBarcodes")
public class Barcode {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String barcode;
    private String productId;

    public Barcode(String barcode, String productId) {
        this.barcode = barcode;
        this.productId = productId;
    }

    public int getId() {
        return id;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getDocumentId() {
        return productId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setDocumentId(String documentId) {
        this.productId = documentId;
    }

    public String getProductId() {
        return productId;
    }
}
