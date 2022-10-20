package com.appsinventiv.noorenikahadmin.Utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppConfig {

    public static String BASE_URL = "https://fcm.googleapis.com/";
    public static String key = "key=AAAA91yjyaU:APA91bFM4SHeu_4MCchQSW19DODeaHGCyZP2fPw7vPgNYnEu020By2uAMuXFUDRagCHOs86VZWRfI6ZjtPyrYJu-vNeuEHPDlNmZrQpQuSlTvg3TU8CZ2655G3fwu7bCPBO8OVUunDxn";

    static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .build();

    public static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

    }

}
