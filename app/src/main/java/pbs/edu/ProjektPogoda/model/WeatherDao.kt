package pbs.edu.ProjektPogoda.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather_items")
    fun getAll(): Flow<List<WeatherEntity>>

    @Query("SELECT * FROM weather_items WHERE id = :id")
    suspend fun getById(id: Long): WeatherEntity?

    @Insert
    suspend fun insert(item: WeatherEntity)

    @Delete
    suspend fun deleteItem(item: WeatherEntity)
}