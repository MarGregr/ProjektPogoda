package pbs.edu.ProjektPogoda.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pbs.edu.ProjektPogoda.model.WeatherEntity
import pbs.edu.ProjektPogoda.repository.WeatherRepository
import pbs.edu.ProjektPogoda.ui.theme.AppBlue
import pbs.edu.ProjektPogoda.ui.theme.White
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File

data class WeatherResponse(
    val current_weather: CurrentWeather
)

data class CurrentWeather(
    val temperature: Double
)

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current_weather") current: Boolean = true
    ): WeatherResponse
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(navController: NavController) {

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val repo = remember { WeatherRepository(context) }
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var temperature by remember { mutableStateOf<Double?>(null) }

    val api = remember {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    fun createImageUri(): Uri {
        val file = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            imageUri = null
        } else {
            imageUri = tempUri
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false

        if (cameraGranted) {
            val uri = createImageUri()
            tempUri = uri
            cameraLauncher.launch(uri)
        }
    }

    fun fetchLocationAndWeather() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                latitude = it.latitude
                longitude = it.longitude

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = api.getWeather(latitude!!, longitude!!)
                        temperature = response.current_weather.temperature
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        if (locationGranted) {
            fetchLocationAndWeather()
        }
    }


    fun checkLocationPermission() {
        if (
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
            return
        }
        fetchLocationAndWeather()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Dodawanie pomiaru") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppBlue, titleContentColor = White),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Wróć",
                            tint = White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
                ) {

                    val uri = createImageUri()
                    tempUri = uri
                    cameraLauncher.launch(uri)

                } else {
                    cameraPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                }
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppBlue,
                    contentColor = White
                )
            ) {
                Text("Zrób zdjęcie")
            }

            Spacer(modifier = Modifier.height(16.dp))

            imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { checkLocationPermission() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppBlue,
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            ) {
                Text(text = "Pobierz GPS i temperaturę")
            }

            Spacer(modifier = Modifier.height(16.dp))

            latitude?.let { Text("Szerokość geograficzna: $it") }
            longitude?.let { Text("Długość geograficzna: $it") }
            temperature?.let { Text("Temperatura: $it °C") }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                enabled = imageUri != null && latitude != null && temperature != null,
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        repo.addItem(
                            WeatherEntity(
                                imagePath = imageUri.toString(),
                                latitude = latitude!!,
                                longitude = longitude!!,
                                temperature = temperature!!,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppBlue,
                    contentColor = White
                )
            ) {
                Text("Zapisz")
            }
        }
    }
}
