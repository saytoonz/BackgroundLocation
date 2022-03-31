package com.sayt.background_location.api

import android.content.Context
import com.sayt.background_location.data.LocationResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface ApiClient {

    @GET("updateLocation.json")
     fun updateLocation() : Call<LocationResponse>

    companion object {

        var retrofit: Retrofit? = null
        val logging = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        fun getInstance(context: Context, postUrl: String) : Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(postUrl)
                    .client(OkHttpClient.Builder().addInterceptor(logging).build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }
    }
}