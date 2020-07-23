package com.shels.delivery.Retrofit;

import com.shels.delivery.Data.DeliveryAct;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @GET("getUserInfo")
    Observable<UserInfo> getUserInfo(@Header("Token") String token);

    @GET("getDeliveryActs")
    Observable<List<DeliveryAct>> getDeliveryActs();

    @POST("saveDocument")
    @Headers("Content-Type:application/json")
    Observable<PostRequest> saveDocument(@Body String data);

}
