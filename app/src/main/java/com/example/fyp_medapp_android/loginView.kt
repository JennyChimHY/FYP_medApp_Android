package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.math.log

var globalLoginStatus: Boolean = false
lateinit var globalLoginInfo: User

@Serializable
data class Info(
    val username: String,
    val password: String
)

@Serializable
data class User(
    val _id: String?,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    val padding = 16.dp
    var usernameLocal by remember { mutableStateOf("") } //data class
    var pwdLocal by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Login", color = Color.White, fontSize = 35.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },  //lab11
        content = { innerPadding ->

            //Login UI

            Column(Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Username or User ID: ", fontSize = 30.sp,
                        modifier = Modifier.padding(16.dp)
                    )

                }
                Row(Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        textStyle = TextStyle.Default.copy(fontSize = 28.sp),
                        maxLines = 1,
                        value = usernameLocal,
                        onValueChange = { usernameLocal = it }
                    )
                }
                Spacer(Modifier.size(padding))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Password: ", fontSize = 30.sp)
                }
                Row(
                    Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        textStyle = TextStyle.Default.copy(fontSize = 28.sp),
                        maxLines = 1,
                        value = pwdLocal,
                        onValueChange = { pwdLocal = it }
                    )
                }
                Spacer(Modifier.size(padding))

                Button(onClick = {
                    coroutineScope.launch {

                        val info = Info(usernameLocal, pwdLocal) //create an object based on Info data class

                        val loginResult: User =
                            KtorClient.postLogin(info) //not String message only, but User data class
                        var message = ""
                        if (loginResult.resultCode == "200") {           //success
                            message =
                                "Login Success. Welcome ${loginResult.lastName ?: ""} ${loginResult.firstName ?: ""}." //null safety

                            globalLoginStatus = true; //redirected in HomeNav
                            globalLoginInfo = loginResult;

                            Log.d("loginView userProfile", message)
//                    snackbarHostState.showSnackbar(message) BUG HERE

                            Log.d("after login navcontroller", navController.toString())
                            navController.navigate("home") //pass to home page


                        } else if (loginResult.resultCode == "400") {     //error
                            message =
                                "Login Failed. The email or password is incorrect, please input again."

                        }
                        Log.d("loginView userProfile", message)
                        snackbarHostState.showSnackbar(message)
                    }
                }) {
                    Text(text = "Login",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 38.sp)
                }
            }
        },
    )
}

@Composable
fun Logout(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    // TODO
    //  clearCache() //clear all stored data including medicine, appoint, health data
    globalLoginInfo = User(null, null, null, null, null, null, null, null, null, null, null, null, null)
    globalLoginStatus = false

//    snackbarHostState = remember { SnackbarHostState() } //TODO: clean after logout
//    snackbarHostState.showSnackbar("Logout Success. See you next time!")
    Log.d("after logout", " Logout Success. See you next time!")
    navController.navigate("home") //pass to home page
}

@Composable
fun LoginScreen(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    Column(horizontalAlignment = Alignment.Start) {
        Login(navController, snackbarHostState)
    }
}
