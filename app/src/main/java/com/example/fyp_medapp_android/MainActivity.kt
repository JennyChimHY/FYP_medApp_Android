package com.example.fyp_medapp_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.fyp_medapp_android.ui.theme.FYP_medApp_AndroidTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview(showBackground = true)
    @Composable
    fun BasicSetting() {
        val snackbarHostState = remember { SnackbarHostState() }
        val navController = rememberNavController()
//        LoginScreen(navController, snackbarHostState)
        HomeNav(navController = navController, snackbarHostState = snackbarHostState)
    }

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