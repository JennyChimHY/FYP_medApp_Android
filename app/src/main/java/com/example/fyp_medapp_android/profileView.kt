package com.example.fyp_medapp_android

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun profileScreen(navController: NavHostController) {


    //use of globalLoginInfo
   Column() {
       Text(text = globalLoginInfo.dob!!)
   }
}