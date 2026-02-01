package pbs.edu.ProjektPogoda.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_items")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imagePath: String,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val timestamp: Long
)