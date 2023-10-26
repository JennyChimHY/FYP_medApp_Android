package com.example.fyp_medapp_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.compose.rememberNavController
import com.example.fyp_medapp_android.ui.theme.FYP_medApp_AndroidTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FYP_medApp_AndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    Greeting("MedApp")
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
//        topBar = {
//            TopAppBar(
//                title = { Text(text = "MedApp", color = Color.White, fontSize = 35.sp, fontWeight = FontWeight.Bold) },
//                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
//            )
//
//            //add logout button the the bar
//            if (globalLoginStatus) {
//
//                Row() {
//                    Button(onClick = { navController.navigate("logout") },
//                            colors = ButtonDefaults.buttonColors(
//                            containerColor = MaterialTheme.colorScheme.tertiary,
//                            contentColor = Color.White
//                        )
//                    ) {
//                        Text(text = "Logout")
//                    }
//                }
//            }
//        },
        bottomBar = {
            //ToDO: colour (Green40)
            if (navController.currentDestination?.route != "home") { //home page no bottom bar
                NavigationBar {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Back to Home Page"
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