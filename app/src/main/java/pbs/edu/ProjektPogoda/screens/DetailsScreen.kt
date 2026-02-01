package pbs.edu.ProjektPogoda.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import pbs.edu.ProjektPogoda.model.WeatherEntity
import pbs.edu.ProjektPogoda.repository.WeatherRepository
import pbs.edu.ProjektPogoda.ui.theme.AppBlue
import pbs.edu.ProjektPogoda.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavController,
    itemId: Long
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val repo = remember { WeatherRepository(context) }
    val scope = rememberCoroutineScope()
    var item by remember { mutableStateOf<WeatherEntity?>(null) }

    LaunchedEffect(itemId) {
        item = repo.getItem(itemId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Szczegóły pomiaru") },
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

        Column(modifier = Modifier.padding(paddingValues)) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    item?.let { weather ->

                        Image(
                            painter = rememberAsyncImagePainter(weather.imagePath),
                            contentDescription = "Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Szerokość geograficzna: ${weather.latitude}",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Text(
                            text = "Długość geograficzna: ${weather.longitude}",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Temperatura: ${weather.temperature} °C",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        val sdf = remember { SimpleDateFormat(
                                "yyyy-MM-dd HH:mm",
                                Locale.getDefault()
                            )
                        }
                        val dateString = sdf.format(Date(weather.timestamp))

                        Text(
                            text = "Data odczytu: ${dateString}",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    item?.let {
                                        repo.deleteItem(it)
                                        navController.popBackStack()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text(text = "Usuń pomiar", color = White)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Wróć")
                        }
                    } ?: run {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}