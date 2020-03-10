package com.shels.delivery.DataBaseUtils.ViewModel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.shels.delivery.Data.Product;
import com.shels.delivery.DataBaseUtils.DeliveryDataBase;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProductViewModel extends AndroidViewModel{
    private DeliveryDataBase dataBase;

    public ProductViewModel(@NonNull Application application) {
        super(application);

        dataBase = DeliveryDataBase.getInstance(getApplication());
    }

    public LiveData<List<Product>> getProductsByDocumentId (String documentId){
        try {
            return new GetProductsByDocumentIdTask().execute(documentId).get();
        } catch (ExecutionException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }

    public void insertProducts(List<Product> products){
        new InsertProductsTask().execute(products);
    }

    public void UpdateProductTask(Product product){
        new UpdateProductTask().execute(product);
    }

    public Product getProductByProductDocumentId(String productId, String documentId){
        try {
            return new GetProductByProductDocumentIdTask().execute(productId, documentId).get();
        } catch (ExecutionException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }

    public void deleteAllProducts(){
        new DeleteAllProductsTask().execute();
    }

    public int getScanned(String documentId){
        try {
            return new GetScannedTask().execute(documentId).get();
        } catch (ExecutionException e) {
            return 0;
        } catch (InterruptedException e) {
            return 0;
        }
    }

    public int getAmount(String documentId){
        try {
            return new GetAmountTask().execute(documentId).get();
        } catch (ExecutionException e) {
            return 0;
        } catch (InterruptedException e) {
            return 0;
        }
    }

    private class GetProductsByDocumentIdTask extends AsyncTask<String, Void, LiveData<List<Product>>>{
        @Override
        protected LiveData<List<Product>> doInBackground(String... strings) {
            if (strings != null && strings.length > 0){
               return dataBase.productDao().getProductsByDocumentId(strings[0]);
            }

            return null;
        }
    }

    private class InsertProductsTask extends AsyncTask<List<Product>, Void, Void>{
        @Override
        protected Void doInBackground(List<Product>... products) {
            if (products != null && products.length > 0){
                dataBase.productDao().insertProducts(products[0]);
            }

            return null;
        }
    }

    private class DeleteAllProductsTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            dataBase.productDao().deleteAllProducts();

            return null;
        }
    }

    private class UpdateProductTask extends AsyncTask<Product, Void, Void>{
        @Override
        protected Void doInBackground(Product... products) {
            if (products.length > 0 && products != null){
                dataBase.productDao().updateProduct(products[0]);
            }

            return null;
        }
    }

    private class GetProductByProductDocumentIdTask extends AsyncTask<String, Void, Product>{
        @Override
        protected Product doInBackground(String... strings) {
            if (strings != null && strings.length > 0){
                return dataBase.productDao().getProductByProductDocumentId(strings[0], strings[1]);
            }

            return null;
        }
    }

    private class GetScannedTask extends AsyncTask<String, Void, Integer>{
        @Override
        protected Integer doInBackground(String... strings) {
            if (strings != null && strings.length > 0 ){
                return dataBase.productDao().getScanned(strings[0]);
            }

            return null;
        }
    }

    private class GetAmountTask extends AsyncTask<String, Void, Integer>{
        @Override
        protected Integer doInBackground(String... strings) {
            if (strings != null && strings.length > 0 ){
                return dataBase.productDao().getAmount(strings[0]);
            }

            return null;
        }
    }
}
