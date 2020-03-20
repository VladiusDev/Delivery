package com.shels.delivery.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.shels.delivery.Adapters.DocumentPagerAdapter;
import com.shels.delivery.Data.DataUtils.DataUtils;
import com.shels.delivery.Data.DeliveryAct;
import com.shels.delivery.DataBaseUtils.ViewModel.DeliveryActsViewModel;
import com.shels.delivery.R;

public class DocumentActivity extends AppCompatActivity {
    private String documentId;
    private DeliveryActsViewModel deliveryActsViewModel;
    private DeliveryAct deliveryAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        deliveryActsViewModel = ViewModelProviders.of(this).get(DeliveryActsViewModel.class);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent.hasExtra("deliveryActId")){
            documentId = intent.getStringExtra("deliveryActId");
        }

        if (documentId != null){
            if (deliveryAct == null) {
                deliveryAct = deliveryActsViewModel.getDeliveryActById(documentId);
            }

            if (deliveryAct != null){
                String documentType = DataUtils.getDocumentNameById(this, deliveryAct.getType());

                actionBar.setTitle(documentType);
            }
        }

        TabLayout tabLayout = findViewById(R.id.document_TabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.document_tab_client)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.document_tab_goods)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.document_tab_documents)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.document_pager);
        final DocumentPagerAdapter adapter = new DocumentPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), documentId);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), true);
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
    public void onBackPressed() {
        super.onBackPressed();
    }

     @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                onBackPressed();

                return true;
        }

        return false;
    }

}
