package com.example.fyp_medapp_android

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.fyp_medapp_android.ui.theme.Green20
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


@Composable
fun filterDataByDate_Type(healthdataResultValue: List<HealthData>) {
//Sort the data by health data type and date

//    healthdataResultValue.sortedBy { it.recordDateTime }  //convert string to date first?

    for (item in healthdataResultValue) {
        when (item.recordType) {
            "bloodPressure" -> bloodPressureList.add(item)
            "bloodSugar" -> bloodSugarList.add(item)
            "pulse" -> heartRateList.add(item)
            "temperature" -> temperatureList.add(item)
            "bloodOxygenLevel" -> bloodOxygenLevelList.add(item)
            "waistWidth" -> waistWidthList.add(item)
        }
    }

    sortDataFlag = true
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun healthDataScreen(navController: NavHostController) {
    sortDataFlag = false

    bloodPressureList.clear()
    bloodSugarList.clear()
    heartRateList.clear()
    temperatureList.clear()
    bloodOxygenLevelList.clear()
    waistWidthList.clear()

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
                filterDataByDate_Type(healthdataResult.value)
                println("after filtering, $sortDataFlag")

//                Log.d("healthdata screen after calling API", "healthdataResult: $healthdataResult")
//
//                Row() {
//                    Text(text = "View Health Data Record")
//                }

                Row() {
                    //dropdown menu to filter the health data type
                    val context = LocalContext.current
                    val healthData = arrayOf(
                        "Blood Pressure",  //bloodPressure
                        "Blood Sugar",  //bloodSugar
                        "Heart Rate",  //pulse
                        "Temperature",  //temperature
                        "Blood Oxygen Level",  //bloodOxygenLevel
                        "Waist Width"  //waistWidth
                    )
                    var expanded by remember { mutableStateOf(false) }
                    var selectedText by remember { mutableStateOf(healthData[0]) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            }
                        ) {
                            TextField(
                                value = selectedText,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded
                                    )
                                },
                                modifier = Modifier.menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = {
                                    expanded = false
                                    //TODO: call api / filter below(as we fetched all data already)
                                }
                            ) {
                                healthData.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item) },
                                        onClick = {
                                            selectedText = item
                                            expanded = false
                                            Toast.makeText(context, item, Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                //Table to display the sorted data


                if (sortDataFlag) {

                    //call to show the table, TODO: advance
                    //1. add into a list of list
                    //2. call functions
                    //3. do filtering?

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        showDataInTable_byType(bloodPressureList)
                        showDataInTable_byType(bloodSugarList)
                        showDataInTable_byType(heartRateList)
                        showDataInTable_byType(temperatureList)
                        showDataInTable_byType(bloodOxygenLevelList)
                        showDataInTable_byType(waistWidthList)
                    }
                }
            }
            //}
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

    // Each cell of a column must have the same weight.
    val column1Weight = .5f // 50%
    val column2Weight = .5f // 50%

    println("enter showDataInTable_byType")
    if (!targetList.isEmpty()) { //sortDataFlag &&


        println("enter targetList: ${targetList[0].recordType}")

        //Table, not using LazyColumn as it is used in the outter structure
        Column(
            Modifier
//                    .fillMaxSize()
                .padding(16.dp)
        ) {

            //header for each table
            Text(
                text = targetList[0].recordType.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Here is the header setting
            Row(Modifier.background(Green20)) {
                TableCell(text = "Date", weight = column1Weight)
                TableCell(text = "Data", weight = column2Weight)
            }
            // Here are all the lines of your table.
            for (item in targetList) {
                Row(Modifier.fillMaxWidth()) {
                    TableCell(
                        text = item.recordDateTime.toString(),
                        weight = column1Weight
                    )
                    TableCell(
                        text = valueStringConvertor(item),
                        weight = column2Weight
                    )
                }
            }
        }
    }

    println("Table Shown")
}

fun valueStringConvertor(item: HealthData) : String {
    if (item.recordType == "bloodPressure") {
        return " ${item.recordValue1_defaultUnit.toString()} / ${item.recordValue2_defaultUnit.toString()} ${item.recordUnit_Patient.toString()}"

    } else return " ${item.recordValue1_defaultUnit.toString()} ${item.recordUnit_Patient.toString()}"

}