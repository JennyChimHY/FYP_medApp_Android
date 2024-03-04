package com.example.fyp_medapp_android

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "rawLocationData")
//data class RawLocationData(
//    @PrimaryKey val userID: String?, val datetime: Long?, val locationDetail: LocationDetail?
//) //{
////    companion object {
////        val data = listOf(
////            Event(id = 1, title = "Career Talks", deptId = "COMS", saved = false),
////            Event(id = 2, title = "Guided Tour", deptId = "COMS", saved = true),
////            Event(id = 3, title = "MindDrive Demo", deptId = "COMP", saved = false),
////            Event(id = 4, title = "Project Demo", deptId = "COMP", saved = false)
////        )
////  }
////}
//
//@Composable
//fun RawLocationSave() {
//
//    val context = LocalContext.current
//    val rawlocationViewModel: RawlocationViewModel = viewModel(
//        factory = RawLocationViewModelFactory(context.applicationContext as Application)
//    )
//
//    val rawlocation by rawlocationViewModel.readAllData.observeAsState(listOf())
//
//    rawlocationViewModel.saveCurrentLocation(rawlocation) //add
//}