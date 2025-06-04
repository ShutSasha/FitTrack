package com.example.mobile.data.api

import com.example.mobile.data.dto.role.ChangeRoleDto
import com.example.mobile.domain.model.Role
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface RoleApi {
    @GET("roles")
    fun fetchroles(
        @Header("accessToken") accessToken: String?
    ): Call<List<Role>>

    @POST("roles/change-user-role")
    fun changeRole(
        @Header("accessToken") accessToken: String?,
        @Body changeRoleDto: ChangeRoleDto,
    ): Call<ResponseBody>
}
