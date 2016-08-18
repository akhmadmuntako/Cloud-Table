package com.example.cloudtable;

import com.example.cloudtable.Database.generator.Tables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Lenovo on 08/08/2016.
 */
public interface ApiInterface {
    @FormUrlEncoded
    @POST("mydevice")
    Call<ApiResponse> getResponse(@Field("device_id") String device_id);

    @FormUrlEncoded
    @POST("choose_table")
    Call<Tables> selectTable(@Field("table_id") int table_id);

    @GET("current_table:9000")
    Call<ApiResponse> tables();

    class Interface {
        private static ApiInterface service;
        //set server url
        private static String BASE_URL = "http://192.168.2.6/";

        public static ApiInterface buildRetrofitService() {

            if (service == null) {
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                service = retrofit.create(ApiInterface.class);
                return service;
            } else {
                return service;
            }
        }
    }
}
