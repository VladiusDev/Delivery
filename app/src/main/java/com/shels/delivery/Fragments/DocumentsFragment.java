package com.shels.delivery.Fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shels.delivery.Activity.DocumentActivity;
import com.shels.delivery.Adapters.DocumentsAdapter;
import com.shels.delivery.AuthorizationUtils;
import com.shels.delivery.BarcodeScanner.BarcodeScannerUtils;
import com.shels.delivery.Constants;
import com.shels.delivery.Data.Barcode;
import com.shels.delivery.Data.DeliveryAct;
import com.shels.delivery.Data.Product;
import com.shels.delivery.DataBaseUtils.ViewModel.BarcodeViewModel;
import com.shels.delivery.DataBaseUtils.ViewModel.DeliveryActsViewModel;
import com.shels.delivery.DataBaseUtils.ViewModel.ProductViewModel;
import com.shels.delivery.JsonUtils.JsonParser1C;
import com.shels.delivery.R;
import com.shels.delivery.WebService.WebService1C;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DocumentsFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private DocumentsAdapter adapter;
    private final ArrayList<DeliveryAct> deliveryActs = new ArrayList<>();
    private DeliveryActsViewModel deliveryActsViewModel;
    private ProductViewModel productViewModel;
    private BarcodeViewModel barcodeViewModel;
    private TextView timeTextView;
    private TextView completedTextView;
    private Activity activity;
    private SearchView searchView;
    private FloatingActionButton floatingActionButton;

    public DocumentsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_documents, container, false);

        activity = this.getActivity();

        deliveryActsViewModel = ViewModelProviders.of(this).get(DeliveryActsViewModel.class);
        productViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        barcodeViewModel = ViewModelProviders.of(this).get(BarcodeViewModel.class);

        timeTextView = view.findViewById(R.id.documents_time);
        completedTextView = view.findViewById(R.id.documents_completed);
        searchView = activity.findViewById(R.id.main_searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<DeliveryAct> found;

                if (newText.isEmpty()) {
                    found = deliveryActsViewModel.getDeliveryActs().getValue();
                }else{
                    found = deliveryActsViewModel.getDeliveryActsByFilter(newText);
                }

                adapter.setDocuments(found);

                return false;
            }
        });

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");

        String stringDate = simpleDateFormat.format(date);

        timeTextView.setText(stringDate);

        floatingActionButton = view.findViewById(R.id.documents_floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarcodeScannerUtils.scanBarcode(activity);
            }
        });

        swipeRefreshLayout = view.findViewById(R.id.documents_swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadDataToDB();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        adapter = new DocumentsAdapter(deliveryActs);
        adapter.setOnDocumentClickListener(new DocumentsAdapter.OnDocumentClickListener() {
            @Override
            public void onDocumentClick(int position) {
                DeliveryAct deliveryAct = adapter.getDocuments().get(position);

                Intent intent = new Intent(activity, DocumentActivity.class);
                intent.putExtra("deliveryActId", deliveryAct.getId());
                startActivity(intent);
            }
        });

        recyclerView = view.findViewById(R.id.documents_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        downloadDataToDB();

        getData();

        return view;
    }

    private void downloadDataToDB(){
        new DownloadDataToDbTask().execute();
    }

    private class DownloadDataToDbTask extends AsyncTask<Void, Void, ContentValues> {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(ContentValues jsonResult) {
            // Парсим JSON результат
            if ((Boolean) jsonResult.get(Constants.WEB_SERVICE_AUTH) == true) {
                String jsonData = jsonResult.get(Constants.WEB_SERVICE_RESULT).toString();

                HashMap result = JsonParser1C.getDeliveryActs(jsonData);

                // Заносим данные в БД
                ArrayList<DeliveryAct> deliveryActs = (ArrayList<DeliveryAct>) result.get(Constants.JSON_FIELD_DELIVERY_ACTS);
                ArrayList<Product> goods = (ArrayList<Product>) result.get(Constants.JSON_FIELD_GOODS);
                ArrayList<Barcode> barсodes = (ArrayList<Barcode>) result.get(Constants.JSON_FIELD_DELIVERY_BARCODES);

                deliveryActsViewModel.insertDeliveryActs(deliveryActs);
                productViewModel.insertProducts(goods);
                barcodeViewModel.insertBarcodes(barсodes);
            }else{
                Toast.makeText(getContext(), getResources().getString(R.string.check_error_authorization), Toast.LENGTH_LONG).show();

                AuthorizationUtils.clearAuthorization(getContext(), getActivity());
            }

            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected ContentValues doInBackground(Void... voids) {
            return WebService1C.sendRequest(Constants.SOAP_METHOD_GET_DELIVERY_ACTS, getContext());
        }
    }

    private void getData(){
        LiveData<List<DeliveryAct>> deliveryActsFromDB = deliveryActsViewModel.getDeliveryActs();
        deliveryActsFromDB.observe(this, new Observer<List<DeliveryAct>>() {
            @Override
            public void onChanged(List<DeliveryAct> deliveryActs) {
                adapter.setDocuments(deliveryActs);

                updateQuantityTasks();
            }
        });
    }

    private void updateQuantityTasks(){
        int completedTasks = deliveryActsViewModel.getCompletedDeliveryActs();
        int quantityTasks = deliveryActsViewModel.getDeliveryActs().getValue().size();

        completedTextView.setText(completedTasks + "/" + quantityTasks);
    }

}
