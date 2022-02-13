package com.example.fitme.core.network

import com.example.fitme.core.network.result.Resource
import com.example.fitme.core.utils.Log
import org.json.JSONObject
import retrofit2.Response

abstract class BaseDataSource {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {

        try {
            val response = call()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) return Resource.success(body)
            } else {

                var message: String? = "BaseDataSource: Unknown error"

                try {
                    val errorMessage: String = response.errorBody()?.string() ?: ""
                    val json = JSONObject(errorMessage)

                    message = json.getString("message")

                } catch (e: Exception) {}

                return Resource.error(message, response.body(), response.code())
            }
        } catch (e: Exception) {
            Log.d(e.message)
            return Resource.error(e.message ?: e.toString(), null, 429)
        }

        return Resource.error(null, null, 429)
    }
}