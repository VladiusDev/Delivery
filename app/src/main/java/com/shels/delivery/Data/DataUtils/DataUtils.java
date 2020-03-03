package com.shels.delivery.Data.DataUtils;

import android.content.Context;

import com.shels.delivery.R;

public class DataUtils {

    public static String getDocumentNameById(Context context, int id){
        switch (id){
            case 1:
                return context.getResources().getString(R.string.selling);
            case 2:
                return context.getResources().getString(R.string.removalOfComplaints);
            case 3:
                return context.getResources().getString(R.string.complaintDelivery);
            case 4:
                return context.getResources().getString(R.string.moving);
            default:
                return "";
        }
    }

}
