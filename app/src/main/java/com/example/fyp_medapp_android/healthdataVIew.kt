package com.example.fyp_medapp_android

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import kotlin.math.round

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
        }, snackbarHost = { },  //lab11
        content = { innerPadding ->
            //display the content of the page
            Column(modifier = Modifier.padding(innerPadding)) {
                val healthdataResult =
                    produceState(initialValue = listOf<HealthData>(), producer = {
                        value =
                            KtorClient.getHealthData(globalLoginInfo.userID) //not String message only, but User data class
                    })

                println("before filtering, $sortDataFlag")
                filterDataByDate_Type(healthdataResult.value)
                println("after filtering, $sortDataFlag")

//                Log.d("healthdata screen after calling API", "healthdataResult: $healthdataResult")


                Row() {
                    Text(text = "View Health Data Record")
                }

//                //dropdown menu to filter the health data type
//                Row() {
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

                //Table to display the sorted data
                if (sortDataFlag) {

                    //call to show the table, TODO: advance
                    //1. add into a list of list
                    //2. call functions
                    //3. do filtering?

                    var sortedDataList =
                        mutableListOf(  //take the list out and call table function one by one
                            bloodPressureList,
                            bloodSugarList, //cannot update inside list of list
                            heartRateList,
                            temperatureList,
                            bloodOxygenLevelList,
                            waistWidthList
                        )

                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        for (item in sortedDataList) {
                            if (item.size > 0) {
                                println("item: $item")
                                showDataInTable_byType(item)
                            }
                        }
                    }
                }
            }
            //}
        })
}

//Table setting for displaying record
@Composable  //normal table cell
fun RowScope.TableCell(
    text: String,
    weight: Float,
    alignment: TextAlign = TextAlign.Center,
    title: Boolean = false
) {
    Text(
        text = text,
        Modifier
            .weight(weight)
            .padding(10.dp),
        fontWeight = if (title) FontWeight.Bold else FontWeight.Normal,
        textAlign = alignment,
    )
}
@Composable //status cell which will change label colour according to the data
fun RowScope.StatusCell(
    text: String,
    weight: Float,
    alignment: TextAlign = TextAlign.Center,
) {

    val color = when (text) {
        "healthy" -> Color(0xffadf7a4)
        "risk" -> Color(0xfff8deb5)
        else -> Color(0xffffcccf)  //danger
    }
    val textColor = when (text) {
        "healthy" -> Color(0xff00ad0e)
        "risk" -> Color(0xffde7a1d)
        else -> Color(0xffca1e17)
    }

    Text(
        text = text,
        Modifier
            .weight(weight)
            .padding(12.dp)
            .background(color, shape = RoundedCornerShape(50.dp)),
        textAlign = alignment,
        color = textColor
    )
}

@Composable
fun showDataInTable_byType(targetList: List<HealthData>) {

    // Each cell of a column must have the same weight.
    val column1Weight = .35f // 40%
    val column2Weight = .35f // 40%
    val column3Weight = .3f // 20%

    if (!targetList.isEmpty()) { //sortDataFlag &&

        //cannot use lazyColumn (infinite scroll), each table a table column on the same screen
        Column(
            Modifier
//                    .fillMaxSize()
                .padding(16.dp)
        ) {

            //Table Title
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when (targetList[0].recordType.toString()) {
                        "bloodPressure" -> "Blood Pressure"
                        "bloodSugar" -> "Blood Sugar"
                        "pulse" -> "Heart Rate"
                        "temperature" -> "Temperature"
                        "bloodOxygenLevel" -> "Blood Oxygen Level"
                        "waistWidth" -> "Waist Width"
                        else -> "Unknown"
                    }, fontSize = 20.sp, fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(10.dp))

                Button(colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                ), onClick = { /*TODO*/ }) {//call function to pop up add record
                    Image(
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "Add Record",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }


            //Table
            //define table field header
            Row(Modifier.background(Green20)) {
                TableCell(text = "Date", weight = column1Weight)
                TableCell(text = "Data", weight = column2Weight)
                TableCell(text = "Status", weight = column3Weight)
            }

            //table content
            for (content in targetList) {

                val dateTime = content.recordDateTime.toString().split("T")
                val date = dateTime[0]

                // Time conversion lambda function
                val convertTo12HourFormat: (String) -> String = { time24hr ->
                    val hour = time24hr.substring(0, 2).toInt()
                    val minute = time24hr.substring(3, 5)
                    val period = if (hour < 12) "AM" else "PM"

                    val hour12hr = when {
                        hour == 0 -> 12
                        hour > 12 -> hour - 12
                        else -> hour
                    }

                    String.format("%02d:%s %s", hour12hr, minute, period)
                }

                val convertedTime = dateTime[1].let(convertTo12HourFormat) // Using the lambda function

                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = "$date $convertedTime", weight = column1Weight)
                    TableCell(text = valueStringConvertor(content), weight = column2Weight)
                    StatusCell(text = content.healthStatus.toString(), weight = column3Weight)
                }

                Divider(
                            color = Color.LightGray,
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxHeight()
                                .fillMaxWidth()
                )

            }
        }
    }
}

fun valueStringConvertor(item: HealthData): String {

    when (item.recordType) {
        "bloodPressure" -> return "${round(item.recordValue1_defaultUnit!!).toInt()} / ${round(item.recordValue2_defaultUnit!!).toInt()} ${item.recordUnit_Patient.toString()}"
        "temperature" -> {
            if (item.recordUnit_Patient.toString() == "dF") {
                var dFvalue = item.recordValue1_defaultUnit!!.toDouble() * 9 / 5 + 32
                return "$dFvalue \u2109"
            } else {
                return "${item.recordValue1_defaultUnit.toString()} \u2103"
            }
        }
        else -> return "${item.recordValue1_defaultUnit.toString()} ${item.recordUnit_Patient.toString()}"
    }

}