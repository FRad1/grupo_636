package com.example.detectar_luz;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface JasonPlaceHolderApi {

    @POST("login")
    Call<Post> createPostLogin(@Body Post post);

    @POST("register")
    Call<Post> createPostRegister(@Body Post post);

    //@POST("event")
    //Call<Event> createEvent(@Header("\"token\"") String header , @Body Event event);
    //Call<Event> createEvent(@Header("token") String header , @Body Event event);

    @POST("event")
    @FormUrlEncoded
    Call<Event> createEvent(@Header("token") String token,
                             @Field("env") String env,
                            @Field("type_events") String typeEvents,
                            @Field("state") String state,
                            @Field("description")String description);


}
