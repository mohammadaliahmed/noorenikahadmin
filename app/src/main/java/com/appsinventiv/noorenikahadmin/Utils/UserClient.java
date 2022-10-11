package com.appsinventiv.noorenikahadmin.Utils;


import com.appsinventiv.noorenikahadmin.Models.SendNotificationModel;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserClient {

    @POST("api/uploadFile")
    @Multipart
    Call<ResponseBody> uploadFile(
            @Part MultipartBody.Part file, @Part("photo") RequestBody name

    );


    @Headers("Content-Type: application/json")
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(
            @Body SendNotificationModel jsonObject,
            @Header("Authorization") String authHeader

    );


}
