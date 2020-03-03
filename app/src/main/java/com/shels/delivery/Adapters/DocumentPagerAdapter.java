package com.shels.delivery.Adapters;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.shels.delivery.Fragments.DocumentClientFragment;
import com.shels.delivery.Fragments.DocumentGoodsFragment;
import com.shels.delivery.Fragments.DocumentPhotoFragment;

public class DocumentPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String documentId;

    public DocumentPagerAdapter(FragmentManager fm, int NumOfTabs, String documentId) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.documentId = documentId;
    }
    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        args.putString("documentId", documentId);

        switch (position) {
            case 0:
                DocumentClientFragment documentClientFragment = new DocumentClientFragment();
                documentClientFragment.setArguments(args);

                return documentClientFragment;
            case 1:
                DocumentGoodsFragment documentGoodsFragment = new DocumentGoodsFragment();
                documentGoodsFragment.setArguments(args);

                return documentGoodsFragment;
            case 2:

                DocumentPhotoFragment documentPhotoFragment = new DocumentPhotoFragment();
                documentPhotoFragment.setArguments(args);

                return documentPhotoFragment;
            default:

            return null;
        }
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
