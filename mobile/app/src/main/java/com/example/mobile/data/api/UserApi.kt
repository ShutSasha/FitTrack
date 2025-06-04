package com.example.mobile.data.api

import com.example.mobile.data.dto.user.UserSearchResponse
import com.example.mobile.domain.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    @GET("users/search")
    fun searchUsers(
        @Query("query") query: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<UserSearchResponse>

    @GET("users/{id}")
    fun getUserById(@Path("id") id: String): Call<User>

}
