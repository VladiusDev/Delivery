package com.shels.delivery.Data.DataUtils;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataTypeConverter{

    @TypeConverter
    public static String fromDocumentsPhotos(List<String> documentsPhotos) {
        if (documentsPhotos == null) {
            return "";
        }else{
            return documentsPhotos.stream().collect(Collectors.joining(","));
        }
    }

    @TypeConverter
    public static List<String> toDocumentsPhotos(String data) {
        if (data.isEmpty()) {
            return new ArrayList<>();
        }else{
            return Arrays.asList(data.split(","));
        }
    }

}
