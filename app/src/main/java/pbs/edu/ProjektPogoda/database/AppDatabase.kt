package pbs.edu.ProjektPogoda.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import pbs.edu.ProjektPogoda.model.WeatherDao
import pbs.edu.ProjektPogoda.model.WeatherEntity
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = [WeatherEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_db"
                ).fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build().also { INSTANCE = it }
            }
    }

    private class AppDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val dao = database.weatherDao()

                    val sample = WeatherEntity(
                        imagePath = "android.resource://pbs.edu.ProjektPogoda/drawable/wroclaw",
                        latitude = 51.0709,
                        longitude = 17.0234,
                        temperature = 19.6,
                        timestamp = 1749725686000
                    )

                    val sample2 = WeatherEntity(
                        imagePath = "android.resource://pbs.edu.ProjektPogoda/drawable/honolulu",
                        latitude = 21.1876,
                        longitude = 157.5109,
                        temperature = 28.6,
                        timestamp = 1749725686000
                    )

                    val sample3 = WeatherEntity(
                        imagePath = "android.resource://pbs.edu.ProjektPogoda/drawable/antarktyda",
                        latitude = 66.3323,
                        longitude = 45.9011,
                        temperature = -17.7,
                        timestamp = 1749725686000
                    )
                    dao.insert(sample)
                    dao.insert(sample2)
                    dao.insert(sample3)
                }
            }
        }
    }
}