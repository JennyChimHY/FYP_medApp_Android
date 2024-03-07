package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.fyp_medapp_android.ui.theme.Green20
import com.example.fyp_medapp_android.ui.theme.Green50
import kotlinx.serialization.Serializable

@Serializable
data class Appointment(
    val _id: String?,
    val appointID: String?,
    val patientID: String?,
    val doctorID: String?,
    val appointDateTime: String?,
    val appointPlace: String?,
    val appointClass: String?,
    val appointType: String?,
    val appointStatus: String?,
    val appointRemark: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appointmentScreen(navController: NavHostController) {
    Scaffold(
        //diaplay the header of each page
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Appointment",
                        color = Color.White,
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )

//            //add logout button the the bar
//            logoutButton(navController)
        },
        snackbarHost = { },
        content = { innerPadding ->
            //display the content of the page
            boxTextSize = 15.sp

            Column(modifier = Modifier.padding(innerPadding)) {
                val appointmentResult = produceState(
                    initialValue = listOf<Appointment>(),
                    producer = {
                        value =
                            KtorClient.getAppointment(globalLoginInfo.userID) //not String message only, but User data class
                    })
                Log.d("appointScreen after calling API", "appointmentResult: $appointmentResult")

                Row() {
                    Text(text = "View Appointment Record")
                }
                //Display records card-by card
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    //TODO scrollable Modifier.verticalScroll(rememberScrollState())

                    items(appointmentResult.value) { appointItem ->
                        var appointDateTimeArr = appointItem.appointDateTime?.split("T")
                        var appointDate = appointDateTimeArr?.get(0)
                        var appointTime = appointDateTimeArr?.get(1)?.substring(0, 5) //24hr format
                        //TODO: 12 hrs conversion

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Green50,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(5f) // Take 50% of the available width
                                        .fillMaxHeight()
                                ) {
                                    Box(  //Class Label
                                        modifier = Modifier
                                            .size(120.dp, 40.dp)
                                            .background(
                                                color = Green20,
                                                shape = RoundedCornerShape(8.dp))
                                    ) {
                                        Text(
                                            appointItem.appointType.toString(),
                                            fontSize = boxTextSize,
                                            color = (Color.Black),
                                            modifier = Modifier.align(Alignment.Center),
                                            maxLines = 2
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(5f) // Take 50% of the available width
                                        .fillMaxHeight()
                                ) {
                                    Box(  //Class Label
                                        modifier = Modifier
                                            .size(120.dp, 40.dp)
                                            .background(
                                                color = Green20,
                                                shape = RoundedCornerShape(8.dp))
                                    ) {
                                        Text(
                                            appointItem.appointClass.toString(),
                                            fontSize = boxTextSize,
                                            color = (Color.Black),
                                            modifier = Modifier.align(Alignment.Center),
                                            maxLines = 2
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {

                                Column(
                                    modifier = Modifier
                                        .weight(3f) // Take 50% of the available width
                                        .fillMaxHeight()
                                ) {

                                    Text(
                                        text = "Date:",
                                        textAlign = TextAlign.Start,
                                        fontSize = 20.sp
                                    )
                                    Text(
                                        text = "Time:",
                                        textAlign = TextAlign.Start,
                                        fontSize = 20.sp
                                    )
                                    Text(
                                        text = "Place:",
                                        textAlign = TextAlign.Start,
                                        fontSize = 20.sp
                                    )
                                    if (appointItem.appointRemark != null) {
                                        Text(
                                            text = "Remark:",
                                            textAlign = TextAlign.Start,
                                            fontSize = 20.sp
                                        )
                                    }
                                    Text(
                                        text = "Status:", //TODO: change to icon
                                        textAlign = TextAlign.Start,
                                        fontSize = 20.sp
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(7f) // Take 50% of the available width
                                        .fillMaxHeight()
                                ) {

                                    Text(
                                        text = "$appointDate",
                                        textAlign = TextAlign.Start,
                                        fontSize = 20.sp
                                    )
                                    Text(
                                        text = "$appointTime",
                                        textAlign = TextAlign.Start,
                                        fontSize = 20.sp
                                    )
                                    Text(
                                        text = "${appointItem.appointPlace.toString()}",
                                        textAlign = TextAlign.Start,
                                        fontSize = 20.sp
                                    )
                                    if (appointItem.appointRemark != null) {
                                        Text(
                                            text = "${appointItem.appointRemark.toString()}",
                                            textAlign = TextAlign.Start,
                                            fontSize = 20.sp
                                        )
                                    }
                                    Text(
                                        text = "${appointItem.appointStatus.toString()}", //TODO: change to icon
                                        textAlign = TextAlign.Start,
                                        fontSize = 20.sp
                                    )

                                    Button (
                                        onClick = {
                                            navController.navigate("appointmentChange/${appointItem.appointID}")
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary,
                                            contentColor = Color.Black
                                        ),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 10.dp
                                        )
                                    ) {
                                        Text("Change")  //Change Datetime
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )

}