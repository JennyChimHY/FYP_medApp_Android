package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.fyp_medapp_android.ui.theme.FYP_medApp_AndroidTheme
import com.example.fyp_medapp_android.ui.theme.Green20
import com.example.fyp_medapp_android.ui.theme.Green50
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.*

var textPadding = 10.dp
var boxTextSize = 15.sp

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun medicineSceen(navController: NavHostController) {
    //ref: ItemView in InventoryApp
    Scaffold(
        //diaplay the header of each page
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Medicine",
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
        snackbarHost = { },  //lab11
        content = { innerPadding ->
            //display the content of the page

//            val targetUserID = if (globalLoginInfo.userRole == "patient") {
//                globalLoginInfo.userID
//            } else {
//                globalLoginInfo.patientConnection[0].patientID
//            }
            //changed to global var

            var convertedDate =
                mutableListOf<String>()  //TODO: difference between listOf and mutableListOf and List<String>?
            Column(modifier = Modifier.padding(innerPadding)) {
                val medicineResult = produceState(
                    initialValue = listOf<Medicine>(),
                    producer = {
                        value =
                            KtorClient.getMedicine(targetUserID) //not String message only, but User data class
                    })
                Log.d("medicineScreen after calling API", "medicineResult: $medicineResult")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()  //BUG: right padding not working, Column innerPadding?
                        .padding(10.dp)
                ) {
                    Text(
                        text = "In-taking Medicine",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                HorizontalDivider(
                    thickness = 2.dp,
                    color = sectionBorderColor
                )  //section line

                //Display records card-by card
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {

                    //TODO scrollable Modifier.verticalScroll(rememberScrollState())

                    items(medicineResult.value) { medicineItem ->
                        Log.d("medicineScreen", "enter each item")

                        convertedDate = dateConversion(medicineItem.issueDate!!)

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

                                //a column with photo, name, box and a row (consisting of 2 column storing heading and info)
                                Column(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(10.dp)
                                ) {
                                    AsyncImage(
                                        //fetch the backend directly, apiDomain is a global var from KtorClient
                                        model = apiDomain + "/images/MedApp_medicinePicture/" + medicineItem.medicineInfo?.medicineImageName + ".jpg",
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(150.dp)
//                                            .clip(RoundedCornerShape(8))
                                            .align(Alignment.CenterHorizontally)
                                    )

                                    Spacer(modifier = Modifier.size(10.dp))

                                    Text(
                                        text = "${medicineItem.medicineInfo?.medicineName.toString()}",
                                        textAlign = TextAlign.Start,
                                        fontSize = 25.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.size(10.dp))

                                    Box(  //Class Label
                                        modifier = Modifier
                                            .size(120.dp, 40.dp)
                                            .background(
                                                color = Green20,
                                                shape = RoundedCornerShape(8.dp))
                                    ) {
                                        Text(
                                            medicineItem.medicineInfo?.medicineClass!!,
                                            fontSize = boxTextSize,
                                            color = (Color.Black),
                                            modifier = Modifier.align(Alignment.Center),
                                            maxLines = 2
                                        )
                                    }

                                    Spacer(modifier = Modifier.size(10.dp))

                                    Row() {//Data Content
                                        Column(
                                            modifier = Modifier
                                                .weight(6f) // Take 50% of the available width
                                                .fillMaxHeight()
                                                .padding(bottom = 5.dp)
                                        ) {
                                            Text(
                                                text = "Daily Intake:",
                                                textAlign = TextAlign.Start,
                                                fontSize = 20.sp
                                            )
                                            Text(
                                                text = "Each Intake:",
                                                textAlign = TextAlign.Start,
                                                fontSize = 20.sp
                                            )

//                                            if (medicineItem.medicineInfo?.specialRemark != null || medicineItem.specialRemark_patient != null) {
//                                                Text(
//                                                    text = "Remarks: ${medicineItem.medicineInfo?.specialRemark?.toString()}, ${medicineItem.specialRemark_patient?.toString()}",
//                                                    textAlign = TextAlign.Start,
//                                                    fontSize = 20.sp
//                                                )
//                                            }

                                            if (medicineItem.selfNote != null) {
                                                Text(
                                                    text = "Self note:",
                                                    textAlign = TextAlign.Start,
                                                    fontSize = 20.sp
                                                )
                                            }

                                            Spacer(modifier = Modifier.size(8.dp))

                                            Text(
                                                text = "Issue Quantity:",
                                                fontSize = 15.sp,
                                                textAlign = TextAlign.Start,
                                            )
                                            Text(
                                                text = "Issue Date:",
                                                fontSize = 15.sp,
                                                textAlign = TextAlign.Start,
                                            )
                                        }


                                        Column(
                                            modifier = Modifier
                                                .weight(4f) // Take 50% of the available width
                                                .fillMaxHeight()
                                                .padding(5.dp)
                                        ) {
                                            Text(
                                                text = "${medicineItem.dailyIntake.toString()}",
                                                textAlign = TextAlign.Start,
                                                fontSize = 20.sp
                                            )
                                            Text(
                                                text = "${medicineItem.eachIntakeAmount.toString()}",
                                                textAlign = TextAlign.Start,
                                                fontSize = 20.sp
                                            )
//                                        if (medicineItem.medicineInfo?.specialRemark != null || medicineItem.specialRemark_patient != null) {
//                                            Text(
//                                                text = "${medicineItem.medicineInfo?.specialRemark?.toString()}, ${medicineItem.specialRemark_patient?.toString()}",
//                                                textAlign = TextAlign.Right,
//                                                fontSize = 20.sp
//                                            )
//                                        }

                                            if (medicineItem.selfNote != null) {
                                                Text(
                                                    text = "${medicineItem.selfNote}",
                                                    textAlign = TextAlign.Start,
                                                    fontSize = 20.sp
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Text(
                                                text = "${medicineItem.issueQuantity.toString()}",
                                                fontSize = 15.sp,
                                                textAlign = TextAlign.Start,
                                            )
                                            Text(
                                                text = "${convertedDate[0]}",
                                                fontSize = 15.sp,
                                                textAlign = TextAlign.Start,
                                            )
                                        }
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

//dateConversion: for all date conversion in medicine, appointment, healthData
fun dateConversion(dateString: String): MutableList<String> {
    //convert the date format from "2021-08-01T00:00:00.000Z" to "2021-08-01" and time to "00:00:00"
    //Remarks: Return String only, Split is okay rather than Date.now() blah blah blah

    var convertedDate =
        dateString.split("T").toMutableList()  //convertedDate[0]: Date; convertedDate[1]: Time
    convertedDate[1] = convertedDate[1].substring(0, 8)  //remove the .000Z

    return convertedDate

    //https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.js/-date/-init-.html Real Date data type

}
