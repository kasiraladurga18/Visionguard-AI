package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.VisionGuardDatabase
import com.example.data.local.entity.EmergencyContactEntity
import com.example.data.local.entity.UserPreferenceEntity
import com.example.data.local.entity.VisionHistoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: VisionGuardDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            VisionGuardDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context validates app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("VisionGuard AI", appName)
    }

    @Test
    fun `insert and query emergency contacts`() = runBlocking {
        val contactDao = database.emergencyContactDao()
        val contact = EmergencyContactEntity(
            id = UUID.randomUUID().toString(),
            name = "Sarah Jenkins",
            phoneNumber = "+15559876543",
            relationship = "Primary Caregiver",
            isPrimary = true
        )
        contactDao.insertContact(contact)

        val contacts = contactDao.getAllContacts().first()
        assertEquals(1, contacts.size)
        assertEquals("Sarah Jenkins", contacts[0].name)
        assertTrue(contacts[0].isPrimary)
    }

    @Test
    fun `insert and retrieve vision history item`() = runBlocking {
        val historyDao = database.visionHistoryDao()
        val entity = VisionHistoryEntity(
            id = "test_hist_1",
            taskType = "scene_describe",
            prompt = "What is ahead of me?",
            resultText = "An open doorway about 3 meters straight ahead.",
            conciseSpeech = "An open doorway 3 meters ahead."
        )
        historyDao.insertHistory(entity)

        val list = historyDao.getAllHistory().first()
        assertEquals(1, list.size)
        assertEquals("test_hist_1", list[0].id)
        assertEquals("An open doorway 3 meters ahead.", list[0].conciseSpeech)
    }

    @Test
    fun `save and update user preferences`() = runBlocking {
        val prefDao = database.userPreferenceDao()
        val initialPref = UserPreferenceEntity(
            id = 1,
            speechRate = 1.25f,
            highContrast = true,
            obstacleAlerts = true
        )
        prefDao.savePreferences(initialPref)

        val retrieved = prefDao.getPreferences().first()
        assertNotNull(retrieved)
        assertEquals(1.25f, retrieved!!.speechRate)
        assertTrue(retrieved.highContrast)
    }
}
