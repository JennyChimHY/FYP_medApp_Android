package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profileScreen(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()  //for user role switching
    textPadding = 5.dp

    //use of globalLoginInfo
    Scaffold(
        //diaplay the header of each page
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "User", //Profile?
                        color = Color.White,
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )

            //add logout button the the bar
//            logoutButton(navController)  //allow in user profile view?
        },
        snackbarHost = { },  //lab11
        content = { innerPadding ->

            Column(
                Modifier.padding(innerPadding)
            ) {
                Row() {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {

                        Text(
                            text = "Personal Information",
                            modifier = Modifier
                                .padding(textPadding),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start
                        )

                        HorizontalDivider(
                            thickness = 4.dp,
                            color = sectionBorderColor
                        )  //section line

                        Text(
                            text = "Name: ${globalLoginInfo.username}",
                            modifier = Modifier
                                .padding(textPadding),
                            textAlign = TextAlign.Start,
                        )
                        if (globalLoginInfo.userRole != "doctor" && globalLoginInfo.userRole != "doctorNavigatePatient") {
                            Text(
                                text = "HKID: ${globalLoginInfo.userID}",
                                modifier = Modifier
                                    .padding(textPadding),
                                textAlign = TextAlign.Start,
                            )
                        }
                        Text(
                            text = "Gender: ${globalLoginInfo.gender}",
                            modifier = Modifier
                                .padding(textPadding),
                            textAlign = TextAlign.Start,
                        )
                        Text(
                            text = "Age: ${globalLoginInfo.age.toString()}",
                            modifier = Modifier
                                .padding(textPadding),
                            textAlign = TextAlign.Start,
                        )
                        Text(
                            text = "Date of Birth: ${globalLoginInfo.dob.toString()}",
                            modifier = Modifier
                                .padding(textPadding),
                            textAlign = TextAlign.Start,
                        )
                        Text(
                            text = "Email: ${globalLoginInfo.email}",
                            modifier = Modifier
                                .padding(textPadding),
                            textAlign = TextAlign.Start,
                        )
                    }
                }

                HorizontalDivider(
                    thickness = 2.dp,
                    color = sectionBorderColor
                )  //section line

                if (globalLoginInfo.userRole == "caregiver" || globalLoginInfo.userRole == "doctorNavigatePatient") {
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "Patient Profile:",
                            modifier = Modifier
                                .padding(textPadding),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start
                        )
                    }

                    Row() {
                        Column() {
                            Text(
                                text = "Name: ${globalLoginPatientInfo.username}",
                                modifier = Modifier
                                    .padding(textPadding),
                                textAlign = TextAlign.Start,
                            )
                            Text(
                                text = "HKID: ${globalLoginPatientInfo.userID}",
                                modifier = Modifier
                                    .padding(textPadding),
                                textAlign = TextAlign.Start,
                            )
                            Text(
                                text = "Gender: ${globalLoginPatientInfo.gender}",
                                modifier = Modifier
                                    .padding(textPadding),
                                textAlign = TextAlign.Start,
                            )
                            Text(
                                text = "Age: ${globalLoginPatientInfo.age.toString()}",
                                modifier = Modifier
                                    .padding(textPadding),
                                textAlign = TextAlign.Start,
                            )
                            Text(
                                text = "Date of Birth: ${globalLoginPatientInfo.dob.toString()}",
                                modifier = Modifier
                                    .padding(textPadding),
                                textAlign = TextAlign.Start,
                            )
                            Text(
                                text = "Email: ${globalLoginPatientInfo.email}",
                                modifier = Modifier
                                    .padding(textPadding),
                                textAlign = TextAlign.Start,
                            )
                        }
                    }

                } else {
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "Patient Connection:",
                            modifier = Modifier
                                .padding(textPadding),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start
                        )
                    }

                    patientConnection(navController)
                }

            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun patientConnection(navController: NavHostController) {

    var coroutineScope = rememberCoroutineScope()

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {

        items(globalLoginInfo?.patientConnection!!) { patient ->
            Card( //Select the card and get connected patient info in globalLoginInfo.patientProfileList
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                ),
                modifier = Modifier
                    .size(width = 400.dp, height = 120.dp)
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                ),
                onClick = {
                    //call KtorClient to get patient info, using patient.patientID as parameter to call API
                    coroutineScope.launch {

                        if (globalLoginInfo.userRole == "patient") {
                            globalLoginInfo.userRole = "caregiver"
                        } else if (globalLoginInfo.userRole == "doctor") {
                            globalLoginInfo.userRole = "doctorNavigatePatient"
                        }

                        val getPatientDetail: (String) -> User = {
                            // lambda function to get the corresponding profile
                            var result =
                                globalLoginInfo.patientProfileList!!.filter { patient -> patient.userID == it }
                                    .first()  //get the target patient info from the stored connection list

                            var tmpPatientInfo: User? = User(
                                globalLoginInfo.token,
                                result!!._id,
                                result.userID,
                                result.firstName,
                                result.lastName,
                                result.gender,
                                result.age,
                                result.dob,
                                result.username,
                                result.email,
                                result.password,
                                result.userRole,
                                result.patientConnection,
                                result.patientProfileList
                            )

                            tmpPatientInfo!!

                        }

                        globalLoginPatientInfo =
                            getPatientDetail(patient.patientID!!)
                        targetUserID = globalLoginPatientInfo.userID!!

                        Log.d(
                            "GET",
                            "Patient info: ${globalLoginPatientInfo.userID}"
                        ) //.patientProfileList[0]!!.firstName
                        navController.navigate("home")
                    }
                }
            ) {
                Text(
                    text = "Name: ${patient.patientName}",
                    modifier = Modifier
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    color = (Color.Black)
                )
                Text(
                    text = "HKID: ${patient.patientID}",
                    modifier = Modifier
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    color = (Color.Black)
                )
            }
        }
    }
}


//TODO: cancel location alarm?