package com.shels.delivery.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.shels.delivery.AuthorizationUtils;
import com.shels.delivery.Constants;
import com.shels.delivery.Data.DeliveryAct;
import com.shels.delivery.DataBaseUtils.ViewModel.BarcodeViewModel;
import com.shels.delivery.DataBaseUtils.ViewModel.DeliveryActsViewModel;
import com.shels.delivery.DataBaseUtils.ViewModel.ProductViewModel;
import com.shels.delivery.Fragments.ClientsMapFragment;
import com.shels.delivery.Fragments.DocumentsFragment;
import com.shels.delivery.R;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.search.SearchFactory;

public class MainActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private SearchView searchView;
    private Button buttonExit;
    private Context context;
    private Activity activity;
    private SharedPreferences preferences;
    private DeliveryActsViewModel deliveryActsViewModel;
    private ProductViewModel productViewModel;
    private BarcodeViewModel barcodeViewModel;
    private ConstraintLayout constraintLayout;
    private DocumentsFragment documentsFragment;
    private ClientsMapFragment clientsMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            documentsFragment = (DocumentsFragment) getSupportFragmentManager().getFragment(savedInstanceState, "documentsFragment");
            clientsMapFragment = (ClientsMapFragment) getSupportFragmentManager().getFragment(savedInstanceState, "clientsMapFragment");
        }else{
            documentsFragment = new DocumentsFragment();
            clientsMapFragment = new ClientsMapFragment();

            MapKitFactory.setApiKey(Constants.YANDEX_MAPS_API_KEY);
            MapKitFactory.initialize(this);
            SearchFactory.initialize(this);
        }

        context = this;
        activity = this;

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        deliveryActsViewModel = ViewModelProviders.of(this).get(DeliveryActsViewModel.class);
        productViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        barcodeViewModel = ViewModelProviders.of(this).get(BarcodeViewModel.class);

        constraintLayout = findViewById(R.id.main_constraintLayout);

        buttonExit = findViewById(R.id.main_button_exit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(getResources().getString(R.string.exit_app));
                alertDialogBuilder.setNegativeButton(getResources().getString(R.string.no),null);
                alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AuthorizationUtils.clearAuthorization(context, activity);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        searchView = findViewById(R.id.main_searchView);
        searchView.setQueryHint(getResources().getString(R.string.tasks_search));

        actionBar = getSupportActionBar();
        actionBar.hide();

        TabLayout tabLayout = findViewById(R.id.main_TabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tasks)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.clients_map)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.main_pager);
        final MainPagerAdapter adapter = new MainPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                int tabPosition = tab.getPosition();
                switch (tabPosition){
                    case 0:
                        searchView.setQueryHint(getResources().getString(R.string.tasks_search));
                        break;
                    case 1:
                        searchView.setQueryHint(getResources().getString(R.string.search_map));
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {

            } else {
                String barcode = intentResult.getContents();
                if (!barcode.isEmpty() && barcode != null){
                    // Нашли документ, открываем
                    DeliveryAct deliveryAct = deliveryActsViewModel.getDeliveryActByBarcode(barcode);
                    if (deliveryAct != null) {
                        Intent intent = new Intent(activity, DocumentActivity.class);
                        intent.putExtra("deliveryActId", deliveryAct.getId());
                        startActivity(intent);
                    }else{
                        Snackbar.make(constraintLayout, getResources().getString(R.string.document_not_found), Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "documentsFragment", documentsFragment);
        getSupportFragmentManager().putFragment(outState, "clientsMapFragment", clientsMapFragment);
    }

    public class MainPagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public MainPagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return documentsFragment;
                case 1:
                    return clientsMapFragment;
                default:
                    return null;
            }
        }
        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
