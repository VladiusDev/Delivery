package com.shels.delivery.DataBaseUtils.ViewModel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.shels.delivery.Data.DeliveryAct;
import com.shels.delivery.DataBaseUtils.DeliveryDataBase;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class DeliveryActsViewModel extends AndroidViewModel {
    private DeliveryDataBase dataBase;
    private LiveData<List<DeliveryAct>> deliveryActs;

    public DeliveryActsViewModel(@NonNull Application application) {
        super(application);

        dataBase = DeliveryDataBase.getInstance(getApplication());
        deliveryActs = dataBase.documentsDao().getAllDeliveryActs();
    }

    public DeliveryAct getDeliveryActById(String id){
        try {
            return new GetDeliveryActByIdTask().execute(id).get();
        } catch (ExecutionException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }

    public LiveData<List<DeliveryAct>> getDeliveryActs() {
        return deliveryActs;
    }

    public DeliveryAct getDeliveryActByBarcode(String barcode){
        try {
            return new GetDeliveryActByBarcodeTask().execute(barcode).get();
        } catch (ExecutionException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }

    public int getCompletedDeliveryActs(){
        try {
            return new GetCompletedDeliveryActsTask().execute().get();
        } catch (ExecutionException e) {
            return 0;
        } catch (InterruptedException e) {
            return 0;
        }
    }

    public List<DeliveryAct> getDeliveryActsByFilter(String filter){
        try {
            return new GetDeliveryActsByFilter().execute(filter).get();
        } catch (ExecutionException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }

    public void insertDeliveryActs(List<DeliveryAct> deliveryActs){
        new InsertTask().execute(deliveryActs);
    }

    public void updateDeliveryAct(DeliveryAct deliveryAct){
        new UpdateDeliveryActTask().execute(deliveryAct);
    }

    public void deleteAllDeliveryActs(){
        new DeleteAllTask().execute();
    }

    private class InsertTask extends AsyncTask<List<DeliveryAct>, Void, Void>{
        @Override
        protected Void doInBackground(List<DeliveryAct>... deliveryActs) {
            if (deliveryActs != null && deliveryActs.length > 0) {
                dataBase.documentsDao().insertDeliveryActs(deliveryActs[0]);
            }

            return null;
        }
    }

    private class GetDeliveryActByIdTask extends AsyncTask<String, Void, DeliveryAct>{
        @Override
        protected DeliveryAct doInBackground(String... strings) {
            if (strings != null && strings.length > 0){
               return dataBase.documentsDao().getDeliveryActById(strings[0]);
            }

            return null;
        }
    }

    private class DeleteAllTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            dataBase.documentsDao().deleteAllDeliveryActs();

            return null;
        }
    }

    private class GetDeliveryActByBarcodeTask extends AsyncTask<String, Void, DeliveryAct>{
        @Override
        protected DeliveryAct doInBackground(String... strings) {
            if (strings.length > 0 && strings != null){
                return dataBase.documentsDao().getDeliveryActByBarcode(strings[0]);
            }

            return null;
        }
    }

    private class GetCompletedDeliveryActsTask extends AsyncTask<Void, Void, Integer>{
        @Override
        protected Integer doInBackground(Void... voids) {
            return dataBase.documentsDao().getCompletedDeliveryActs();
        }
    }

    private class GetDeliveryActsByFilter extends AsyncTask<String, Void, List<DeliveryAct>>{
        @Override
        protected List<DeliveryAct> doInBackground(String... strings) {
            if (strings.length > 0 && strings != null){
                return dataBase.documentsDao().getDeliveryActsByFilter(strings[0]);
            }

            return null;
        }
    }

    private class UpdateDeliveryActTask extends AsyncTask<DeliveryAct, Void, Void>{
        @Override
        protected Void doInBackground(DeliveryAct... deliveryActs) {
            if (deliveryActs.length > 0 && deliveryActs != null){
                dataBase.documentsDao().updateDeliveryAct(deliveryActs[0]);
            }

            return null;
        }
    }
}
