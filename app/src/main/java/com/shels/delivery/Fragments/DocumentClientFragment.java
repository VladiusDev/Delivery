package com.shels.delivery.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.shels.delivery.Data.DeliveryAct;
import com.shels.delivery.DataBaseUtils.ViewModel.DeliveryActsViewModel;
import com.shels.delivery.R;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateSource;
import com.yandex.mapkit.map.Map;
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


public class DocumentClientFragment extends Fragment implements Session.SearchListener, CameraListener {
    private String documentId;
    private TextView client;
    private TextView phone;
    private TextView address;
    private DeliveryAct deliveryAct;
    private DeliveryActsViewModel deliveryActsViewModel;
    private MapView mapView;
    private SearchManager searchManager;
    private Session searchSession;
    private static final int REQUEST_PHONE_CALL = 1;

    public DocumentClientFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document_client, container, false);

        documentId = getArguments().getString("documentId");

        deliveryActsViewModel = ViewModelProviders.of(this).get(DeliveryActsViewModel.class);

        client = view.findViewById(R.id.document_client);
        phone = view.findViewById(R.id.document_phone);
        address = view.findViewById(R.id.document_address);

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               call();
            }
        });

        if (documentId != null){
            deliveryAct = deliveryActsViewModel.getDeliveryActById(documentId);
            if (deliveryAct != null){
                client.setText(deliveryAct.getClient());
                phone.setText(deliveryAct.getClientPhone());
                address.setText(deliveryAct.getDeliveryAddress());
            }
        }

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

        mapView = view.findViewById(R.id.document_mapView);
        mapView.getMap().addCameraListener(this);

        submitQuery(address.getText().toString());

        setHasOptionsMenu(true);

        return view;
    }

    private void submitQuery(String query) {
        searchSession = searchManager.submit(
                query,
                VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()),
                new SearchOptions(),
                this);
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
    public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateSource cameraUpdateSource, boolean finished) {

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
                            new Animation(Animation.Type.SMOOTH, 1),
                            null);
                }

                // Добавляем данные только по одной найденной точке
                // по этому просто прерываем цикл после добавления первой точки
                break;
            }
        }
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
}
