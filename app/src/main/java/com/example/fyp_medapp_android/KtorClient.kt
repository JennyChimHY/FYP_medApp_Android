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
data class HttpBinResponse(
    //Item
    val args: Map<String, String>,
    val data: String,
    val files: Map<String, String>,
    val form: Map<String, String>,
    val headers: Map<String, String>,
    val json: String?,
    val origin: String,
    val url: String,
)

var apiDomain = "https://rnjzl-158-182-188-100.a.free.pinggy.online"
object KtorClient {
    private var token: String = ""

//    var apiDomain = "http://rnves-158-182-199-249.a.free.pinggy.online";
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

//    suspend fun getUserProfile(): String { //for testing
//        Log.d("enter getUserProfile", "getUserProfile: ")
//        val response: String = httpClient.get(apiDomain + "/user").body() //.toString() vs .body()
//
//        Log.d("KtorCLient getUserProfile", response)
//
//        return response.toString()
//    }

    suspend fun postLogin(login: Info): User { //Login function, post the info to backend to authorize
        Log.d("Enter postLogin", login.toString())
        val user: User =
            httpClient.post(apiDomain + "/login") {
                setBody(login) //data class is fine in converting to json
            }.body()

        return user
    }

    suspend fun getMedicine(userID: String?): List<Medicine> { //Login function, post the info to backend to authorize
        Log.d("Enter getMedicine", "getMedicine:$userID ")

        try {
            val medicine: List<Medicine> =
                httpClient.get(apiDomain + "/medicineRecord/$userID")
                    .body() //.toString() vs .body()

            Log.d("KtorClient getMedicine", medicine.toString())

            return medicine
        } catch (e: Exception) {  //catch 404 error from backend
            Log.d("KtorClient getMedicine", e.toString())

            //TODO check not null
            return emptyList()
        }
    }

    suspend fun getAppointment(userID: String?): List<Appointment> { //Login function, post the info to backend to authorize
        Log.d("Enter getAppointment", "getAppointment:$userID ")

        try {
            val appointment: List<Appointment> =
                httpClient.get(apiDomain + "/appointmentRecord/$userID")
                    .body()

            return appointment
        } catch (e: Exception) {
            Log.d("KtorClient getAppointment", e.toString())

            //TODO check not null
            return emptyList()
        }
    }
}




