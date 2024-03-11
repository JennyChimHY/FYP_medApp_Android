package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appointmentPatientChangeView(appointID: String?) {

    val changeAppointmentResult = remember { mutableStateListOf<Appointment>() }
//    var appointItem: Appointment? = null

    //call KTor client to get the latest appointment details
    if (changeAppointmentResult.size == 0) {
        CoroutineScope(Dispatchers.IO).launch {
            val list = KtorClient.getAppointment(appointID)
            CoroutineScope(Dispatchers.Main).launch {
                changeAppointmentResult.clear()
                changeAppointmentResult.addAll(list)
            }
        }
    }

    Log.d(
        "changeAppointmentResult",
        "changeAppointmentResult = ${changeAppointmentResult.size}"
    )

    Scaffold(
        //diaplay the header of each page
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Change Appointment",
                        color = Color.White,
                        fontSize = 28.sp,
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

            Column(modifier = Modifier.padding(innerPadding)
                .verticalScroll(rememberScrollState())) {

                if (changeAppointmentResult.size > 0) {
                    var appointItem = changeAppointmentResult.get(0)

                    var appointDateTimeArr =
                        appointItem.appointDateTime?.split("T")
                    var appointDate = appointDateTimeArr?.get(0)
                    var appointTime = appointDateTimeArr?.get(1)?.substring(0, 5) //24hr format

                    lateinit var appointUpdateDateTime: String
                    var doctorUpdateStatus = "Pending"

                    var addAppointmentDate by remember { mutableStateOf("") }
                    var addAppointmentTime by remember { mutableStateOf("") }


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()  //BUG: right padding not working, Column innerPadding?
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Application of\nChange in Appointment",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    HorizontalDivider(
                        thickness = 2.dp,
                        color = sectionBorderColor
                    )  //section line

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Current Appointment Record",
                            fontSize = 20.sp,
//                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        )
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
                                text = "${appointItem.appointPlace}",
                                textAlign = TextAlign.Start,
                                fontSize = 20.sp
                            )
                            if (appointItem.appointRemark != null) {
                                Text(
                                    text = "${appointItem.appointRemark}",
                                    textAlign = TextAlign.Start,
                                    fontSize = 20.sp
                                )
                            }
                            Text(
                                text = "${appointItem.appointStatus}", //TODO: change to icon
                                textAlign = TextAlign.Start,
                                fontSize = 20.sp
                            )
                        }
                    }

                    HorizontalDivider(
                        thickness = 2.dp,
                        color = sectionBorderColor
                    )  //section line

                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "New Appointment Date and Time",
                                fontSize = 20.sp
                            )

                                Text(text = "New Date:")

                                addAppointmentDate = datePickerComponent("applyChangeInAppointment")

                                Text(text = "New Time:")
                                addAppointmentTime = timePickerComponent()
                                //drop down list to select the timeslot
                        }

                        Row() {
                            //submit edit record
                            Button(
                                onClick = {
                                    //validation and call API to PATCH
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
                                Text("Submit Application")  //Change Datetime
                            }
                        }
                    }
                }
            }
        }
    )
}