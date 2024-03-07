package com.example.fyp_medapp_android

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class LocationData(    //for storing and getting location history
    val _id: String?,
    val userID: String?,
    val datetime: Long?,
    val locationDetail: LocationDetail?
)

@Serializable
data class LocationDetail(
    val latitude: Double?,
    val longitude: Double?,
)

data class History(
    val formattedDatetime: String,
    val latitude: Double,
    val longitude: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun locationHistoryScreen(navHostController: NavHostController) {

    Scaffold(
        //diaplay the header of each page
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Location History",
                        color = Color.White,
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )

        },
        snackbarHost = { },
        content = { innerPadding ->
            Column(
                Modifier.padding(innerPadding)
            ) {
                Text(
                    text = "Location History View",
                    fontSize = 24.sp
                )

                val locationResult = produceState(
                    initialValue = listOf<LocationData>(),
                    producer = {
                        value =
                            KtorClient.getLocationData(targetUserID) //not String message only, but User data class
                    })
                Log.d("locationScreen after calling API", "locationResult: $locationResult")

                if (locationResult.value.isEmpty()) {
                    Text(
                        text = "No location history found",
                        fontSize = 24.sp
                    )
                } else {

                    var locationList: List<LocationData> = locationResult.value

                    //shown in Map view
                    val firstLocation = LatLng(
                        locationList[0].locationDetail!!.latitude!!,
                        locationList[0].locationDetail!!.longitude!!
                    )

                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(firstLocation, 17f)
                    }

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {

                        locationList.forEach { location ->

                            Marker(
                                state = MarkerState(
                                    position = LatLng(
                                        location.locationDetail!!.latitude!!,
                                        location.locationDetail!!.longitude!!
                                    )
                                ),
                                title = SimpleDateFormat("yyyy MMMM dd, HH:mm:ss", Locale.ENGLISH).format(location.datetime), //Formatted Datetime formattedDatetime
                                snippet = "${location.locationDetail!!.latitude!!}, ${location.locationDetail!!.longitude!!}"
                            )
                        }
                    }
                }
            }
        }
    )
}

//TODO: create a data class for location history and return to UI Screen
//fun getLocationHistory() : History {
//
//   val converted = SimpleDateFormat("yyyy MMMM dd, HH:mm:ss", Locale.ENGLISH).format(currentLocation!!.time)
//   Log.d("abc", "converted time: $converted")
//}