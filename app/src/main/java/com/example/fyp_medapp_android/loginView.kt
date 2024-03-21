package com.example.fyp_medapp_android

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import kotlin.math.log

var globalLoginStatus: Boolean = false
lateinit var globalLoginInfo: User
lateinit var globalLoginPatientInfo: User //for patient profile view, in caregiver and doctor mode
lateinit var targetUserID: String

@Serializable
data class Info(  //for frontend input and send to backend
    val username: String,
    val password: String,
    val patientFCM_token: String
)

@Serializable
data class PatientConnection(
    val patientID: String,
    val patientName: String
)

@Serializable
data class User(
    val token: String?,
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
    var patientConnection: Array<PatientConnection>? = arrayOf<PatientConnection>(),
    var patientProfileList: List<User>?  //outter layer
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    val padding = 16.dp
    var usernameLocal by remember { mutableStateOf("") } //data class
    var pwdLocal by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current  //for notification, composable function outter layer*

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Login",
                        color = Color.White,
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },  //lab11
        content = { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Image(
                    modifier = Modifier
                        .size(125.dp),
                    imageVector = Icons.Default.Person,
                    contentDescription = "Login Icon"
                )

//                Row(
//                    Modifier.padding(bottom = 16.dp)
//                ) {
//                    Text(
//                        text = "Login",
//                        fontSize = 38.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                }

                Row(
                    Modifier.padding(16.dp),
                ) { //verticalAlignment = Alignment.CenterVertically
                    OutlinedTextField(
                        label = { Text("Username or User ID:") },
                        textStyle = TextStyle.Default.copy(fontSize = 28.sp),
                        singleLine = true,
                        value = usernameLocal,
                        onValueChange = { usernameLocal = it }
                    )
                }

                Spacer(Modifier.size(padding))

                Row(
                    Modifier.padding(16.dp),
                ) { //verticalAlignment = Alignment.CenterVertically
                    OutlinedTextField(
                        label = { Text("Password") },
                        textStyle = TextStyle.Default.copy(fontSize = 28.sp),
                        singleLine = true,
                        value = pwdLocal,
                        onValueChange = { pwdLocal = it },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }

                Spacer(Modifier.size(padding))

                Button(
                    onClick = {

                        CoroutineScope(Dispatchers.IO).launch {

                            val info = Info(
                                usernameLocal,
                                pwdLocal,
                                patientFCM_token
                            ) //create an object based on Info data class

                            val loginResult: User =
                                KtorClient.postLogin(info) //not String message only, but User data class
//
                            coroutineScope.launch {
                                var message = ""
                                if (loginResult.userID != null) {           //success
                                    message =
                                        "Login Success. Welcome ${loginResult.lastName ?: ""} ${loginResult.firstName ?: ""}." //null safety

                                    //call another API to take patient profile list
                                    val patientProfileList = KtorClient.getPatientProfileList()
                                    println("patientProfileList: $patientProfileList")
                                    if (patientProfileList != null) {
                                        loginResult.patientProfileList = patientProfileList
                                    }

                                    globalLoginStatus = true; //redirected in HomeNav
                                    globalLoginInfo = loginResult;
                                    targetUserID = loginResult.userID.toString()

                                    //================ 1st Alarm: Medicine Reminder ================
                                    val notiAlarmScheduler: NotiAlarmScheduler =
                                        NotiAlarmSchedulerImpl(context.applicationContext)

                                    //if allow notification --> take medicine record and add into notification channel
                                    if (loginResult.userRole == "patient" && NotificationManagerCompat.from(
                                            context
                                        ).areNotificationsEnabled()
                                    ) {
                                        //check system noti enable or not
                                        val medicineList =
                                            KtorClient.getMedicine(loginResult.userID)
                                        println("medicineList: $medicineList")
                                        if (medicineList != null) {

                                            val currentDateTime = LocalDateTime.now()
                                            //use for loop to add each alarm item
                                            for (medicine in medicineList) {
                                                var setHour = 9 //reset default daily intake = 1

                                                for (i in 0 until medicine.dailyIntake!!) {
                                                    if (medicine.dailyIntake > 1) { //daily intake > 1
                                                        setHour =
                                                            9 + ((12 / (medicine.dailyIntake - 1)) * i)
                                                    }
                                                    val newDateTime = currentDateTime
                                                        .withHour(setHour) //9, 13, 17, 21 if dailyIntake = 4
                                                        .withMinute(0)
                                                        .withSecond(0)
                                                        .withNano(0)

                                                    Log.d(
                                                        "${medicine.medicineInfo?.medicineName} Reminder newDateTime, $i time of ${medicine.dailyIntake}",
                                                        newDateTime.toString()
                                                    )

                                                    val notiAlarmItem = NotiAlarmItem(
                                                        alarmTime = newDateTime//LocalDateTime.now().plusSeconds("10".toLong())
                                                        , //medicine.dailyIntake
                                                        notiType = "Medicine",
                                                        message = "${medicine.medicineInfo?.medicineName}:\n${medicine.dailyIntake} time(s) per day,\n${medicine.eachIntakeAmount} dose(s) each time.",
                                                        picture = medicine.medicineInfo?.medicineImageName
                                                    )
                                                    notiAlarmItem?.let(notiAlarmScheduler::schedule)
                                                    notiAlarmScheduler.schedule(notiAlarmItem)
                                                }
                                            }
                                        }

                                        //================ 2nd Alarm: Location Alarm ================
                                        //create empty location alarm channel
                                        val locationScheduler: LocationAlarmScheduler =
                                            LocationAlarmSchedulerImpl(context.applicationContext)

                                        if (loginResult.userRole == "patient" && ActivityCompat.checkSelfPermission(
                                                context!!.applicationContext,
                                                Manifest.permission.ACCESS_FINE_LOCATION
                                            ) == PackageManager.PERMISSION_GRANTED
                                        ) {
                                            //set location alarm with user id

                                            val currentDateTime =
                                                LocalDateTime.now().plusSeconds("10".toLong())
                                            val locationItem = LocationAlarmItem(
                                                alarmTime = currentDateTime,
                                                locationUser = loginResult.userID.toString()
                                            )

                                            locationItem?.let(locationScheduler::schedule)
                                            locationScheduler.schedule(locationItem) //An Alarm trigger a fusedLocationProviderClient with looper to get locations
                                            Log.d(
                                                "loginView userProfile",
                                                "Location alarm scheduled."
                                            )

                                        }
                                    }

                                    Log.d("loginView userProfile", message)
                                    snackbarHostState.showSnackbar(message)
                                    navController.navigate("home") //pass to home page


                                } else {     //error
                                    //handle login false
                                    println("login false")
                                    println(loginResult)
                                    message =
                                        "Login Failed. The email or password is incorrect, please input again."

                                }
                                Log.d("loginView userProfile", message)
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    },
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 10.dp
                    )
                ) {
                    Text(
                        text = "Login",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 38.sp
                    )
                }
            }
        },
    )
}

@Composable
fun Logout(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    // TODO
    //  clearCache() //clear all stored data including medicine, appoint, health data
    globalLoginInfo =
        User(null, null, null, null, null, null, null, null, null, null, null, null, null, null)
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
