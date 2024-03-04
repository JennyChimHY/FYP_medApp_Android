package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.*
import com.example.fyp_medapp_android.ui.theme.Green20
import com.example.fyp_medapp_android.ui.theme.Green40
import com.example.fyp_medapp_android.ui.theme.Green50
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.math.round

@Serializable
data class HealthData(
    val _id: String?,
    val userId: String?,
    var recordDateTime: String?,
    var recordTimeslot: String?,
    val recordType: String?,
    var recordUnit_Patient: String?,
    var recordValue1_defaultUnit: Double?,
    var recordValue2_defaultUnit: Double?,
    var healthStatus: String?,
    var selfNote: String?
)

@Serializable
data class addDeletehealthDataRecordResult(
    val acknowledged: Boolean,
    val insertedId: String
)

//global variables, (or make function filterDataByDate_Type() return list of list?)
var bloodPressureList = mutableListOf<HealthData>()
var bloodSugarList = mutableListOf<HealthData>()
var heartRateList = mutableListOf<HealthData>()
var temperatureList = mutableListOf<HealthData>()
var bloodOxygenLevelList = mutableListOf<HealthData>()
var waistWidthList = mutableListOf<HealthData>()

var sortDataFlag = false
//var updateDataBlock: MutableMap<String, Boolean> = mutableMapOf( //remember { mutableStateOf(false)
//    "bloodPressure" to false,
//    "bloodSugar" to false,
//    "heartRate" to false,
//    "temperature" to false,
//    "bloodOxygenLevel" to false,
//    "waistWidth" to false
//)

var modifierForForm = 5.dp


@Composable
fun filterDataByDate_Type(healthdataResultValue: List<HealthData>) {

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
//    updateDataBlock = mutableMapOf(
//        "bloodPressure" to false,
//        "bloodSugar" to false,
//        "heartRate" to false,
//        "temperature" to false,
//        "bloodOxygenLevel" to false,
//        "waistWidth" to false
//    )

    var updateDataBlock by remember {
        mutableStateOf(
            mutableStateMapOf(
                "bloodPressure" to false,
                "bloodSugar" to false,
                "heartRate" to false,
                "temperature" to false,
                "bloodOxygenLevel" to false,
                "waistWidth" to false
            )
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }

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

//            //add logout button the the bar
//            logoutButton(navController)
        }, snackbarHost = { SnackbarHost(snackbarHostState) },  //lab11
        content = { innerPadding ->
            //display the content of the page
            Column(modifier = Modifier.padding(innerPadding)) {
                val healthdataResult =
                    produceState(initialValue = listOf<HealthData>(), producer = {
                        value =
                            KtorClient.getHealthData(targetUserID) //not String message only, but User data class
                    })

//                println("before filtering, $sortDataFlag")
                filterDataByDate_Type(healthdataResult.value)
//                println("after filtering, $sortDataFlag")

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

                    //call to show the table
                    //1. add into a list of list
                    //2. call functions

                    var sortedDataList =
                        mutableListOf(  //take the list out and call table function one by one
                            bloodPressureList, bloodSugarList, //cannot update inside list of list
                            heartRateList, temperatureList, bloodOxygenLevelList, waistWidthList
                        )

                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        for (item in sortedDataList) {
                            if (item.size > 0) {
                                println("item: $item")
                                showDataInGraphTable_byType(
                                    item,
                                    snackbarHostState,
                                    updateDataBlock
                                )

                                HorizontalDivider(
                                    thickness = 4.dp,
                                    color = sectionBorderColor
                                )  //section line
                            }
                        }
                    }
                }
            }
            //}
        })
}


var tableFontSize = 15.sp
var tablePadding = 5.dp

