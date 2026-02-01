package pbs.edu.ProjektPogoda.repository

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import pbs.edu.ProjektPogoda.database.AppDatabase
import pbs.edu.ProjektPogoda.model.WeatherEntity

class WeatherRepository(context: Context) {

    private val dao = AppDatabase.getDatabase(context, CoroutineScope(Dispatchers.IO)).weatherDao()

    val items: Flow<List<WeatherEntity>> = dao.getAll()

    suspend fun addItem(item: WeatherEntity) {
        dao.insert(item)
    }

    suspend fun deleteItem(item: WeatherEntity) {
        dao.deleteItem(item)
    }

    suspend fun getItem(id: Long): WeatherEntity? {
        return dao.getById(id)
    }
}