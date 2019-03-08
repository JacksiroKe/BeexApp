package com.jackson_siro.beexpress.retrofitconfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
//import com.jackson_siro.beexpress.ui.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CallJson {
    public static final boolean DEBUG = Boolean.parseBoolean("true");

    public static API callJson(){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(11, TimeUnit.SECONDS);
        builder.writeTimeout(13, TimeUnit.SECONDS);
        builder.readTimeout(18, TimeUnit.SECONDS);

        if (DEBUG){
            builder.addInterceptor(loggingInterceptor);
        }

        builder.cache(null);
        OkHttpClient okHttpClient = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrlConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit.create(API.class);
    }
}
