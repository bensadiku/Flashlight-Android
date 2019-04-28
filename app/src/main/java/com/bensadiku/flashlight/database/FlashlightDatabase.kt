package com.bensadiku.flashlight.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bensadiku.flashlight.utils.ioThread

/**
 * Database for the app, only 1 entity, v1.
 * No export schema added at this point, likely will update it in the future!
 * Volatile makes sure the value of INSTANCE is always up to date, doesn't cache, updates all threads instantly
 * Synchronized because only 1 reference can be created
 *
 * INSTANCE will keep a reference to the database
 */
@Database(entities = [FlashlightEntity::class], version = 1, exportSchema = false)
abstract class FlashlightDatabase : RoomDatabase() {

    abstract fun flashlightDatabaseDao(): FlashlightDatabaseDao


    companion object {
        @Volatile
        private var INSTANCE: FlashlightDatabase? = null

        fun getInstance(context: Context): FlashlightDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        FlashlightDatabase::class.java,
                        "flash_light_database"
                    )
                        .fallbackToDestructiveMigration()
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                ioThread {
                                    val initialPreferences = FlashlightEntity(
                                        Id = 1,
                                        enableTimer = false, addTimer = 30000,
                                        lightType = false,
                                        screenAwake = false,
                                        enableNotifications = true
                                    )
                                    getInstance(context).flashlightDatabaseDao().insert(initialPreferences)
                                }
                            }
                        })
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
