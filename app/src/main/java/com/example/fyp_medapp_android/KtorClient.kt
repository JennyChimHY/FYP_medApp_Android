package com.example.fyp_medapp_android

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.Date

@Serializable
data class HttpBinResponse( //Item
    val args: Map<String, String>,
    val data: String,
    val files: Map<String, String>,
    val form: Map<String, String>,
    val headers: Map<String, String>,
    val json: String?,
    val origin: String,
    val url: String,
)

@Serializable
data class HttpLogResponse( //Login
//    val _id: String?,
    val userID: String?,
    val firstName: String?, //200 success
    val lastName: String?,
    val gender: String?,
    val age: Int?,
    val dob: String?,
    var username: String?,
    val email: String?,
    var password: String?,
    var userRole: String?,
    var patientConnection: Array<String>?,
    var resultCode: String?
)
object KtorClient {
    private var token: String = ""

    var apiDomain =  "https://rnlss-158-182-199-233.a.free.pinggy.online";
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
                explicitNulls = false
            }

            ) // enable the client to perform JSON serialization
        }
        install(Logging)
        defaultRequest {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            header("Authorization", token)
        }
        expectSuccess = true
    }

    suspend fun getUserProfile(): String { //for testing
        Log.d("enter getUserProfile", "getUserProfile: ")
        val response: String = httpClient.get(apiDomain + "/user").body() //.toString() vs .body()

        Log.d("KtorCLient getUserProfile", response)

        return response.toString()
    }

    suspend fun postLogin(login: Info): User { //Login function, post the info to backend to authorize
        Log.d("Enter postLogin", login.toString())
        val response: HttpLogResponse =
            httpClient.post(apiDomain + "/login") {
                setBody(login) //data class is fine in converting to json
            }.body()

//        token = response.token ?: "" //TODO: add error code

        var user = User(response.userID, response.firstName, response.lastName, response.gender, response.age, response.dob, response.username, response.email, response.password, response.userRole, response.patientConnection, response.resultCode )
        return user
    }
}




