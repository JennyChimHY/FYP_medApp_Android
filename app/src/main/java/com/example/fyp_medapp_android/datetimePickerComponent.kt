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
import java.time.*


@OptIn(ExperimentalMaterial3Api::class)
object FutureSelectableDates : SelectableDates {
//    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
//        return utcTimeMillis > System.currentTimeMillis()  //not for today
//    }

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        // Convert utcTimeMillis to LocalDate and compare with today's date
        val date = Instant.ofEpochMilli(utcTimeMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val today = LocalDate.now()
        return date.isAfter(today)  // Return true if the date is after today
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year >= LocalDate.now().year
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun datePickerComponent(canlendarType: String): String {

    val addDateState: DatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    ) //initialize

    val addDateState2: DatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli(),
        selectableDates = FutureSelectableDates
    ) //for apply appointment

    var selectedDate: OffsetDateTime? = addDateState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atOffset(ZoneOffset.UTC)
    }

//    var selectedDate2: OffsetDateTime? = null

    if (canlendarType == "applyChangeInAppointment") {
        selectedDate = addDateState2.selectedDateMillis?.let {
            Instant.ofEpochMilli(it).atOffset(ZoneOffset.UTC).plusDays(1)  //not for today
        }
    }
    DatePicker(
        state = if (canlendarType == "applyChangeInAppointment") addDateState2 else addDateState,
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