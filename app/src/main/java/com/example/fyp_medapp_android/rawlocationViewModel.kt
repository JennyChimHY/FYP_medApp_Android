//package com.example.fyp_medapp_android
//
//import android.app.Application
//import android.content.Context
//import androidx.lifecycle.*
//import androidx.room.*
//import androidx.room.OnConflictStrategy.Companion.REPLACE
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//@Dao
//interface RawLocationDatabaseDao {
//    @Query("SELECT * from rawLocationData") //SQL query
//    fun getAll(): LiveData<List<RawLocationData>>
//
//    @Update
//    suspend fun update(rawlocationdata : RawLocationData);
//
//    @Query("DELETE FROM rawLocationData")
//    fun clearDatabase()  //delete success return true message? for safety
//
//}
//
//@Database(entities = [RawLocationData::class], version = 1)
//abstract class RawLocationDatabase : RoomDatabase() {
//    abstract fun rawlocationDao(): RawLocationDatabaseDao
//
//    companion object {
//        private var INSTANCE: RawLocationDatabase? = null
//        fun getInstance(context: Context): RawLocationDatabase {
//            synchronized(this) {
//                var instance = INSTANCE
//                if (instance == null) {
//                    instance = Room.databaseBuilder(
//                        context.applicationContext,
//                        RawLocationDatabase::class.java,
//                        "rawLocationData_database"
//                    )
//                        .createFromAsset("locations.db")
//                        .fallbackToDestructiveMigration()
//                        .build()
//                    INSTANCE = instance
//                }
//                return instance
//            }
//        }
//    }
//}
//
//class RawLocationRepository(private val rawlocationDatabaseDao: RawLocationDatabaseDao) {
//
//    val readAllData: LiveData<List<RawLocationData>> = rawlocationDatabaseDao.getAll()
//
//    suspend fun updateLocation(updateLocation: RawLocationData) {
//        rawlocationDatabaseDao.update(updateLocation)
//    }
//}
//
//class RawlocationViewModel(application: Application): AndroidViewModel(application) {
//
//    val readAllData: LiveData<List<RawLocationData>>
//
//    private val repository: RawLocationRepository
//
//    init {
//        val rawlocationDao = RawLocatiAonDatabase.getInstance(application).rawlocationDao()
//        repository = RawLocationRepository(rawlocationDao)
//        readAllData = repository.readAllData
//    }
//
//    fun saveCurrentLocation(rawlocationdata : RawLocationData) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.updateLocation(updateLocation = rawlocationdata)
//        }
//    }
//
//}
//
//class RawLocationViewModelFactory(
//    private val application: Application
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        @Suppress("UNCHECKED_CAST")
//        if (modelClass.isAssignableFrom(RawlocationViewModel::class.java)) {
//            return RawlocationViewModel(application) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
