package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appointmentPatientChangeView(appointID: String?) {
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

            Column(modifier = Modifier.padding(innerPadding)) {

                Log.d("Enter", "Enter change view $appointID")
                //call KTor client to get the latest appointment details

                val changeAppointmentResult = produceState(
                    initialValue = listOf<Appointment>(),
                    producer = {
                        value =
                            KtorClient.getAppointment(appointID) //not String message only, but User data class
                    })

                Log.d(
                    "appointmentPatientChangeView",
                    "appointmentResult.value = ${changeAppointmentResult}"
                )

                Row() {
                    Text(text = "Change Appointment Record")
                }

                Row() {
                    Text(text = "Original Record: ")

                    Spacer(modifier = Modifier.padding(10.dp))

                    Text(text = "")
                }
            }
        }
    )
}