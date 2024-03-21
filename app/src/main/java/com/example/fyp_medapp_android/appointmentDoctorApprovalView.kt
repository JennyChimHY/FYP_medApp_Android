package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fyp_medapp_android.ui.theme.*
import com.google.firebase.messaging.Constants.MessagePayloadKeys.SENDER_ID
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class ApproveRejectAppointRecord(
    var appointID: String,
    var doctorUpdateStatus: String,
    var appointUpdateDateTime: String,
    var appointUpdateTimestamp: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun doctorApprovalScreen(navController: NavController) {

    val appointmentDoctorResult = remember { mutableStateListOf<Appointment>() }
    var openDialog = remember { mutableStateOf(false) } //for success dialog

    //call Ktor Client to get the appointment record list, use condition to show the applied appointment only
    if (appointmentDoctorResult.size == 0) {
        CoroutineScope(Dispatchers.IO).launch {
            val list = KtorClient.getAppointment(targetUserID)
            CoroutineScope(Dispatchers.Main).launch {
                appointmentDoctorResult.clear()
                appointmentDoctorResult.addAll(list)
            }
        }
    }

    Scaffold(
        //diaplay the header of each page
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Change Appointment Approval",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )

        },
        snackbarHost = { },
        content = { innerPadding ->

            var textPadding = 8.dp
            var doctorTextSize = 16.sp

            var callKtor = remember { mutableStateOf(false) }
            var approveRejectRecord = ApproveRejectAppointRecord("", "", "", 0)

            var doctorApprove = false

            Column(
                modifier = Modifier
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Doctor Approval",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )

                HorizontalDivider(
                    thickness = 2.dp,
                    color = sectionBorderColor
                )  //section line

                //display the list of appointments with Application

                if (appointmentDoctorResult.size > 0) {
                    Log.d("appointmentDoctorResult", appointmentDoctorResult.get(0).toString())
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        items(appointmentDoctorResult) { appointItem ->

                            if (appointItem.appointUpdateDateTime != null && appointItem.doctorUpdateStatus == "Pending") {
                                var originalAppointDateTimeArr =
                                    appointItem.appointDateTime?.split("T")
                                var originalAppointDate = originalAppointDateTimeArr?.get(0)
                                var originalAppointTime = originalAppointDateTimeArr?.get(1)
                                    ?.substring(0, 5) //24hr format

                                var updateAppointDateTimeArr =
                                    appointItem.appointUpdateDateTime?.split("T")
                                var updateAppointDate = updateAppointDateTimeArr?.get(0)
                                var updateAppointTime =
                                    updateAppointDateTimeArr?.get(1)?.substring(0, 5) //24hr format

                                var patientID = appointItem.patientID //for firebase notification

                                Log.d(
                                    "Appointment Doctor Simple Date Format",
                                    SimpleDateFormat(
                                        "yyyy MMMM dd, HH:mm:ss",
                                        Locale.ENGLISH
                                    ).format(appointItem.appointTimestamp)
                                )


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
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {

                                        Column(
                                            modifier = Modifier
                                                .weight(5f) // Take 50% of the available width
                                                .fillMaxHeight()
                                        ) {
//
                                            Box(  //Appointment type Label
                                                modifier = Modifier
                                                    .size(120.dp, 40.dp)
                                                    .background(
                                                        color = yellow40,
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                            ) {
                                                Text(
                                                    text = "${appointItem.appointClass}",
                                                    fontSize = boxTextSize,
                                                    color = (Color.Black),
                                                    modifier = Modifier.align(Alignment.Center),
                                                    maxLines = 2
                                                )
                                            }
                                        }

                                        Column(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                        ) {
                                            Box(  //Appointment type Label
                                                modifier = Modifier
                                                    .size(120.dp, 40.dp)
                                                    .background(
                                                        color = Green20,
                                                        shape = RoundedCornerShape(8.dp)
                                                    ),
                                                contentAlignment = Alignment.TopEnd
                                            ) {
                                                Text(
                                                    text = "${appointItem.appointType}",
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
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(4f) // Take 50% of the available width
                                                .fillMaxHeight()
                                        ) {
                                            Text(
                                                text = "Patient ID:",
                                                fontSize = doctorTextSize,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.padding(start = textPadding)
                                            )

                                            Text(
                                                text = "Place:",
                                                fontSize = doctorTextSize,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.padding(start = textPadding)
                                            )

                                            Text(
                                                text = "Original Date:",
                                                fontSize = doctorTextSize,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.padding(start = textPadding)
                                            )

                                            Text(
                                                text = "Original Time:",
                                                fontSize = doctorTextSize,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.padding(start = textPadding)
                                            )

                                            Text(
                                                text = "Applied Date:",
                                                fontSize = doctorTextSize,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.padding(start = textPadding)
                                            )

                                            Text(
                                                text = "Applied Time:",
                                                fontSize = doctorTextSize,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.padding(start = textPadding)
                                            )
                                        }

                                        Column(
                                            modifier = Modifier
                                                .weight(6f) // Take 50% of the available width
                                                .fillMaxHeight()
                                        ) {

                                            Text(
                                                text = "${appointItem.patientID}",
                                                fontSize = doctorTextSize,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.padding(start = textPadding)
                                            )

                                            Text(
                                                text = "${appointItem.appointPlace}",
                                                fontSize = doctorTextSize,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.padding(start = textPadding)
                                            )

                                            Text(
                                                text = "${originalAppointDate}",
                                                fontSize = doctorTextSize,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.padding(start = textPadding)
                                            )

                                            Text(
                                                text = "${originalAppointTime}", //"${appointItem.appointTimestamp}",
                                                fontSize = doctorTextSize,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.padding(start = textPadding)
                                            )

                                            Text(
                                                text = "${updateAppointDate}",
                                                fontSize = doctorTextSize,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.padding(start = textPadding)
                                            )

                                            Text(
                                                text = "${updateAppointTime}",
                                                fontSize = doctorTextSize,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.padding(start = textPadding)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(5f) // Take 50% of the available width
                                                .fillMaxHeight()
                                        ) {

                                            Button( //approve
                                                onClick = {

                                                    approveRejectRecord =
                                                        ApproveRejectAppointRecord(
                                                            appointItem.appointID!!,
                                                            "Approved",
                                                            appointItem.appointUpdateDateTime!!,
                                                            appointItem.appointTimestamp!!
                                                        )

                                                    Log.d(
                                                        "Approve",
                                                        "Approve: $approveRejectRecord"
                                                    )

                                                    doctorApprove = true
                                                    callKtor.value = true
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Green20,
                                                    contentColor = Color.Black
                                                ),
                                                elevation = ButtonDefaults.buttonElevation(
                                                    defaultElevation = 10.dp
                                                )
                                            ) {
                                                Text("Approve")
                                            }
                                        }

                                        Column(
                                            modifier = Modifier
                                                .weight(5f) // Take 50% of the available width
                                                .fillMaxHeight()
                                        ) {

                                            Button( //reject
                                                onClick = {
//                                                        approveRejectStatus = "Rejected"

                                                    approveRejectRecord =
                                                        ApproveRejectAppointRecord(
                                                            appointItem.appointID!!,
                                                            "Rejected",
                                                            appointItem.appointUpdateDateTime!!,
                                                            0
                                                        )

                                                    doctorApprove = false
                                                    callKtor.value = true
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Red10,
                                                    contentColor = Color.Black
                                                ),
                                                elevation = ButtonDefaults.buttonElevation(
                                                    defaultElevation = 10.dp
                                                )
                                            ) {
                                                Text("Reject")
                                            }
                                        }

                                        if (callKtor.value) {
                                            //call Ktor Client to update the appointment record
                                            CoroutineScope(Dispatchers.IO).launch {

                                                val approveResult: putApplyApproveAppointmentRecordResult =
                                                    KtorClient.putApproveRejectAppointment(
                                                        appointItem.appointID!!,
                                                        approveRejectRecord
                                                    ) //not String message only, but User data class
                                                var message = ""
                                                Log.d(
                                                    "putResult",
                                                    "putResult approveResult: $approveResult"
                                                )

                                                MainScope().launch {
                                                    if (approveResult.acknowledged) {           //success
                                                        message =
                                                            "Approve Success! The record is updated just now."  //The reference code is: ${patchResult.referenceCode}"

                                                        Log.d(
                                                            "Approved Success",
                                                            message
                                                        )

                                                        callKtor.value = false
                                                        openDialog.value = true

                                                    } else {     //error
                                                        message =
                                                            "Approve Failed, please try again later."
                                                        Log.d("Approve failed", message)

                                                        callKtor.value = false
                                                    }
                                                }
                                            }
                                        }


                                        if (openDialog.value) {

                                            var dialogTitle =
                                                if (doctorApprove) "Approve Success!" else "Reject Success!"
                                            var dialogContent =
                                                if (doctorApprove) "Approve Success! The record is updated just now." else "Reject Success! The record is updated just now."

                                            AlertDialog(
                                                onDismissRequest = {
                                                    openDialog.value = false
                                                }) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(
                                                            color = Green20,
                                                            shape = RoundedCornerShape(8.dp)
                                                        )
                                                        .padding(15.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {

                                                    Text(
                                                        text = "$dialogTitle",
                                                        modifier = Modifier
                                                            .align(Alignment.CenterHorizontally)
                                                            .padding(10.dp),
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.Black,
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
                                                        text = "$dialogContent",
                                                        color = Color.Black,
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

                                            //Generate a Firebase Notification to the Patient
                                            //call api to send the notification to the backend server, and backend to patient
                                            CoroutineScope(Dispatchers.IO).launch {
                                                val sendFirebaseNotificationResult: FirebaseNotification =
                                                    KtorClient.sendFirebaseNotification(patientID!!, "Approve") //Approve or Reject

                                                Log.d(
                                                    "sendFirebaseNotificationResult",
                                                    "sendFirebaseNotificationResult: $sendFirebaseNotificationResult"
                                                )
                                            }
                                        }
                                    }

                                }
                                Row() {
                                    HorizontalDivider(
                                        thickness = 1.dp,
                                        color = Green30
                                    )  //section line
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}