package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fyp_medapp_android.ui.theme.Green40
import com.example.fyp_medapp_android.ui.theme.Green50
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime


@OptIn(ExperimentalMaterial3Api::class)
object FutureSelectableDates: SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis > System.currentTimeMillis()  //not for today
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year >= LocalDate.now().year
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun datePickerComponent(canlendarType : String): String {

    lateinit var addDateState: DatePickerState
    if(canlendarType == "healthData") {
        addDateState = rememberDatePickerState(
            initialSelectedDateMillis = Instant.now().toEpochMilli()
        ) //**user input
    } else if (canlendarType == "applyChangeInAppointment") {
        addDateState = rememberDatePickerState(
            initialSelectedDateMillis = Instant.now().toEpochMilli(),
            selectableDates = FutureSelectableDates //disable previous date
        ) //**user input
    }

    var selectedDate: OffsetDateTime? = null

    DatePicker(
        state = addDateState,
        modifier = Modifier
            .width(300.dp)
            .height(500.dp)
    )

    Log.d("dateselection", "selectedDate: $selectedDate")

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