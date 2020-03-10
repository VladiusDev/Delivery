package com.shels.delivery.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.shels.delivery.Adapters.ProductsAdapter;
import com.shels.delivery.BarcodeScanner.BarcodeScannerUtils;
import com.shels.delivery.Data.Barcode;
import com.shels.delivery.Data.Product;
import com.shels.delivery.DataBaseUtils.ViewModel.BarcodeViewModel;
import com.shels.delivery.DataBaseUtils.ViewModel.ProductViewModel;
import com.shels.delivery.R;

import java.util.ArrayList;
import java.util.List;


public class DocumentGoodsFragment extends Fragment {
    private String documentId;
    private RecyclerView recyclerView;
    private ProductViewModel productViewModel;
    private BarcodeViewModel barcodeViewModel;
    private ProductsAdapter productsAdapter;
    private final List<Product> products = new ArrayList<>();
    private ArrayList<String> barcodes;
    private Fragment fragment;
    private FloatingActionButton floatingActionButton;

    public DocumentGoodsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document_goods, container, false);

        fragment = this;

        documentId = getArguments().getString("documentId");

        productViewModel      = ViewModelProviders.of(this).get(ProductViewModel.class);
        barcodeViewModel      = ViewModelProviders.of(this).get(BarcodeViewModel.class);

        floatingActionButton = view.findViewById(R.id.document_floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarcodeScannerUtils.scanBarcode(getActivity(), fragment);
            }
        });

        recyclerView = view.findViewById(R.id.document_recyclerView);
        productsAdapter = new ProductsAdapter(products);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(productsAdapter);

        setHasOptionsMenu(true);

        getData();

        return view;
    }

    private void getData(){
        LiveData<List<Product>> productsFromDB = productViewModel.getProductsByDocumentId(documentId);
        productsFromDB.observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                // Получим штрих-кода по всем товарам
                barcodes = new ArrayList<>();

                for (Product product : products){
                    List<Barcode> productBarcodes = barcodeViewModel.getBarcodesByProductId(product.getProductId());

                    for (Barcode barcode : productBarcodes){
                        barcodes.add(barcode.getBarcode());
                    }
                }

                // Обновим список товаров
                productsAdapter.setProducts(products);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() != null) {
                // Найдем полученный штрих-код
                String barcode = intentResult.getContents();

                if (barcode != null && !barcode.isEmpty()){
                    if (barcodes.contains(barcode)) {
                        Barcode productBarcode = barcodeViewModel.getBarcode(barcode);
                        Product product = productViewModel.getProductByProductDocumentId(productBarcode.getProductId(), documentId);
                        product.setScanned(product.getScanned() + 1);

                        productViewModel.UpdateProductTask(product);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        alertDialogBuilder.setTitle(getResources().getString(R.string.product_scan_successfully));
                        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BarcodeScannerUtils.scanBarcode(getActivity(), fragment);
                            }
                        });
                        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.no), null);

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }else{
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        alertDialogBuilder.setMessage(getResources().getString(R.string.scan_again));
                        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BarcodeScannerUtils.scanBarcode(getActivity(), fragment);
                            }
                        });
                        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.no), null);

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
