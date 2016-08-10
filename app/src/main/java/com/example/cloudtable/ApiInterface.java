package com.example.cloudtable;

import com.example.cloudtable.Model.Table;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Lenovo on 08/08/2016.
 */
public interface ApiInterface {
    @POST("{mydevice}")
    Call<ApiResponse> tables(@Path("mydevice") int device_id);

    @POST("{choose_table}")
    Call<Table> selectTable(@Path("choose_table") int table_id);

    @GET("current_table")
    Call<ApiResponse> tables();

    class Interface {

        private static ApiInterface service;

        public static ApiInterface buildRetrofitService() {

            if (service == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://192.168.1.105/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build();
                service = retrofit.create(ApiInterface.class);
                return service;
            } else {
                return service;
            }
        }
    }
}
