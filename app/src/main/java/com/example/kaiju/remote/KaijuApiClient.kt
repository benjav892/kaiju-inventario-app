package com.example.kaiju.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object KaijuApiClient {

    // Cambiar IP si se reinicia la instancia EC2 y cambia su IP pública
    private const val BASE_URL = "http://35.173.204.211:8080/api/"

    // Token JWT en memoria durante la sesión activa
    @Volatile
    var tokenJwt: String? = null

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = tokenJwt
        val request = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        chain.proceed(request)
    }

    val api: KaijuApiService by lazy {
        val clienteHttp = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .callTimeout(20, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KaijuApiService::class.java)
    }
}