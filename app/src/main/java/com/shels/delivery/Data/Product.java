package com.shels.delivery.Data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {

    @PrimaryKey
    @NonNull
    private String id;
    private String productId;
    private String documentId;
    private String name;
    private String characteristic;
    private int amount;
    private int scanned;

    public Product(String productId, String name, String characteristic, int amount, String documentId) {
        this.productId = productId;
        this.name = name;
        this.characteristic = characteristic;
        this.amount = amount;
        this.documentId = documentId;
        this.id = documentId + productId;
    }

    public int getScanned() {
        return scanned;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getCharacteristic() {
        return characteristic;
    }

    public int getAmount() {
        return amount;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCharacteristic(String characteristic) {
        this.characteristic = characteristic;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setScanned(int scanned) {
        this.scanned = scanned;
    }
}
