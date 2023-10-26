package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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

@Composable
fun appointmentScreen(navController: NavHostController) {

    val appointmentResult = produceState(
        initialValue = listOf<Appointment>(),
        producer = {
            value = KtorClient.getAppointment(globalLoginInfo.userID) //not String message only, but User data class
        })
    Log.d("appointScreen after calling API", "appointmentResult: $appointmentResult")

    Row() {
        Text(text = "View Appointment Record")
    }
    //Display records card-by card
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)) {
        //TODO scrollable Modifier.verticalScroll(rememberScrollState())

        items(appointmentResult.value) { appointItem ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier
                    .size(width = 300.dp, height = 200.dp)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Patient's Name:" + globalLoginInfo.lastName + " " + globalLoginInfo.firstName,
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = appointItem.appointPlace.toString(),
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}