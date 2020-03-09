package com.shels.delivery.Data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.shels.delivery.Data.DataUtils.DataTypeConverter;

import java.util.List;

@Entity(tableName = "deliveryActs")
@TypeConverters({DataTypeConverter.class})
public class DeliveryAct {

    public DeliveryAct(String id, String number, String date, String time, String barcode, String client,
                       String clientPhone, String deliveryAddress, String warehouseRecipient, String warehouseSender,
                       int type, String actId, String actNumber, String actDate,
                       String actPartner, String actResponsible, String actWarehouse, String actCar, String actSum, int status) {
        this.id = id;
        this.number = number;
        this.date = date;
        this.client = client;
        this.clientPhone = clientPhone;
        this.deliveryAddress = deliveryAddress;
        this.warehouseRecipient = warehouseRecipient;
        this.warehouseSender = warehouseSender;
        this.type = type;
        this.actId = actId;
        this.actNumber = actNumber;
        this.actDate = actDate;
        this.actPartner = actPartner;
        this.actResponsible = actResponsible;
        this.actWarehouse = actWarehouse;
        this.actCar = actCar;
        this.actSum = actSum;
        this.time = time;
        this.barcode = barcode;
        this.status = status;
    }

    // Реквизиты накладной
    @PrimaryKey
    @NonNull
    private String id;
    private String number;
    private String date;
    private String client;
    private String clientPhone;
    private String deliveryAddress;
    private String warehouseRecipient;
    private String warehouseSender;
    private String time;
    private String barcode;
    private int type; // 1 - Реализация 2 - Вывоз рекламации 3 - Доставка по рекламации 4 - Перемещение товаров
    private int status; // 0 - Не доставлено, 1 - Доставлено, 2 - Доставлено частично
    private List<String> documentsPhotos;

    // Реквизиты акта доставки
    private String actId;
    private String actNumber;
    private String actDate;
    private String actPartner;
    private String actResponsible;
    private String actWarehouse;
    private String actCar;
    private String actSum;

    public List<String> getDocumentsPhotos() {
        return documentsPhotos;
    }

    public String getBarcode() {
        return barcode;
    }

    public int getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getDate() {
        return date;
    }

    public String getClient() {
        return client;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getWarehouseRecipient() {
        return warehouseRecipient;
    }

    public String getWarehouseSender() {
        return warehouseSender;
    }

    public int getType() {
        return type;
    }

    public String getActId() {
        return actId;
    }

    public String getActNumber() {
        return actNumber;
    }

    public String getActDate() {
        return actDate;
    }

    public String getActPartner() {
        return actPartner;
    }

    public String getActResponsible() {
        return actResponsible;
    }

    public String getActWarehouse() {
        return actWarehouse;
    }

    public String getActCar() {
        return actCar;
    }

    public String getActSum() {
        return actSum;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setWarehouseRecipient(String warehouseRecipient) {
        this.warehouseRecipient = warehouseRecipient;
    }

    public void setWarehouseSender(String warehouseSender) {
        this.warehouseSender = warehouseSender;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public void setActNumber(String actNumber) {
        this.actNumber = actNumber;
    }

    public void setActDate(String actDate) {
        this.actDate = actDate;
    }

    public void setActPartner(String actPartner) {
        this.actPartner = actPartner;
    }

    public void setActResponsible(String actResponsible) {
        this.actResponsible = actResponsible;
    }

    public void setActWarehouse(String actWarehouse) {
        this.actWarehouse = actWarehouse;
    }

    public void setActCar(String actCar) {
        this.actCar = actCar;
    }

    public void setActSum(String actSum) {
        this.actSum = actSum;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setDocumentsPhotos(List<String> documentsPhotos) {
        this.documentsPhotos = documentsPhotos;
    }
}
