package com.example.fyp_medapp_android

import android.Manifest.permission.*
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.fyp_medapp_android.ui.theme.FYP_medApp_AndroidTheme
import com.example.fyp_medapp_android.ui.theme.Green20
import java.time.LocalDateTime


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //create empty local notification alarm channel
        val alarmScheduler: NotiAlarmScheduler = NotiAlarmSchedulerImpl(this)
        var alarmItem: NotiAlarmItem? = null

        val requestPermissionLauncher =
            registerForActivityResult( //call at Main Activity
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }

        //ask for ACCESS_COARSE_LOCATION
        when {
            ContextCompat.checkSelfPermission(
                this.applicationContext,
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {} // You can use the API that requires the permission.

            //permitted already, take no action.
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, ACCESS_COARSE_LOCATION) -> {}
            else -> {
                //no permit, ask for the permission
                requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
            }
        }

        //ask for ACCESS_FINE_LOCATION
        when {
            ContextCompat.checkSelfPermission(
                this.applicationContext,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {}

            //permitted already, take no action.
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, ACCESS_FINE_LOCATION) -> {}
            else -> {
                //no permit, ask for the permission
                requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
            }
        }

        //ask for ACCESS_BACKGROUND_LOCATION
        when {
            ContextCompat.checkSelfPermission(
                this.applicationContext,
                ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {}

            //permitted already, take no action.
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, ACCESS_BACKGROUND_LOCATION) -> {}
            else -> {
                //no permit, ask for the permission
                requestPermissionLauncher.launch(ACCESS_BACKGROUND_LOCATION)
            }
        }



        setContent {
            FYP_medApp_AndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Testing alarm scheduling
                    alarmItem = NotiAlarmItem(  //empty list of time, type of reminder and photo?
                        alarmTime = LocalDateTime.now().plusSeconds(
                            "8".toLong()
                        ),
                        notiType = "Initial",
                        message = "Testing Alarm Success, Reminder Success.",
                        picture = "xarelto"
                    )
                    alarmItem?.let(alarmScheduler::schedule)

                    //turn to home page
                    BasicSetting()
                }



            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun BasicSetting() {
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()

    Scaffold(
        //only the location of bottom bar is fixed in all pages
        bottomBar = {
            //ToDO: colour (Green40)
            if (navController.currentDestination?.route != "home") { //home page no bottom bar
                NavigationBar {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Back to Home Page",
//                                tint = Color.Transparent //failed
                            )
                        },
                        label = { Text("Home") },
                        selected = true,
                        onClick = { navController.navigate("home") }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },  //lab11
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                HomeNav(navController = navController, snackbarHostState = snackbarHostState)
            }
        },
    )

}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

//not using
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FYP_medApp_AndroidTheme {
        Greeting("MedApp")
    }
}