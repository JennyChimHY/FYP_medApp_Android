package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.fyp_medapp_android.ui.theme.FYP_medApp_AndroidTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
data class MedicineInfo(
    val _id: String?,
    val medicineId: String?,
    val medicineName: String?,
    val medicineClass: String?,
    val medicineImageName: String?,
    val specialRemark: String?
)

@Serializable
data class Medicine(
    val _id: String?,
    val issueMedID: String?,
    val userID: String?,
    val medicineId: String?,
    val medicineName: String?,
    val medicineClass: String?,
    val medicineImageName: String?, //DB: Name, Backend: return corr. Image
    val dailyIntake: Int?,
    val eachIntakeAmount: Int?,
    val issueQuantity: Int?,
    val issueDate: String?,
    val specialRemark_patient: String?,
    val reminderTime: Array<String> = arrayOf(),
    val selfNote: String?,
    val medicineInfo: MedicineInfo?
)

@Composable
fun medicineSceen(navController: NavHostController) {
    //ref: ItemView in InventoryApp

    val medicineResult = produceState(
        initialValue = listOf<Medicine>(),
        producer = {
            value = KtorClient.getMedicine(globalLoginInfo.userID) //not String message only, but User data class
        })
    Log.d("medicineScreen after calling API", "medicineResult: $medicineResult")

    Row() {
        Text(text = "View In-taking Medicine Record", modifier = Modifier.padding(16.dp))
    }
    //Display records card-by card
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)) {
        //TODO scrollable Modifier.verticalScroll(rememberScrollState())

        items(medicineResult.value) { medicineItem ->
            Log.d("medicineScreen", "enter each item")
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier
                    .size(width = 300.dp, height = 500.dp)
                    .padding(8.dp)
            ) {
                AsyncImage(
                    //fetch the backend directly, apiDomain is a global var from KtorClient
                    model = apiDomain + "/images/MedApp_medicinePicture/" + medicineItem.medicineInfo?.medicineImageName + ".jpg",
                    contentDescription = null,
                )
                Text(
                    text = medicineItem.medicineId.toString(),
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = medicineItem.medicineInfo?.medicineName.toString(),
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = medicineItem.issueDate.toString(),
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
