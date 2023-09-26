package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeNav(navController: NavHostController, snackbarHostState: SnackbarHostState) {

        NavHost(
            navController = navController,
            startDestination = "home",
        ) {
            composable("home") {
                InitialScreen(navController, snackbarHostState)
            }

            composable("login") {
                LoginScreen(navController, snackbarHostState)
            }
//            composable("event/{deptId}") { backStackEntry -> //FROM  navController.navigate("details/123")
//                EventScreen(snackbarHostState, backStackEntry.arguments?.getString("deptId"))
//            }
        }

}

@Composable
fun InitialScreen(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    if(!globalLoginStatus) {


        Column {
            Text(text = "Welcome to MedApp!")

            Button(onClick = {
                Log.d("nav in home screen", navController.toString())

                navController.navigate("login") }
            ) {
                Text(text = "Login")
                HomeScreen(navController, snackbarHostState)
            }

//        Button(onClick = { SignUpScreen(navController, snackbarHostState) }) {
//            Text(text = "Sign Up")
//        }

        }
    } else {
        Text(text = "Welcome" + globalLoginInfo.lastName + " " + globalLoginInfo.firstName + "!")
        //TODO: add logout button, add navigation to other functions


        HomeScreen(navController, snackbarHostState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    //Home Page to choose and navigate the functions, also show the user's name
    //it allows logout here? or upper
    Column() {


    }

}



@Preview(showBackground = true)
@Composable
fun HomePreview() {
    var navController = rememberNavController()
    var snackbarHostState = remember { SnackbarHostState() }
    HomeScreen(navController, snackbarHostState)
}
