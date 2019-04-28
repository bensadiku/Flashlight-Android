package com.bensadiku.flashlight

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.bensadiku.flashlight.database.FlashlightDatabase
import com.bensadiku.flashlight.database.FlashlightDatabaseDao
import com.bensadiku.flashlight.database.FlashlightEntity
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 *
 */

@RunWith(AndroidJUnit4::class)
class FlashlightDatabaseTest {

    private lateinit var flashlightDatabaseDao: FlashlightDatabaseDao
    private lateinit var db: FlashlightDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, FlashlightDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        flashlightDatabaseDao = db.flashlightDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNight() {
        val flashlight = FlashlightEntity()
        flashlightDatabaseDao.insert(flashlight)
        val flashlightEntity = flashlightDatabaseDao.getPref()
        assertEquals(flashlightEntity?.Id, 1)
    }
}


