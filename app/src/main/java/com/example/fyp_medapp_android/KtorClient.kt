package com.example.fyp_medapp_android

import android.util.Log
import com.auth0.android.jwt.JWT
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


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

@Serializable
data class LoginResult(
    val resultCode: String,
    val token: String
)

var apiDomain = "https://rnzcw-158-182-196-20.a.free.pinggy.link"

object KtorClient {
    var token: String = ""

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
            header("Authorization", "Bearer ${token}")  //token for authorization in fetching api
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
        val response: LoginResult =
            httpClient.post(apiDomain + "/login") {
                setBody(login) //data class is fine in converting to json
            }.body()

        if(response.resultCode == "200") {

            token = response.token //for header in other api
            val jwt = JWT(response.token)
//            val issuer = jwt.issuer //get registered claims
//            val isExpired = jwt.isExpired(10) // Do time validation with 10 seconds leeway

            Log.d("JWT token getClaim", "id: ${jwt.getClaim("_id").asString()}")
            Log.d("JWT token", "id: ${jwt.getClaim("_id").asString()}")
            val _id = jwt.getClaim("_id").asString() //get custom claims
            val userID = jwt.getClaim("userID").asString()
            val firstName = jwt.getClaim("firstName").asString()
            val lastName = jwt.getClaim("lastName").asString()
            val gender = jwt.getClaim("gender").asString()
            val age = jwt.getClaim("age").asInt()
            val dob = jwt.getClaim("dob").asString()
            val username = jwt.getClaim("username").asString()
            val email = jwt.getClaim("email").asString()
            val password = jwt.getClaim("password").asString()
            val userRole = jwt.getClaim("userRole").asString()
            val patientConnection = jwt.getClaim("patientConnection").asArray(String::class.java)

            val user = User(jwt, _id, userID, firstName, lastName, gender, age, dob, username, email,
                password, userRole, patientConnection)
            println("return user")
            return user
        } else {
            println("return null user")
            return User(null, null, null, null, null, null, null, null, null,
                null, null, null, null)
        }
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

            //null or errors
            return emptyList()
        }
    }

    suspend fun getHealthData(userID: String?): List<HealthData> { //Login function, post the info to backend to authorize

        try {
            val healthdata: List<HealthData> =
                httpClient.get(apiDomain + "/healthDataRecord/$userID")
                    .body()

            return healthdata
        } catch (e: Exception) {
            Log.d("KtorClient getHealthData", e.toString())

            //null or errors
            return emptyList()
        }
    }
}




