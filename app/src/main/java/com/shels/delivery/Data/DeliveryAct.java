package com.shels.delivery.Data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.shels.delivery.Data.DataUtils.DataTypeConverter;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "deliveryActs")
public class DeliveryAct {
    @PrimaryKey
    @NonNull
    private String id;
    private String number;
    private String date;
    private String partner;
    private String responsible;
    private String warehouse;
    private String car;
    private String sum;
    private String docId;
    private String docNumber;
    private String docDate;
    private String docClient;
    private String docClientPhone;
    private String docDeliveryAddress;
    private Integer docType;
    private String docSender;
    private String docRecipient;
    private String docBarcode;
    private String docTime;
    private Integer docStatus;

    @Ignore
    private List<Product> products = new ArrayList<>();

    @TypeConverters(DataTypeConverter.class)
    private List<String> documentsPhotos = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getDocDate() {
        return docDate;
    }

    public void setDocDate(String docDate) {
        this.docDate = docDate;
    }

    public String getDocClient() {
        return docClient;
    }

    public void setDocClient(String docClient) {
        this.docClient = docClient;
    }

    public String getDocClientPhone() {
        return docClientPhone;
    }

    public void setDocClientPhone(String docClientPhone) {
        this.docClientPhone = docClientPhone;
    }

    public String getDocDeliveryAddress() {
        return docDeliveryAddress;
    }

    public void setDocDeliveryAddress(String docDeliveryAddress) {
        this.docDeliveryAddress = docDeliveryAddress;
    }

    public Integer getDocType() {
        return docType;
    }

    public void setDocType(Integer docType) {
        this.docType = docType;
    }

    public String getDocSender() {
        return docSender;
    }

    public void setDocSender(String docSender) {
        this.docSender = docSender;
    }

    public String getDocRecipient() {
        return docRecipient;
    }

    public void setDocRecipient(String docRecipient) {
        this.docRecipient = docRecipient;
    }

    public String getDocBarcode() {
        return docBarcode;
    }

    public void setDocBarcode(String docBarcode) {
        this.docBarcode = docBarcode;
    }

    public String getDocTime() {
        return docTime;
    }

    public void setDocTime(String docTime) {
        this.docTime = docTime;
    }

    public Integer getDocStatus() {
        return docStatus;
    }

    public void setDocStatus(Integer docStatus) {
        this.docStatus = docStatus;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<String> getDocumentsPhotos() {
        return documentsPhotos;
    }

    public void setDocumentsPhotos(List<String> documentsPhotos) {
        this.documentsPhotos = documentsPhotos;
    }
}
