package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
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

            composable("logout") {
                Logout(navController, snackbarHostState)
            }

            composable("medicine") {
                medicineSceen(navController)
            }

            composable("appointment") {
                appointmentScreen(navController)
            }

//            composable("healthData") {
//                healthDataScreen(navController)
//            }

//            composable("event/{deptId}") { backStackEntry -> //FROM  navController.navigate("details/123")
//                EventScreen(snackbarHostState, backStackEntry.arguments?.getString("deptId"))
//            }
        }

}

@Composable
fun InitialScreen(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    if(!globalLoginStatus) {

        Column(Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(text = "Welcome to MedApp!",
                modifier = Modifier.padding(24.dp),
                fontSize = 40.sp,
                lineHeight = 2.em,
                )
            Button(onClick = {
                navController.navigate("login") }
            ) {
                Text(text = "Login",
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 38.sp
                    )
            }

//        Button(onClick = { SignUpScreen(navController, snackbarHostState) }) {
//            Text(text = "Sign Up")
//        }

        }
    } else {
        Text(text = "Welcome ${globalLoginInfo.lastName} ${globalLoginInfo.firstName} !")
        //TODO: add logout button, add navigation to other functions


        HomeScreen(navController, snackbarHostState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    //Home Page to choose and navigate the functions, also show the user's name
    //it allows logout here? or upper

    var functionImageVectorList = mutableVectorOf<ImageVector>(Icons.Default.Face, Icons.Default.Call, Icons.Default.Person)
    var functionContentDescriptionList = mutableListOf<String>("medicine", "appointment", "healthData")
    if (globalLoginStatus) {

        Row() {
            Text(text = "Welcome ${globalLoginInfo.lastName} ${globalLoginInfo.firstName} !")
            Button(onClick = { navController.navigate("logout") }) {
                Text(text = "Logout")
            }
        }

        Column() {
            for(i in functionImageVectorList.indices) {
                IconButton(onClick = {
                    navController.navigate(functionContentDescriptionList[i])
                }) {
                    Icon(
                        imageVector = functionImageVectorList[i],
                        contentDescription = functionContentDescriptionList[i]
                    )
                }

            }
        }
    }

}



@Preview(showBackground = true)
@Composable
fun HomePreview() {
    var navController = rememberNavController()
    var snackbarHostState = remember { SnackbarHostState() }
    HomeScreen(navController, snackbarHostState)
}
