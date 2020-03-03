package com.shels.delivery.DataBaseUtils.ViewModel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.shels.delivery.Data.Barcode;
import com.shels.delivery.DataBaseUtils.DeliveryDataBase;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class BarcodeViewModel extends AndroidViewModel {
    private DeliveryDataBase dataBase;

    public BarcodeViewModel(@NonNull Application application) {
        super(application);

        dataBase = DeliveryDataBase.getInstance(getApplication());
    }

     public List<Barcode> getBarcodesByProductId(String productId){
        try {
            return new GetBarcodesByProductId().execute(productId).get();
        } catch (ExecutionException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }

    public void insertBarcodes(List<Barcode> barcodes){
        new InsertBarcodesTask().execute(barcodes);
    }

    public void deleteAllBarcodes(){
        new DeleteAllBarcodesTask().execute();
    }

    public Barcode getBarcode(String barcode){
        try {
            return new GetBarcodeTask().execute(barcode).get();
        } catch (ExecutionException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }

    private class GetBarcodesByProductId extends AsyncTask<String, Void, List<Barcode>> {
        @Override
        protected List<Barcode> doInBackground(String... strings) {
            if (strings != null && strings.length > 0){
                return dataBase.barcodeDao().getBarcodesByProductId(strings[0]);
            }

            return null;
        }
    }

    private class InsertBarcodesTask extends AsyncTask<List<Barcode>, Void, Void>{
        @Override
        protected Void doInBackground(List<Barcode>... barcodes) {
            if (barcodes != null && barcodes.length > 0){
                dataBase.barcodeDao().insertBarcodes(barcodes[0]);
            }

            return null;
        }
    }

    private class DeleteAllBarcodesTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            dataBase.barcodeDao().deleteAllBarcodes();

            return null;
        }
    }

    private class GetBarcodeTask extends AsyncTask<String, Void, Barcode>{
        @Override
        protected Barcode doInBackground(String... strings) {
            if (strings.length > 0 && strings != null){
                return dataBase.barcodeDao().getBarcode(strings[0]);
            }

            return null;
        }
    }
}
