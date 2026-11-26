package com.example.kaiju.remote

import com.example.kaiju.model.AuthResponse
import com.example.kaiju.model.LoginRequest
import com.example.kaiju.model.UsuarioCreateRequest
import com.example.kaiju.model.UsuarioResponse
import com.example.kaiju.model.UsuarioUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface KaijuApiService {

    @POST("v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("v1/usuarios")
    suspend fun listarUsuarios(): List<UsuarioResponse>

    @POST("v1/usuarios")
    suspend fun crearUsuario(@Body request: UsuarioCreateRequest): UsuarioResponse

    @PUT("v1/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Long,
        @Body request: UsuarioUpdateRequest
    ): UsuarioResponse

    @DELETE("v1/usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Long): Response<Unit>
}