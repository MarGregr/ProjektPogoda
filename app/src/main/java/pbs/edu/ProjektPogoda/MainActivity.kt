package pbs.edu.ProjektPogoda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import edu.pbs.ProjektPogoda.navigation.AppNavigation
import pbs.edu.ProjektPogoda.ui.theme.ProjektPogodaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjektPogodaApp()
        }
    }
}

@Composable
fun ProjektPogodaApp() {
    ProjektPogodaTheme {
        AppNavigation()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProjektPogodaApp()
}