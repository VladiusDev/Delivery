package com.shels.delivery.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.shels.delivery.Activity.DocumentActivity;
import com.shels.delivery.Adapters.DocumentsAdapter;
import com.shels.delivery.AuthorizationUtils;
import com.shels.delivery.BarcodeScanner.BarcodeScannerUtils;
import com.shels.delivery.Data.Barcode;
import com.shels.delivery.Data.DeliveryAct;
import com.shels.delivery.Data.Product;
import com.shels.delivery.DataBaseUtils.ViewModel.BarcodeViewModel;
import com.shels.delivery.DataBaseUtils.ViewModel.DeliveryActsViewModel;
import com.shels.delivery.DataBaseUtils.ViewModel.ProductViewModel;
import com.shels.delivery.R;
import com.shels.delivery.Retrofit.ApiFactory;
import com.shels.delivery.Retrofit.ApiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

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
    private ConstraintLayout constraintLayout;
    private ConstraintLayout linearLayoutEmpty;
    private ImageView imageEmptyState;
    private TextView tvEmptyState;

    private final static int DELIVERY_ACT_SAVE_CODE = 1;

    public DocumentsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_documents, container, false);

        activity = this.getActivity();

        deliveryActsViewModel = new ViewModelProvider(this).get(DeliveryActsViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        barcodeViewModel = new ViewModelProvider(this).get(BarcodeViewModel.class);

        linearLayoutEmpty = view.findViewById(R.id.documents_empty);
        constraintLayout = view.findViewById(R.id.documents_constraintLayout);
        timeTextView = view.findViewById(R.id.documents_time);
        completedTextView = view.findViewById(R.id.documents_completed);
        searchView = activity.findViewById(R.id.main_searchView);
        imageEmptyState = view.findViewById(R.id.documents_image_imptyState);
        tvEmptyState = view.findViewById(R.id.documents_tv_emptyState);

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
                startActivityForResult(intent, DELIVERY_ACT_SAVE_CODE);
            }
        });

        recyclerView = view.findViewById(R.id.documents_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        downloadDataToDB();

        getDataFromDB();
    }

    @SuppressLint("CheckResult")
    private void downloadDataToDB(){
        if (AuthorizationUtils.hasConnection(getContext())) {
            swipeRefreshLayout.setRefreshing(true);

            // Get data from 1C API
            ApiFactory apiFactory = ApiFactory.getInstance(getContext());
            ApiService apiService = apiFactory.getApiService();
            apiService.getDeliveryActs()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<DeliveryAct>>() {
                        @Override
                        public void accept(List<DeliveryAct> deliveryActs) throws Exception {
                            // Delivery acts
                            deliveryActsViewModel.insertDeliveryActs(deliveryActs);
                            for (DeliveryAct deliveryAct : deliveryActs) {
                                // Products
                                List<Product> products = deliveryAct.getProducts();
                                productViewModel.insertProducts(products);

                                for (Product product : products) {
                                    // Barcodes
                                    List<Barcode> barcodes = product.getBarcodes();
                                    barcodeViewModel.insertBarcodes(barcodes);
                                }
                            }

                            swipeRefreshLayout.setRefreshing(false);
                            showEmptyState();
                        }
                    }, new Consumer<Throwable>() {
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
                                        swipeRefreshLayout.setRefreshing(false);
                                        Snackbar.make(constraintLayout, exception.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            } else {
                                swipeRefreshLayout.setRefreshing(false);
                                Snackbar.make(constraintLayout, throwable.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }else{
            swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(constraintLayout, getResources().getString(R.string.check_hasConnection), Snackbar.LENGTH_LONG).show();
        }

    }

    private void getDataFromDB(){
        LiveData<List<DeliveryAct>> deliveryActsFromDB = deliveryActsViewModel.getDeliveryActs();
        deliveryActsFromDB.observe(getViewLifecycleOwner(), new Observer<List<DeliveryAct>>() {
            @Override
            public void onChanged(List<DeliveryAct> deliveryActs) {
                adapter.setDocuments(deliveryActs);

                updateQuantityTasks();

                showEmptyState();
            }
        });
    }

    private void updateQuantityTasks(){
        int completedTasks = deliveryActsViewModel.getCompletedDeliveryActs();
        int quantityTasks = deliveryActsViewModel.getDeliveryActs().getValue().size();

        completedTextView.setText(completedTasks + "/" + quantityTasks);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == DELIVERY_ACT_SAVE_CODE && resultCode == DELIVERY_ACT_SAVE_CODE){
            Snackbar snackbar = Snackbar
                    .make(constraintLayout, getResources().getString(R.string.document_saved), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void showEmptyState(){
        if (adapter.getItemCount() > 0 ) {
            imageEmptyState.setVisibility(View.INVISIBLE);
            tvEmptyState.setVisibility(View.INVISIBLE);
            floatingActionButton.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }else{
            imageEmptyState.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.VISIBLE);
            floatingActionButton.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }

}
