package com.example.myapplication

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url


interface ApiInterface {

    @GET
    fun getImage(@Url url: String): Call<ResponseBody>


}





