package com.example.emergencyapp.network;

import com.example.emergencyapp.models.AlerteApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("posts")
    Call<AlerteApi> envoyerAlerte(@Body AlerteApi alerte);

    @GET("posts")
    Call<List<AlerteApi>> getAlertes();
}
