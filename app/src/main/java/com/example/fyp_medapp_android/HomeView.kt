package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.fyp_medapp_android.ui.theme.Green40

var modifierPadding = 16.dp
var sectionBorderColor = Green40
@Composable
fun HomeNav(navController: NavHostController, snackbarHostState: SnackbarHostState) {

        NavHost(
            navController = navController,
            startDestination = "home",
        ) {
            composable("home") {

                if(!globalLoginStatus) {
                    InitialScreen(navController, snackbarHostState)
                } else {
                    HomeScreen(navController, snackbarHostState)
                }
            }

            composable("login") {
                LoginScreen(navController, snackbarHostState)
            }

            composable("logout") {
                Logout(navController, snackbarHostState)
            }

            composable("profile") {
                profileScreen(navController)
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


//Functions Explain:
//1. InitialScreen: .....

//TODO: Scaffold in each screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitialScreen(navController: NavHostController, snackbarHostState: SnackbarHostState) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "MedApp", color = Color.White, fontSize = 35.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )

            //add logout button the the bar
            if (globalLoginStatus) {

                Row() {
                    Button(onClick = { navController.navigate("logout") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Logout")
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },  //lab11
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding),) {

                if (!globalLoginStatus) {  // not login

                    Column(
                        Modifier.padding(modifierPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Row() {
                            Text(
                                text = "Welcome to MedApp!",
                                modifier = Modifier.padding(
                                    start = 5.dp,
                                    top = 20.dp,
                                    end = 5.dp,
                                    bottom = 20.dp
                                ),
                                fontSize = 35.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 1.5.em,
                            )
                        }

                        Spacer(Modifier.size(modifierPadding))

                        Row() {
                            Button(onClick = {
                                navController.navigate("login")
                            }
                            ) {
                                Text(
                                    text = "Login",
                                    modifier = Modifier.padding(modifierPadding),
                                    textAlign = TextAlign.Center,
                                    fontSize = 38.sp
                                )
                            }
                        }

                        Spacer(Modifier.size(modifierPadding))

                        newsFeedCardSection()

                    }
                } else {
                    navController.navigate("home")
                }
            }
        },
    )
}

@Composable
fun welcomeSection() {
    Row() {
        Text(
            text = "Welcome\n${globalLoginInfo.lastName} ${globalLoginInfo.firstName} !",
            modifier = Modifier.padding(
                start = 10.dp,
                top = 10.dp,
                end = 10.dp,
                bottom = 20.dp
            ),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 1.em
        )

        // User Profile Icon -> navigate
        Column() {
            Text(
                text = "User\nProfile",
                modifier = Modifier.padding(
                    start = 10.dp,
                    top = 10.dp,
                    end = 10.dp,
                    bottom = 20.dp
                ),
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal
            )
        }

        Column() {
            IconButton(onClick = {
//                navController.navigate("profile")
            }) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "View User Profile",
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}

@Composable
fun selectFunctionSection(navController: NavHostController) {
    var functionImageVectorList = mutableVectorOf<ImageVector>(Icons.Default.Face, Icons.Default.Call, Icons.Default.Person)
    var functionContentDescriptionList = mutableListOf<String>("medicine", "appointment", "healthData")

    Row(modifier = Modifier.padding(modifierPadding)) {
        Text(
            text = "Please choose the Function:",
            modifier = Modifier.padding(
                start = 5.dp,
                top = 20.dp,
                end = 5.dp,
                bottom = 20.dp
            ),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 1.em,
        )
    }

    Row(modifier = Modifier.padding(modifierPadding),
        verticalAlignment = Alignment.CenterVertically) {

        for (i in functionImageVectorList.indices) {
            IconButton(onClick = {
                navController.navigate(functionContentDescriptionList[i])
            }) {
                Icon(
                    imageVector = functionImageVectorList[i],
                    contentDescription = functionContentDescriptionList[i],
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(Modifier.size(modifierPadding))

        }
    }

}
@Composable
fun newsFeedCardSection() {

    Row() {
        Card(
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.size(width = 350.dp, height = 150.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            AsyncImage(
                //fetch the backend directly, apiDomain is a global var from KtorClient
                model = apiDomain + "/images/MedApp_utilities/healthNews.jpg",
                contentDescription = null,
                //TODO: fill height
                contentScale = ContentScale.FillHeight,

                )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    //Home Page to choose and navigate the functions, also show the user's name
    //it allows logout here? or upper

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Home", color = Color.White, fontSize = 35.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )

            //add logout button the the bar
            if (globalLoginStatus) {

                Row() {
                    Button(onClick = { navController.navigate("logout") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Logout")
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },  //lab11
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {

//        ========= Section 1 ===========

                welcomeSection()

                HorizontalDivider(
                    thickness = 2.dp,
                    color = sectionBorderColor
                )  //section line

//        ========= Section 2: Select functions ===========

                selectFunctionSection(navController)

//        ========= Section 3: newsFeed  ===========
                newsFeedCardSection()
            }
        }
    )
}



@Preview(showBackground = true)
@Composable
fun HomePreview() {
    var navController = rememberNavController()
    var snackbarHostState = remember { SnackbarHostState() }
    HomeScreen(navController, snackbarHostState)
}
