package com.shels.delivery.JsonUtils;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.shels.delivery.Constants;
import com.shels.delivery.Data.Barcode;
import com.shels.delivery.Data.Product;
import com.shels.delivery.Data.DeliveryAct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class JsonParser1C {

    public static ContentValues getUserInfo(String jsonText){
        try {
            return new GetUserInfoTask().execute(jsonText).get();
        } catch (ExecutionException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }

    public static HashMap getDeliveryActs(String jsonText){
        try {
            return new GetDeliveryActs().execute(jsonText).get();
        } catch (ExecutionException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }

    private static class GetUserInfoTask extends AsyncTask<String, Void, ContentValues>{
        @Override
        protected ContentValues doInBackground(String... strings) {
            ContentValues result = new ContentValues();

            try {
                JSONObject jsonObject = new JSONObject(strings[0]);
                JSONObject data = jsonObject.getJSONObject(Constants.JSON_FIELD_DATA);

                result.put(Constants.JSON_FIELD_USER_ID, data.getString(Constants.JSON_FIELD_USER_ID));
                result.put(Constants.JSON_FIELD_USER, data.getString(Constants.JSON_FIELD_USER));
                result.put(Constants.JSON_FIELD_USER_PROFILE, data.getString(Constants.JSON_FIELD_USER_PROFILE));

                return result;
            } catch (JSONException e) {
                return result;
            }
        }
    }

    private static class GetDeliveryActs extends AsyncTask<String, Void, HashMap>{
        @Override
        protected HashMap doInBackground(String... strings) {
            HashMap result = new HashMap();

            List<DeliveryAct> deliveryActs = new ArrayList<>();
            List<Product> actGoods = new ArrayList<>();
            List<Barcode> productBarcodes = new ArrayList<>();

            result.put(Constants.JSON_FIELD_DELIVERY_ACTS, deliveryActs);
            result.put(Constants.JSON_FIELD_GOODS, actGoods);
            result.put(Constants.JSON_FIELD_DELIVERY_BARCODES, productBarcodes);

            try {
                JSONObject jsonObject = new JSONObject(strings[0]);
                JSONArray data = jsonObject.getJSONArray(Constants.JSON_FIELD_DATA);

                for (int i = 0; i < data.length(); i++){
                    JSONObject deliveryActJson = data.getJSONObject(i);
                    JSONArray goodsJson = deliveryActJson.getJSONArray(Constants.JSON_FIELD_GOODS);

                    // Накладная
                    String  act_id = deliveryActJson.getString("act_id");
                    String  act_number = deliveryActJson.getString("act_number");
                    String  act_date = deliveryActJson.getString("act_date");
                    String  act_partner = deliveryActJson.getString("act_partner");
                    String  act_responsible = deliveryActJson.getString("act_responsible");
                    String  act_warehouse = deliveryActJson.getString("act_warehouse");
                    String  act_car = deliveryActJson.getString("act_car");
                    String  act_sum = deliveryActJson.getString("act_sum");
                    String  document_id = deliveryActJson.getString("document_id");
                    String  document_number = deliveryActJson.getString("document_number");
                    String  document_date = deliveryActJson.getString("document_date");
                    String  document_client = deliveryActJson.getString("document_client");
                    String  document_client_phone = deliveryActJson.getString("document_client_phone");
                    String  document_delivery_address = deliveryActJson.getString("document_delivery_address");
                    String  document_sender = deliveryActJson.getString("document_sender");
                    String  document_recipient = deliveryActJson.getString("document_recipient");
                    String  document_time = deliveryActJson.getString("document_time");
                    String  document_barcode = deliveryActJson.getString("document_barcode");
                    int  document_type = deliveryActJson.getInt("document_type");
                    int  document_status = deliveryActJson.getInt("document_status");

                    // Товары по накладной
                    for (int a = 0; a < goodsJson.length(); a++){
                        JSONObject productJson = goodsJson.getJSONObject(a);

                        String name = productJson.getString("name");
                        String characteristic = productJson.getString("characteristic");
                        String productId = productJson.getString("product_id");
                        int amount = productJson.getInt("amount");

                        // Штрихкода по товару
                        JSONArray productBarcodesJson = productJson.getJSONArray(Constants.JSON_FIELD_DELIVERY_BARCODES);
                        for (int b = 0; b < productBarcodesJson.length(); b++){
                            JSONObject productBarcode = productBarcodesJson.getJSONObject(b);

                            String barcode = productBarcode.getString("barcode");

                            productBarcodes.add(new Barcode(barcode, productId));
                        }

                        actGoods.add(new Product(productId, name, characteristic, amount, document_id));
                    }

                    deliveryActs.add(new DeliveryAct(document_id, document_number, document_date, document_time, document_barcode, document_client,
                            document_client_phone, document_delivery_address, document_recipient, document_sender, document_type,
                            act_id, act_number, act_date, act_partner, act_responsible, act_warehouse, act_car, act_sum, document_status));
                }

                return result;
            } catch (JSONException e) {
                return result;
            }
        }
    }

}
