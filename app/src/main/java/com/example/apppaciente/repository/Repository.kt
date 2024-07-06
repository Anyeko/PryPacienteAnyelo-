// Repository.kt
package com.example.apppaciente.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.apppaciente.api.ApiClient
import com.example.apppaciente.model.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONObject

class Repository {
    fun login(email: String, clave: String): LiveData<LoginResponse?> {
        val loginResponse = MutableLiveData<LoginResponse?>()

        ApiClient.apiService.login(email, clave).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    loginResponse.value = response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = errorBody?.let {
                        try {
                            val json = JSONObject(it)
                            json.getString("message")
                        } catch (e: Exception) {
                            "Unknown error"
                        }
                    } ?: "Unknown error"

                    val errorResponse = LoginResponse(false, null, message)
                    loginResponse.value = errorResponse
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                val errorResponse = LoginResponse(false, null, t.message ?: "Unknown error")
                loginResponse.value = errorResponse
            }
        })

        return loginResponse
    }
}
