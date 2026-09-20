package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.EmergencyContactDao
import com.example.data.local.dao.UserPreferenceDao
import com.example.data.local.dao.VisionHistoryDao
import com.example.data.local.entity.EmergencyContactEntity
import com.example.data.local.entity.UserPreferenceEntity
import com.example.data.local.entity.VisionHistoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        VisionHistoryEntity::class,
        EmergencyContactEntity::class,
        UserPreferenceEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class VisionGuardDatabase : RoomDatabase() {
    abstract fun visionHistoryDao(): VisionHistoryDao
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun userPreferenceDao(): UserPreferenceDao

    companion object {
        @Volatile
        private var INSTANCE: VisionGuardDatabase? = null

        fun getInstance(context: Context): VisionGuardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VisionGuardDatabase::class.java,
                    "visionguard_db"
                ).fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            database.emergencyContactDao().insertContact(
                                EmergencyContactEntity(
                                    id = "primary_contact",
                                    name = "Primary Caregiver",
                                    phoneNumber = "911",
                                    relationship = "Emergency Contact",
                                    isPrimary = true
                                )
                            )
                            database.userPreferenceDao().savePreferences(UserPreferenceEntity())
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
