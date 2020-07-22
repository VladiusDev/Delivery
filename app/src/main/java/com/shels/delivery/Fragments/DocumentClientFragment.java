package com.shels.delivery.Fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;
import com.shels.delivery.AuthorizationUtils;
import com.shels.delivery.Constants;
import com.shels.delivery.Data.DeliveryAct;
import com.shels.delivery.DataBaseUtils.ViewModel.DeliveryActsViewModel;
import com.shels.delivery.DataBaseUtils.ViewModel.DocumentViewModel;
import com.shels.delivery.DataBaseUtils.ViewModel.ProductViewModel;
import com.shels.delivery.R;
import com.shels.delivery.Retrofit.ApiFactory;
import com.shels.delivery.Retrofit.ApiService;
import com.shels.delivery.Retrofit.PostRequest;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;


public class DocumentClientFragment extends Fragment implements Session.SearchListener{
    private String documentId;
    private TextView client;
    private TextView phone;
    private TextView address;
    private DeliveryAct deliveryAct;
    private DeliveryActsViewModel deliveryActsViewModel;
    private ProductViewModel productViewModel;
    private DocumentViewModel documentViewModel;
    private MapView mapView;
    private SearchManager searchManager;
    private Session searchSession;
    private Button buttonExecuteTask;
    private LinearLayout progressBar;
    private Activity activity;
    private CameraPosition cameraPosition;
    private ConstraintLayout constraintLayout;

    private static final int REQUEST_PHONE_CALL = 1;

    public DocumentClientFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        deliveryActsViewModel = new ViewModelProvider(this).get(DeliveryActsViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        documentViewModel = new ViewModelProvider(this).get(DocumentViewModel.class);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document_client, container, false);

        documentId = getArguments().getString("documentId");

        activity = getActivity();

        progressBar = view.findViewById(R.id.document_progressBar);
        buttonExecuteTask = view.findViewById(R.id.document_btn_execute_task);
        client = view.findViewById(R.id.document_client);
        phone = view.findViewById(R.id.document_phone);
        address = view.findViewById(R.id.document_address);
        mapView = view.findViewById(R.id.document_mapView);
        constraintLayout = view.findViewById(R.id.document_card_constraintLayout);

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               call();
            }
        });

        buttonExecuteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeTask();
            }
        });

        if (documentId != null){
            deliveryAct = deliveryActsViewModel.getDeliveryActById(documentId);
            if (deliveryAct != null){
                client.setText(deliveryAct.getDocClient());
                phone.setText(deliveryAct.getDocClientPhone());
                address.setText(deliveryAct.getDocDeliveryAddress());
            }
        }

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

        submitQuery(address.getText().toString());

        return view;
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void submitQuery(String query) {
        searchSession = searchManager.submit(
                query,
                VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()),
                new SearchOptions(),
                this);

    }

    @SuppressLint("CheckResult")
    private void executeTask(){
        progressBar.setVisibility(View.VISIBLE);

        int amount;
        int scanned;
        int status;

        // Determine the status of the document before sending by the number of scanned goods
        amount = productViewModel.getAmount(documentId);
        scanned = productViewModel.getScanned(documentId);

        if (scanned != 0) {
            if (scanned >= amount) {
                // Delivered
                status = 1;
            } else if (amount > scanned) {
                // Partly delivered
                status = 2;
            } else {
                status = 0;
            }
        }else{
            status = 0;
        }

        // JSON request
        JSONObject jsonAnswer = new JSONObject();
        JSONArray jsonFiles   = new JSONArray();

        try {
            List<String> files  = deliveryAct.getDocumentsPhotos();

            // Document params
            jsonAnswer.put("documentId", deliveryAct.getDocId());
            jsonAnswer.put("documentType", deliveryAct.getDocType());
            jsonAnswer.put("documentStatus", status);
            jsonAnswer.put("deliveryActId", deliveryAct.getId());

            // Encode images to BASE64.
            if (files.size() > 0) {
                for (String file : files) {
                    String base64String = encodeImageToBase64(file);

                    jsonFiles.put(base64String);
                }
            }

            jsonAnswer.put("pictures", jsonFiles);

            // Get auth data from preference
            String usr = "";
            String pas = "";

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            if (preferences.contains(Constants.PREFERENCE_USER)) {
                usr = preferences.getString(Constants.PREFERENCE_USER, "");
            }
            if (preferences.contains(Constants.PREFERENCE_USER_PASSWORD)) {
                pas = preferences.getString(Constants.PREFERENCE_USER_PASSWORD, "");
            }

            ApiFactory apiFactory = ApiFactory.getInstance(getContext());
            ApiService apiService = apiFactory.getApiService();
            apiService.saveDocument(jsonAnswer.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PostRequest>() {
                    @Override
                    public void accept(PostRequest postRequest) throws Exception {
                        progressBar.setVisibility(View.GONE);

                        deliveryAct.setDocStatus(status);
                        deliveryActsViewModel.updateDeliveryAct(deliveryAct);

                        activity.setResult(1);
                        activity.finish();
                    }
                }, new Consumer<Throwable>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof HttpException) {
                            HttpException exception = (HttpException) throwable;
                            switch (exception.code()) {
                                case 401:
                                    // Authorization error, possibly changed the password, return to the auth page
                                    Toast.makeText(getContext(), getResources().getString(R.string.login_error2), Toast.LENGTH_LONG).show();

                                    AuthorizationUtils.clearAuthorization(getContext(), getActivity());
                                    break;
                                default:
                                    progressBar.setVisibility(View.GONE);
                                    Snackbar.make(constraintLayout, exception.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(constraintLayout, throwable.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
        } catch (JSONException e) {
            progressBar.setVisibility(View.GONE);
            Snackbar.make(constraintLayout, e.toString(), Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearchResponse(@NonNull Response response) {
        MapObjectCollection mapObjects = mapView.getMap().getMapObjects();
        mapObjects.clear();

        Context context = getContext();

        if (context != null) {
            for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
                Point resultLocation = searchResult.getObj().getGeometry().get(0).getPoint();
                if (resultLocation != null) {
                    mapObjects.addPlacemark(
                            resultLocation,
                            ImageProvider.fromResource(context, R.drawable.search_layer_pin_icon_default));

                    mapView.getMap().move(
                            new CameraPosition(resultLocation, 15.0f, 0.0f, 0.0f),
                            new Animation(Animation.Type.LINEAR, 0),
                            null);
                }

                // Add data for only one found point
                break;
            }
        }
    }

    @Override
    public void setRetainInstance(boolean retain) {
        super.setRetainInstance(retain);
    }

    @Override
    public void onSearchError(@NonNull Error error) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PHONE_CALL:
                if (permissions[0].equals(Manifest.permission.CALL_PHONE)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    call();
                }
                break;
        }
    }

    private void call(){
        if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        }else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle(getResources().getString(R.string.client_call)  + " " + phone.getText().toString() + "?");
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intentCall = new Intent(Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + phone.getText().toString()));

                    startActivity(intentCall);
                }
            });
            alertDialogBuilder.setNegativeButton(getResources().getString(R.string.no), null);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private String encodeImageToBase64(String file){
        // Получаем битовую карту изображения
        Bitmap bitmap = BitmapFactory.decodeFile(file);

        // Записываем изображение в поток байтов
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        // Получаем изображение из потока в виде байтов
        byte[] bytes = byteArrayOutputStream.toByteArray();

        // Кодируем байты в строку base64
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

}
