package com.shels.delivery.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
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
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
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

import java.util.List;


public class ClientsMapFragment extends Fragment implements Session.SearchListener, CameraListener, MapObjectTapListener {

    private MapView mapView;
    private DeliveryActsViewModel viewModel;
    private Session searchSession;
    private SearchManager searchManager;

    public ClientsMapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clients_map, container, false);

        viewModel = ViewModelProviders.of(this).get(DeliveryActsViewModel.class);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

        mapView = view.findViewById(R.id.yandex_mapView);
        mapView.getMap().addCameraListener(cameraListener);

        mapView.getMap().move(
                    new CameraPosition(new Point(45.389194, 33.993751), 8.0f, 0.0f, 0.0f),
                    new Animation(Animation.Type.SMOOTH, 5),
                    null);

        getData();

        return view;
    }

    @Override
    public void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    public void getData(){
        LiveData<List<DeliveryAct>> deliveryActsFromDB = viewModel.getDeliveryActs();
        deliveryActsFromDB.observe(this, new Observer<List<DeliveryAct>>() {
            @Override
            public void onChanged(List<DeliveryAct> deliveryActs) {
                MapObjectCollection mapObjects = mapView.getMap().getMapObjects();
                mapObjects.clear();

                for (DeliveryAct deliveryAct : deliveryActs){
                    submitQuery(deliveryAct.getDeliveryAddress());
                }
            }
        });
    }

    private void submitQuery(String query) {
        searchSession = searchManager.submit(
                query,
                VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()),
                new SearchOptions(),
                this);
    }

    @Override
    public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateSource cameraUpdateSource, boolean b) {

    }

    @Override
    public void onSearchResponse(@NonNull Response response) {
        MapObjectCollection mapObjects = mapView.getMap().getMapObjects();

        for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
            Point resultLocation = searchResult.getObj().getGeometry().get(0).getPoint();
            if (resultLocation != null) {
                Context context = getContext();

                if (context != null) {
                    ImageProvider resourceBackedImage = ImageProvider.fromResource(context, R.drawable.search_layer_pin_icon_default);
                    PlacemarkMapObject placemark = mapObjects.addPlacemark(resultLocation, resourceBackedImage);
                    placemark.addTapListener(this);
                }
            }

            break;
        }
    }

    @Override
    public void onSearchError(@NonNull Error error) {

    }

    private final CameraListener cameraListener = new CameraListener() {
        @Override
        public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateSource cameraUpdateSource, boolean b) {

        }
    };

    @Override
    public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
        return false;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }
}
