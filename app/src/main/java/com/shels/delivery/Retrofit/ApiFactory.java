package com.shels.delivery.Retrofit;

import android.content.Context;

import com.shels.delivery.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory{
    private static ApiFactory apiFactory;
    private static Retrofit retrofit;

    private ApiFactory (Context context){
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                // Get auth data from preference
                String token = "Basic " + Constants.TOKEN;

                Request request = chain.request();
                Request.Builder builder = request.newBuilder().header("Authorization", token);
                Request newRequest = builder.build();

                return chain.proceed(newRequest);
            }
        }).build();

        retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .baseUrl(Constants.SERVER_URL).build();
    }

    public static ApiFactory getInstance(Context context){
        if (apiFactory == null){
            apiFactory = new ApiFactory(context);
        }

        return apiFactory;
    }

    public ApiService getApiService(){
        return retrofit.create(ApiService.class);
    }

}
