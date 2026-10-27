package pl.wieczorek.planerpodrozy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pl.wieczorek.planerpodrozy.ui.navigation.PlanerNavHost
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Aplikacja rysuje się pod paskami systemowymi, a odstępy od nich
        // dodają Scaffold i paski aplikacji (WindowInsets).
        enableEdgeToEdge()
        setContent {
            PlanerPodrozyTheme {
                PlanerNavHost()
            }
        }
    }
}
