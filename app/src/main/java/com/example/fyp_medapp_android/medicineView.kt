package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

var textPadding = 10.dp
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

            //add logout button the the bar
            logoutButton(navController)
        },
        snackbarHost = { },  //lab11
        content = { innerPadding ->
            //display the content of the page
            Column(modifier = Modifier.padding(innerPadding)) {
                val medicineResult = produceState(
                    initialValue = listOf<Medicine>(),
                    producer = {
                        value =
                            KtorClient.getMedicine(globalLoginInfo.userID) //not String message only, but User data class
                    })
                Log.d("medicineScreen after calling API", "medicineResult: $medicineResult")

                Row() {
                    Text(
                        text = "View In-taking Medicine Record",
                        modifier = Modifier.padding(textPadding)
                    )
                }
                //Display records card-by card
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {

                    //TODO scrollable Modifier.verticalScroll(rememberScrollState())

                    items(medicineResult.value) { medicineItem ->
                        Log.d("medicineScreen", "enter each item")
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                            modifier = Modifier
                                .size(width = 300.dp, height = 300.dp)
                                .padding(8.dp)
                        ) {
                            Row() {
                                Column() {
                                    Text(
                                        text = medicineItem.medicineId.toString(),
                                        modifier = Modifier
                                            .padding(textPadding),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        text = medicineItem.medicineInfo?.medicineName.toString(),
                                        modifier = Modifier
                                            .padding(textPadding),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        text = medicineItem.issueDate.toString(),
                                        modifier = Modifier
                                            .padding(textPadding),
                                        textAlign = TextAlign.Start,
                                    )
                                }

                                Column() {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(end = 5.dp),
                                        horizontalArrangement = Arrangement.End
                                    ) {

//                                        Box(  //labeling the class
//                                            modifier = Modifier
//                                                .size(120.dp)
//                                                .background(Green20)
//                                        ) {
//                                            Text(medicineItem.medicineInfo?.medicineClass!!,
//                                                modifier = Modifier
//                                                    .padding(textPadding),
//                                                color = (Color.Black),
//                                                textAlign = TextAlign.End,
//                                            )
//                                        }

                                        AsyncImage(
                                            //fetch the backend directly, apiDomain is a global var from KtorClient
                                            model = apiDomain + "/images/MedApp_medicinePicture/" + medicineItem.medicineInfo?.medicineImageName + ".jpg",
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(120.dp)
                                                .padding(textPadding)
                                        )
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