//Table setting for displaying record
@Composable  //normal table cell
fun RowScope.TableCell(
    text: String, weight: Float, alignment: TextAlign = TextAlign.Center, title: Boolean = false
) {
    Text(
        text = text,
        Modifier
            .weight(weight)
            .padding(tablePadding),
        fontWeight = if (title) FontWeight.Bold else FontWeight.Normal,
        fontSize = tableFontSize,
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
            .padding(tablePadding)
            .background(color, shape = RoundedCornerShape(50.dp)),
        fontSize = tableFontSize,
        textAlign = alignment,
        color = textColor
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun showDataInGraphTable_byType(
    targetList: List<HealthData>,
    snackbarHostState: SnackbarHostState, updateDataBlock: MutableMap<String, Boolean>
) {

    // Each cell of a column must have the same weight.
    val column1Weight = .4f // 40%
    val column2Weight = .25f // 25%
    val column3Weight = .3f // 30%
    val column4Weight = .05f // 5%

    var type = targetList[0].recordType.toString() //for update record block

    val coroutineScope = rememberCoroutineScope()  //for delete record
//    var localContext = LocalContext.current
    var openDialog = remember { mutableStateOf(false) }

    if (!targetList.isEmpty()) { //sortDataFlag &&

        //cannot use lazyColumn (infinite scroll), each table a table column on the same screen
        Column(
            Modifier
//                    .fillMaxSize()
                .padding(10.dp)
        ) {

            //Table Title
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = when (type) {
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
                ), onClick = {
                    updateDataBlock[type] = true
                    Log.d("updateDataBlock", "updateDataBlock: ${updateDataBlock[type]}")
                }) {//call function to pop up add record (overlay)
                    Image(
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "Add Record",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            //update record block
            if (updateDataBlock[type] == true) {
                addDataBlockDialog(type, snackbarHostState, updateDataBlock)
            }

            //Graph presentation, pass targetList
            displayLineChart(targetList)

            //Table
            //define table field header
            Row(Modifier.background(Green20)) {
                TableCell(text = "Date", weight = column1Weight)
                TableCell(text = "Data", weight = column2Weight)
                TableCell(text = "Status", weight = column3Weight)
                TableCell(text = "", weight = column4Weight)
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

                val convertedTime =
                    dateTime[1].let(convertTo12HourFormat) // Using the lambda function

                Row(Modifier.fillMaxWidth()) {
                    TableCell(
                        text = "$date $convertedTime \n ${content.recordTimeslot}",
                        weight = column1Weight
                    )
                    TableCell(text = valueStringConvertor(content), weight = column2Weight)
                    StatusCell(text = content.healthStatus.toString(), weight = column3Weight)

                    //Button Cell
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                        ), onClick = {  //not in a UI thread
                            openDialog.value = true
                        }) {
                        Image(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "Delete Record",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    var confirmDelete = remember { mutableStateOf(false) }

                    if (openDialog.value) {
                        //double confirm there, no need CoroutineScope(Dispatchers.Main).launch {//define UI scope

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
                                    .padding(start = 5.dp, end = 5.dp)
                            ) {
                                Text(
                                    text = "Are you sure to delete this record?",
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    fontWeight = FontWeight.Bold
                                )

                                Divider(
                                    color = Color.LightGray,
                                    modifier = Modifier
                                        .height(1.dp)
                                        .fillMaxHeight()
                                        .fillMaxWidth()
                                )

                                //button row
                                Row(
                                    modifier = Modifier.padding(all = 8.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
//                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            confirmDelete.value = true
                                            openDialog.value = false
                                        }
                                    ) {
                                        Text("Delete")
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
//                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            confirmDelete.value = false
                                            openDialog.value = false
                                        }
                                    ) {
                                        Text("Cancel")
                                    }
                                }
                            }
                        }
                    }
//                    println("after if value $confirmDelete.value")
                    if (confirmDelete.value) {
                        //call KTor client to delete the record

                        println("To delete... ${content._id}")  //recordID
                        coroutineScope.launch(Dispatchers.IO) { //define call KtorClient scope

                            val deleteResult: addDeletehealthDataRecordResult =
                                KtorClient.deleteHealthData(content._id!!) //not String message only, but User data class
                            var message = ""
                            Log.d("deleteResult", "deleteResult: $deleteResult")
                            if (deleteResult.acknowledged) {           //success
                                message =
                                    "Delete Success."

                                Log.d("Delete Success", message)
                            } else {     //error
                                println("delete false")
                                println(deleteResult)
                                message = "Delete Failed"
                                Log.d("Delete failed", message)
                            }
                        }
                    }


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
        "bloodPressure" -> return "${round(item.recordValue1_defaultUnit!!).toInt()}/${round(item.recordValue2_defaultUnit!!).toInt()} ${item.recordUnit_Patient.toString()}"
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun addDataBlockDialog(
    type: String,
    snackbarHostState: SnackbarHostState,
    updateDataBlock: MutableMap<String, Boolean>
) { //

//        var newRecordTimeslot: String = "abc"  //OLD DATA, to delete
    var addhealthData by remember {
        mutableStateOf( //mutableStateOf
            HealthData(
                _id = null,
                userId = targetUserID,
                recordDateTime = "",
                recordTimeslot = "",
                recordType = type,
                recordUnit_Patient = null,
                recordValue1_defaultUnit = 0.0,
                recordValue2_defaultUnit = 0.0,
                healthStatus = null,
                selfNote = null
            )
        )
    }

    var addDate by remember { mutableStateOf("") }
    var addTime by remember { mutableStateOf("") }
    var addTimeslot by remember { mutableStateOf("") }
    var addValue by remember { mutableStateOf("") } //data class
    var addValue2 by remember { mutableStateOf("") }
    var addSelfNotes by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    //Method 1: Simple hidden Row --> press "ok" to close the row, validate and update to server
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Green20, shape = RoundedCornerShape(8.dp)
            )
            .padding(start = 3.dp, end = 3.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Add " + when (type) {
                    "bloodPressure" -> "Blood Pressure"
                    "bloodSugar" -> "Blood Sugar"
                    "pulse" -> "Heart Rate"
                    "temperature" -> "Temperature"
                    "bloodOxygenLevel" -> "Blood Oxygen Level"
                    "waistWidth" -> "Waist Width"
                    else -> "Unknown"
                } + " record",
                modifier = Modifier.align(Alignment.Start),
                fontWeight = FontWeight.Bold
            )

            Text(text = "Date: ", modifier = Modifier.align(Alignment.Start))
            addDate = datePickerComponent()
//            println("addDate: $addDate")

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Time: ", modifier = Modifier.align(Alignment.Start))

            Row() {
                addTime = timePickerComponent()
//                println("addTime: $addTime")
            }

            Spacer(modifier = Modifier.height(8.dp))


            Text(text = "Timeslot: ", modifier = Modifier.align(Alignment.Start))

            Row() {

                val radioOptions_timeSlot = listOf("Before Breakfast", "After Breakfast", "Before Lunch", "After Lunch", "Before Dinner", "After Dinner", "Before Sleep", "Other")
                var selectedOption by remember { mutableStateOf(radioOptions_timeSlot[0]) }

                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
                        radioOptions_timeSlot.forEach { unitChoice ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = (unitChoice == selectedOption),
                                    onClick = { selectedOption = unitChoice }
                                )
                                Text(
                                    text = unitChoice,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
//                    }
                }
                addTimeslot = selectedOption
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row() {

                Text(text = "Value: ")

                OutlinedTextField( //TextField  //TODO: make outlinetextfield reusable??
//                    label = { Text("Input Value") },
                    textStyle = TextStyle.Default.copy(fontSize = 23.sp),
                    singleLine = true,
                    value = addValue,
                    onValueChange = { addValue = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .width(90.dp)
                        .height(55.dp)
                )

                if (type == "bloodPressure") {  //TODO: self-defined type has 2 values
                    Text(text = "/", fontSize = 38.sp)
                    OutlinedTextField( //TextField
//                    label = { Text("Input Value") },
                        textStyle = TextStyle.Default.copy(fontSize = 23.sp),
                        singleLine = true,
                        value = addValue2,
                        onValueChange = { addValue2 = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .width(90.dp)
                            .height(55.dp)
                    )

                } else addValue2 = "0.0"

                //unit
                addhealthData.recordUnit_Patient = when (type) {
                    "bloodPressure" -> "mmHg"
                    "bloodSugar" -> "mg/dL"
                    "pulse" -> "bpm"
                    "temperature" -> "dC"  //default
                    "bloodOxygenLevel" -> "%"
                    //"waistWidth" -> "Waist Width" //to be developed
                    else -> "Unknown" //TODO: self-defined type
                }

                if (type == "temperature") {
                    //radio options
                    val radioOptions_temperature = listOf("\u2103", "\u2109")
                    var selectedOption by remember { mutableStateOf(radioOptions_temperature[0]) }

                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            radioOptions_temperature.forEach { unitChoice ->
                                RadioButton(
                                    selected = (unitChoice == selectedOption),
                                    onClick = { selectedOption = unitChoice }
                                )
                                Text(
                                    text = unitChoice,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }

                    addhealthData.recordUnit_Patient = when (selectedOption) {
                        "\u2103" -> "dC"
                        "\u2109" -> "dF"
                        else -> ""
                    }

                    Log.d(
                        "addhealthData.recordUnit_Patient",
                        ": ${addhealthData.recordUnit_Patient}"
                    )

//                    //if dF -> convert the data to dC for DB default
                } else {
                    Text(text = " ${addhealthData.recordUnit_Patient}")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                Modifier.padding(5.dp)
            ) {
                Text(text = "Self Notes: ")

                OutlinedTextField( //TextField
//                    label = { Text("Self Notes:") },
                    textStyle = TextStyle.Default.copy(fontSize = 28.sp),
                    singleLine = true,
                    value = addSelfNotes,
                    onValueChange = { addSelfNotes = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )
            }

            Row(
                Modifier.padding(5.dp)
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                    ), onClick = {

                        Log.d("updateDataBlock", "updateDataBlock: $updateDataBlock")

                        var checkTimeFormat = addTime.split(":")
                        //TODO: Improve the logic
                        if (checkTimeFormat[0].length == 1) {
                            if (checkTimeFormat[1].length == 1) {
                                addTime = "0${checkTimeFormat[0]}:0${checkTimeFormat[1]}"
                            } else {
                                addTime = "0${checkTimeFormat[0]}:${checkTimeFormat[1]}"
                            }
                        } else if (checkTimeFormat[1].length == 1) {
                            addTime = "${checkTimeFormat[0]}:0${checkTimeFormat[1]}"
                        }

                        addhealthData.recordDateTime =
                            "${addDate}T${addTime}:00.000Z" //2021-09-01T12:00:00.000Z
                        addhealthData.recordTimeslot = addTimeslot
                        var addValuetmp: Double = 0.0

                        if (addValue != null) {
                            addValuetmp = addValue.toDouble()
                        }
                        //Temperature type Unit conversion
                        if (addhealthData.recordUnit_Patient == "dF" && addValuetmp > 0.0) {
                            addValuetmp = (5.0 / 9.0) * (addValuetmp!! - 32.0)
//                            println("after conversion in if: ${addValuetmp}")
                        }

                        addhealthData.recordValue1_defaultUnit = addValuetmp
                        addhealthData.recordValue2_defaultUnit = addValue2?.toDouble()
                        addhealthData.selfNote = addSelfNotes

                        var checkAddhealthData = validationCheckUpdate(addhealthData)

                        if (checkAddhealthData != null) {
                            println("validation check and Ktor passed: $checkAddhealthData")
                            //call KtorClient to update the data by api if valid input

                            coroutineScope.launch {

                                val addResult: addDeletehealthDataRecordResult =
                                    KtorClient.addHealthData(checkAddhealthData) //not String message only, but User data class
                                var message = ""
                                Log.d("addResult", "addResult: $addResult")
                                if (addResult.acknowledged) {           //success
                                    message =
                                        "Added Success."

                                    snackbarHostState.showSnackbar(message)

                                    Log.d("Added Success", message)

                                } else {     //error
//                                    println("add false")
//                                    println(addResult)
                                    message = "Added Failed"
                                    Log.d("Added failed", message)
                                }

                                updateDataBlock[type] = false //UI thread: close the block
                            }
                        } else {
                            println("validation check failed")
                        }

                    }) {//call function to pop up add record (overlay)
                    Image(
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "Submit Record",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

fun validationCheckUpdate(
    addhealthData: HealthData
): HealthData? {
    //validation, invalid -> error message

    //validation rules

    //1. Input not null
    //For type with 2 values  TODO: self-defined type has 2 units
    if (addhealthData.recordType == "bloodPressure" && addhealthData.recordValue2_defaultUnit == 0.0) {
        return null
    }

    if (addhealthData.recordUnit_Patient == "" || addhealthData.recordDateTime == "" || addhealthData.recordTimeslot == ""
        || addhealthData.recordValue1_defaultUnit == 0.0
    ) {
        //message: input incomplete
        return null
    }

    //2. Within the Range, 3. health status
    // min and max rules, will mapping / array / direct constant?
    var minRange = 0.0
    var maxRange = 0.0
    var minRange_high = 0.0  //for data type with 2 values
    var maxRange_high = 0.0

    var status_risky = 0.0
    var status_danger = 0.0
    var status_risky_high = 0.0 //for data type with 2 values
    var status_danger_high = 0.0

    Log.d("addhealthData", "before addhealthData: $addhealthData")

    when (addhealthData.recordType) {
        "bloodPressure" -> {
            minRange = 60.0
            maxRange = 110.0
            minRange_high = 80.0
            maxRange_high = 150.0

            status_risky = 81.0
            status_danger = 90.0
            status_risky_high = 120.0
            status_danger_high = 140.0
        }
        "bloodSugar" -> {
            minRange = 3.0
            maxRange = 8.0
            status_risky = 5.6
            status_danger = 6.9
        }
        "pulse" -> {
            minRange = 50.0
            maxRange = 185.0
            status_risky = 100.0
            status_danger = 120.0
        }
        "temperature" -> {
            minRange = 34.0
            maxRange = 45.0
            status_risky = 37.3 //more than
            status_danger = 38.0
        }
        "bloodOxygenLevel" -> {
            minRange = 70.0
            maxRange = 100.0
            status_risky = 92.0 //less than
            status_danger = 88.0 //less than
        }
        "waistWidth" -> {
            minRange = 20.0
            maxRange = 110.0
            status_risky = 100.0
            status_danger = 100.0
        }
        else -> {  //TODO: self-defined type
            minRange = 0.0
            maxRange = 100.0
            status_risky = 150.0
            status_danger = 150.0
        }
    }

    //check the value is within the range and input the health status
    if (addhealthData.recordValue2_defaultUnit == 0.0) { //only 1 value
        if (addhealthData.recordValue1_defaultUnit!! < minRange || addhealthData.recordValue1_defaultUnit!! > maxRange) {
            //msg: out of range
            return null
        } else if (addhealthData.recordValue1_defaultUnit!! > status_danger) {
            addhealthData.healthStatus = "danger"
        } else if (addhealthData.recordValue1_defaultUnit!! > status_risky) {
            addhealthData.healthStatus = "risk"
        } else {
            addhealthData.healthStatus = "healthy"
        }
    } else { //2 values
        if (addhealthData.recordValue1_defaultUnit!! < minRange || addhealthData.recordValue1_defaultUnit!! > maxRange
            || addhealthData.recordValue2_defaultUnit!! < minRange_high || addhealthData.recordValue2_defaultUnit!! > maxRange_high
        ) {
            //msg: out of range
            return null
        } else if (addhealthData.recordValue1_defaultUnit!! > status_danger || addhealthData.recordValue2_defaultUnit!! > status_danger_high) {
            addhealthData.healthStatus = "danger"
        } else if (addhealthData.recordValue1_defaultUnit!! > status_risky || addhealthData.recordValue2_defaultUnit!! > status_risky_high) {
            addhealthData.healthStatus = "risk"
        } else {
            addhealthData.healthStatus = "healthy"
        }
    }

    Log.d("addhealthData", "after addhealthData: $addhealthData")

    return addhealthData //temp.string msg -> validate fail ; add failed ; add success

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun displayLineChart(targetList: List<HealthData>) {

    var type = targetList[0].recordType.toString()
    var value1 = mutableListOf<Float>()
    var value2 = mutableListOf<Float>()
    var date = mutableListOf<String>()

    var maxVal = 100
    var division = 4

    when (type) {
        "bloodPressure" -> {
            maxVal = 180
            division = 9
        }
        "bloodSugar" -> {
            maxVal = 15
            division = 15
        }
        "pulse" -> {
            maxVal = 150
            division = 15
        }
        "temperature" -> {
            maxVal = 45
            division = 5
        }
        "bloodOxygenLevel" -> {
            maxVal = 100
            division = 4
        }
        "waistWidth" -> {
            maxVal = 70
            division = 7
        }
        else -> {
            //TODO: self-defined type
            maxVal = 100
            division = 4
        }
    }

    for (item in targetList) {
        date.add(item.recordDateTime.toString().split("T")[0])
        value1.add(item.recordValue1_defaultUnit!!.toFloat())
        if (item.recordValue2_defaultUnit != null) {
            value2.add(item.recordValue2_defaultUnit!!.toFloat())
        }
    }

//    var pointTMP = 52.22  //for testing
//    var pointsData = mutableListOf(Point(0f, pointTMP.toFloat()), Point(1f, 55.56f), Point(2f, 54.44f), Point(3f, 65.56f), Point(4f, 10f))
//    Log.d("pointsData", "pointsData: $pointsData")

    var pointsData = mutableListOf<Point>()
    pointsData.add(Point(0f, 0f))       //add the min. value
    for (i in 0..value1.size - 1) {
        var pointY = value1[i]
        var pointX = i + 1
        pointsData.add(Point(pointX.toFloat(), pointY))
//        Log.d("pointsData", "pointsData: $pointsData")
    }
    pointsData.add(Point((value1.size + 1).toFloat(), maxVal.toFloat()))  //add the max. value

    val xAxisData = AxisData.Builder().axisStepSize(100.dp).backgroundColor(Color.Transparent)
        .steps(pointsData.size - 1).labelData { i ->
            i.toString()
        }  //date[i] label diaply date
        .labelAndAxisLinePadding(15.dp).build()

    val yAxisData = AxisData.Builder().steps(division).backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(20.dp).labelData { i ->
            val yScale = maxVal / division
            (i * yScale).toString()
        }.build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(),
                    IntersectionPoint(),
                    SelectionHighlightPoint(),
                    ShadowUnderLine(),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(),
        backgroundColor = Color.Transparent
    )


    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp), lineChartData = lineChartData
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun datePickerComponent(): String {
    val addDateState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    ) //**user input
    var selectedDate: OffsetDateTime? = null

    DatePicker(
        state = addDateState,
        modifier = Modifier
            .width(300.dp)
            .height(500.dp)
    )
    selectedDate = addDateState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atOffset(ZoneOffset.UTC)
    }

//    println("selectedDate: $selectedDate")

    return selectedDate.toString().split("T")[0]
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun timePickerComponent(): String {

    val addTimeState = rememberTimePickerState()
    var selectedTime = ""

    TimePicker(
        state = addTimeState,
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        colors = TimePickerDefaults.colors(
            clockDialColor = Color.Transparent,
            timeSelectorSelectedContainerColor = Green40,
            selectorColor = Green50,
        )
    )
//    Text(text = "Time is ${addTimeState.hour} : ${addTimeState.minute}")
    selectedTime = "${addTimeState.hour}:${addTimeState.minute}"

    return selectedTime
}
