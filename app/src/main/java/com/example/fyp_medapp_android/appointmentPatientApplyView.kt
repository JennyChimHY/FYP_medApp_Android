package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fyp_medapp_android.ui.theme.Green20
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appointmentPatientChangeView(appointID: String?) {

    val changeAppointmentResult = remember { mutableStateListOf<Appointment>() }
    val coroutineScope = rememberCoroutineScope() //for apply record
    val snackbarHostState = remember { SnackbarHostState() }
    var openDialog = remember { mutableStateOf(false) } //for success dialog


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

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (changeAppointmentResult.size > 0) {
                    var appointItem = changeAppointmentResult.get(0)

                    var appointDateTimeArr =
                        appointItem.appointDateTime?.split("T")
                    var appointDate = appointDateTimeArr?.get(0)
                    var appointTime = appointDateTimeArr?.get(1)?.substring(0, 5) //24hr format

                    var addAppointmentDate by remember { mutableStateOf("") }
                    var addAppointmentTime by remember { mutableStateOf("") }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
                    ) {
                        Text(
                            text = "Change in Appointment",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(
                        thickness = 2.dp,
                        color = sectionBorderColor
                    )  //section line

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
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
                            .padding(start = 20.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
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

                            Log.d("Appointment Patient Apply Simple Date Format" , SimpleDateFormat("yyyy MMMM dd, HH:mm:ss", Locale.ENGLISH).format(appointItem.appointTimestamp))


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

                    Row() {
                        Column(
                            modifier = Modifier.padding(
                                start = 10.dp,
                                top = 10.dp,
                                end = 60.dp,
                                bottom = 10.dp
                            ),
                        ) {
                            Text(
                                text = "New Appointment Date and Time",
                                fontSize = 20.sp
                            )

                            Text(text = "New Date:")
                        }
                    }

                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            addAppointmentDate = datePickerComponent("applyChangeInAppointment")

                            Text(text = "New Time:")
                            addAppointmentTime = timePickerComponent()
                            //drop down list to select the timeslot
                        }
                    }

                    Row() {
                        //submit edit record
                        Button(
                            onClick = {
                                //call KtorClient API to update record
                                var checkTimeFormat = addAppointmentTime.split(":")
                                //TODO: Improve the logic
                                if (checkTimeFormat[0].length == 1) {
                                    if (checkTimeFormat[1].length == 1) {
                                        addAppointmentTime = "0${checkTimeFormat[0]}:0${checkTimeFormat[1]}"
                                    } else {
                                        addAppointmentTime = "0${checkTimeFormat[0]}:${checkTimeFormat[1]}"
                                    }
                                } else if (checkTimeFormat[1].length == 1) {
                                    addAppointmentTime = "${checkTimeFormat[0]}:0${checkTimeFormat[1]}"
                                }

                                appointItem.appointUpdateDateTime =
                                    "${addAppointmentDate}T${addAppointmentTime}:00.000Z" //2021-09-01T12:00:00.000Z, todo: timeStamp data type
                                appointItem.doctorUpdateStatus = "Pending"

                                CoroutineScope(Dispatchers.IO).launch {

                                    val applyResult: putApplyApproveAppointmentRecordResult =
                                        KtorClient.putApplyAppointment(
                                            appointItem.appointID!!,
                                            appointItem
                                        ) //not String message only, but User data class
                                    var message = ""
                                    Log.d("patchResult", "patchResult: $applyResult")

                                    MainScope().launch {
                                        if (applyResult.acknowledged) {           //success
                                            message =
                                                "Applied Success, pending for the Doctor's Approval."  //The reference code is: ${patchResult.referenceCode}"

                                            Log.d("Applied Success", message)
//                                            snackbarHostState.showSnackbar(message)

                                            openDialog.value = true

                                        } else {     //error
                                            message = "Apply Failed, please try again later."
                                            Log.d("Apply failed", message)
//                                            snackbarHostState.showSnackbar(message)
                                        }
                                    }
                                }
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

                        if (openDialog.value) {
                            AlertDialog(
                                onDismissRequest = {
                                    openDialog.value = false
                                }) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = Green20, shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(15.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    Text(
                                        text = "Apply Success!",
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(10.dp),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    )

                                    HorizontalDivider(
                                        color = sectionBorderColor,
                                        modifier = Modifier
                                            .height(1.dp)
                                            .fillMaxHeight()
                                            .fillMaxWidth()
                                    )

                                    Text(
                                        text = "Your appointment has been successfully applied. It is pending for the Doctor's Approval.",
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(10.dp),
                                    )

                                    HorizontalDivider(
                                        color = sectionBorderColor,
                                        modifier = Modifier
                                            .height(1.dp)
                                            .fillMaxHeight()
                                            .fillMaxWidth()
                                    )

                                    Button(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        onClick = {
                                            openDialog.value = false
                                        }
                                    ) {
                                        Text("OK")
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