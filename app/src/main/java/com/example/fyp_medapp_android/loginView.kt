package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.math.log

var globalLoginStatus: Boolean = false
lateinit var globalLoginInfo: User

@Composable
fun LoginGreeting() {
    val padding = 3.dp
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.size(padding))
        Text(text = "Login", fontSize = 24.sp, modifier = Modifier.padding(16.dp))
    }
}

@Serializable
data class Info(
    val username: String,
    val password: String
)

data class User(
    val _id: String?,
    val hkid: String?,
    val firstName: String?, //200 success
    val lastName: String?,
    val gender: String?,
    val age: Int?,
    val dob: String?,
    val email: String?,
    var username: String?,
    var password: String?,
    var isPatient: Boolean?,
    var resultCode: String?

//    val token: String?,
//    val error: String?,
)

@Composable
fun Login(snackbarHostState: SnackbarHostState) {
    val padding = 16.dp
    var usernameLocal by remember { mutableStateOf("") } //data class
    var pwdLocal by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
//    val navController = rememberNavController()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Username: ", fontSize = 16.sp,
                modifier = Modifier.padding(18.dp))
            TextField(
                maxLines = 1,
                value = usernameLocal,
                onValueChange = { usernameLocal = it }
            )
        }
        Spacer(Modifier.size(padding))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Password: ", fontSize = 16.sp)
            TextField(
                maxLines = 1,
                value = pwdLocal,
                onValueChange = { pwdLocal = it }
            )
        }
        Spacer(Modifier.size(padding))

        Button(onClick = {
            coroutineScope.launch {

                val info = Info(usernameLocal, pwdLocal) //create an object based on Info data class

                val loginResult: User = KtorClient.postLogin(info) //not String message only, but User data class
                var message = ""
                if (loginResult.resultCode == "200") {           //success
                    message =
                        "Login Success. Welcome ${loginResult.lastName ?: ""} ${loginResult.firstName ?: ""}." //null safety

                    globalLoginStatus = true
                    globalLoginInfo = User(loginResult._id, loginResult.hkid, loginResult.firstName, loginResult.lastName, loginResult.gender, loginResult.age, loginResult.dob, loginResult.email, loginResult.username, loginResult.password, loginResult.isPatient, loginResult.resultCode);
//                    HomeNav(navController, snackbarHostState, loginResult) //call home page --> make in homeView

                } else if (loginResult.resultCode == "400")     //error
                    message =
                        "Login Failed. The email or password is incorrect, please input again."

                Log.d("loginView userProfile", message)
                snackbarHostState.showSnackbar(message)

            }
        }) {
            Text(text = "Login")
        }
    }
}

@Composable
fun LoginScreen(snackbarHostState: SnackbarHostState) {
    Column(horizontalAlignment = Alignment.Start) {
        LoginGreeting()
        Login(snackbarHostState)
    }
}
