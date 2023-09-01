package com.niveon.tugasakhir.api

import com.google.gson.JsonObject
import com.niveon.tugasakhir.model.LoginRequest
import com.niveon.tugasakhir.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("user")
    suspend fun getUser(@Header("Authorization") token: String): Response<JsonObject>

    @GET("dosens")
    suspend fun getDosen(): Response<JsonObject>

    @GET("mahasiswas")
    suspend fun getMahasiswa(): Response<JsonObject>

    @POST("updatenfcdata")
    suspend fun updatenfcdata(@Body requestBody: JsonObject): Response<JsonObject>

    @POST("jadwalmahasiswas")
    suspend fun getJadwalmahasiswas(@Body requestBody: JsonObject): Response<JsonObject>

    @POST("jadwaldosens")
    suspend fun getJadwaldosens(@Body requestBody: JsonObject): Response<JsonObject>

    @POST("pertemuanberlangsung")
    suspend fun getPertemuanBerlangsung(@Body requestBody: JsonObject): Response<JsonObject>

    @POST("pertemuandosenberlangsung")
    suspend fun getPertemuanDosenBerlangsung(@Body requestBody: JsonObject): Response<JsonObject>

    @POST("pertemuanharian")
    suspend fun getPertemuanHarian(@Body requestBody: JsonObject): Response<JsonObject>

    @POST("pertemuandetail")
    suspend fun getPertemuanDetail(@Body requestBody: JsonObject): Response<JsonObject>

    @POST("pertemuandosendetail")
    suspend fun getPertemuanDosenDetail(@Body requestBody: JsonObject): Response<JsonObject>

    @POST("pertemuanriwayat")
    suspend fun getPertemuanRiwayat(@Body requestBody: JsonObject): Response<JsonObject>

    @POST("absenmahasiswa")
    suspend fun absenmahasiswa(@Body requestBody: JsonObject): Response<JsonObject>

    @POST("profilemahasiswa")
    suspend fun getProfileMahasiswa(@Body requestBody: JsonObject): Response<JsonObject>

}