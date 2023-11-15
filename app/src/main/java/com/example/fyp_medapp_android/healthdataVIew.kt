package com.example.fyp_medapp_android

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

@Serializable
data class HealthData(
    val _id: String?,
    val userId: String?,
    val recordDateTime: String?,
    val recordTimeslot: String?,
    val recordType: String?,
    val recordUnit_Patient: String?,
    val recordValue1_defaultUnit: Double?,
    val recordValue2_defaultUnit: Double?,
    val healthStatus: String?,
    val selfNote: String?
)

//global veriables, (or make function filterDataByDate_Type() return list of list?)
var bloodPressureList = mutableListOf<HealthData>()
var bloodSugarList = mutableListOf<HealthData>()
var heartRateList = mutableListOf<HealthData>()
var temperatureList = mutableListOf<HealthData>()
var bloodOxygenLevelList = mutableListOf<HealthData>()
var waistWidthList = mutableListOf<HealthData>()

//var sortedDataList = mutableListOf(  //take the list out and call table function one by one
//    bloodPressureList,
//    bloodSugarList, //cannot update inside list of list
//    heartRateList,
//    temperatureList,
//    bloodOxygenLevelList,
//    waistWidthList
//)

var sortDataFlag = false

fun showTable_filterDataByDate_Type(healthdataResultValue: List<HealthData>) {
//Sort the data by health data type and date

    println("enter filtering")
//    healthdataResultValue.sortedBy { it.recordDateTime }  //convert string to date first?

    for (item in healthdataResultValue) {
//        println("item: ${item.recordType}")
        when (item.recordType) {
            "bloodPressure" -> bloodPressureList.add(item)
            "bloodSugar" -> bloodSugarList.add(item)
            "pulse" -> heartRateList.add(item)
            "temperature" -> temperatureList.add(item)
            "bloodOxygenLevel" -> bloodOxygenLevelList.add(item)
            "waistWidth" -> waistWidthList.add(item)
        }
    }

    //call to show the table


//    println(sortedDataList)
    println("exit filtering")
    sortDataFlag = true
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun healthDataScreen(navController: NavHostController) {
    sortDataFlag = false
    Scaffold(
        //diaplay the header of each page
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Health Data",
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
                val healthdataResult = produceState(
                    initialValue = listOf<HealthData>(),
                    producer = {
                        value =
                            KtorClient.getHealthData(globalLoginInfo.userID) //not String message only, but User data class
                    })

                println("before filtering, $sortDataFlag")
                showTable_filterDataByDate_Type(healthdataResult.value)
                println("after filtering, $sortDataFlag")

//                Log.d("healthdata screen after calling API", "healthdataResult: $healthdataResult")
//
//                Row() {
//                    Text(text = "View Health Data Record")
//                }

//                Row() {
//                    //dropdown menu to filter the health data type
//                    val context = LocalContext.current
//                    val healthData = arrayOf(
//                        "Blood Pressure",  //bloodPressure
//                        "Blood Sugar",  //bloodSugar
//                        "Heart Rate",  //pulse
//                        "Temperature",  //temperature
//                        "Blood Oxygen Level",  //bloodOxygenLevel
//                        "Waist Width"  //waistWidth
//                    )
//                    var expanded by remember { mutableStateOf(false) }
//                    var selectedText by remember { mutableStateOf(healthData[0]) }
//
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(32.dp)
//                    ) {
//                        ExposedDropdownMenuBox(
//                            expanded = expanded,
//                            onExpandedChange = {
//                                expanded = !expanded
//                            }
//                        ) {
//                            TextField(
//                                value = selectedText,
//                                onValueChange = {},
//                                readOnly = true,
//                                trailingIcon = {
//                                    ExposedDropdownMenuDefaults.TrailingIcon(
//                                        expanded = expanded
//                                    )
//                                },
//                                modifier = Modifier.menuAnchor()
//                            )
//
//                            ExposedDropdownMenu(
//                                expanded = expanded,
//                                onDismissRequest = {
//                                    expanded = false
//                                    //TODO: call api / filter below(as we fetched all data already)
//                                }
//                            ) {
//                                healthData.forEach { item ->
//                                    DropdownMenuItem(
//                                        text = { Text(text = item) },
//                                        onClick = {
//                                            selectedText = item
//                                            expanded = false
//                                            Toast.makeText(context, item, Toast.LENGTH_SHORT)
//                                                .show()
//                                        }
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }

                //TODO: Table to display the sorted data
//timer to extend the waiting time
//                Thread.sleep(5_000)

//                Row() {


                if (sortDataFlag) {
                    println("sortDataFlag is true, enter table")

                    showDataInTable_byType()
                }
//                }
            }
        }
    )
}

//Table setting for displaying record
@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@Composable
fun showDataInTable_byType(targetList: List<HealthData>) {

//    // Just a fake data... a Pair of Int and String
//    val tableData = (1..100).mapIndexed { index, item ->
//        index to "Item $index"
//    }
    // Each cell of a column must have the same weight.
    val column1Weight = .5f // 50%
    val column2Weight = .5f // 50%

    println("enter table vaildation")
    if (!targetList.isEmpty()) { //sortDataFlag &&
        //for loop here
        Row() {
            for (targetList in sortedDataList) {

                if (!targetList.isEmpty()) {
                    println("enter targetList loop: ${targetList[0].recordType}")

                    // The LazyColumn will be our table. Notice the use of the weights below
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {

                    //header for each table
//                    Text(
//                        text = targetList[0].recordType.toString(),
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold
//                    )

                        // Here is the header setting
                        item {
                            Row(Modifier.background(Color.Gray)) {
                                TableCell(text = "Date", weight = column1Weight)
                                TableCell(text = "Data", weight = column2Weight)
                            }
                        }
                        // Here are all the lines of your table.
                        items(targetList) { item ->
                            Row(Modifier.fillMaxWidth()) {
                                println(item.recordDateTime.toString())
                                TableCell(
                                    text = item.recordDateTime.toString(),
                                    weight = column1Weight
                                )
                                TableCell(
                                    text = item.recordValue1_defaultUnit.toString(),  //80"/"100 for blood pressure
                                    weight = column2Weight
                                )
                            }
                        }
                    }


//
//                    //table
//                    LazyColumn(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier.padding(16.dp)
//                    ) {
//                        //TODO? scrollable Modifier.verticalScroll(rememberScrollState())
//
//                        items(targetList) { healthdataItem ->
//                            Card(
//                                colors = CardDefaults.cardColors(
//                                    containerColor = MaterialTheme.colorScheme.primary,
//                                ),
//                                modifier = Modifier
//                                    .size(width = 300.dp, height = 200.dp)
//                                    .padding(8.dp)
//                            ) {
//                                Text(
//                                    text = "Patient's Name:" + globalLoginInfo.lastName + " " + globalLoginInfo.firstName,
//                                    modifier = Modifier
//                                        .padding(16.dp),
//                                    textAlign = TextAlign.Center,
//                                )
//                                Text(
//                                    text = healthdataItem.recordType.toString(),
//                                    modifier = Modifier
//                                        .padding(16.dp),
//                                    textAlign = TextAlign.Center,
//                                )
//                            }
//                        }
//                    }
//                    println("Table Shown")
//                }
                }
            }
        }
    }
}
