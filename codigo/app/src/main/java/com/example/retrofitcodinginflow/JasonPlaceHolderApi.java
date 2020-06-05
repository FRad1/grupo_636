package com.example.retrofitcodinginflow;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface JasonPlaceHolderApi {

    @POST("login")
    Call<Post> createPostLogin(@Body Post post);

    @POST("register")
    Call<Post> createPostRegister(@Body Post post);

    @POST("event")
    Call<Event> createEvent(@Header("\"token\"") String header , @Body Event event);



}
