package com.shels.delivery.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shels.delivery.Adapters.ClientsAdapter;
import com.shels.delivery.Data.DeliveryAct;
import com.shels.delivery.DataBaseUtils.ViewModel.DeliveryActsViewModel;
import com.shels.delivery.R;

import java.util.ArrayList;
import java.util.List;


public class ClientsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ClientsAdapter adapter;
    private DeliveryActsViewModel viewModel;
    private final List<DeliveryAct> deliveryActs = new ArrayList<>();

    public ClientsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clients, container, false);

        viewModel = ViewModelProviders.of(this).get(DeliveryActsViewModel.class);

        adapter = new ClientsAdapter(deliveryActs);

        recyclerView = view.findViewById(R.id.clients_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        getData();

        return view;
    }

    private void getData(){
        LiveData<List<DeliveryAct>> deliveryActsFromDB = viewModel.getDeliveryActs();
        deliveryActsFromDB.observe(this, new Observer<List<DeliveryAct>>() {
            @Override
            public void onChanged(List<DeliveryAct> deliveryActs) {
                adapter.setDeliveryActs(deliveryActs);
            }
        });
    }

}
