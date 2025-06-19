package hr.ferit.antoniocarevic.jambmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import hr.ferit.antoniocarevic.jambmaster.ui.theme.JambMasterTheme
import hr.ferit.antoniocarevic.jambmaster.view.BegginingScreen
import hr.ferit.antoniocarevic.jambmaster.view.LoginScreen
import hr.ferit.antoniocarevic.jambmaster.view.PlayOptions
import hr.ferit.antoniocarevic.jambmaster.view.PlayedGamesScreen
import hr.ferit.antoniocarevic.jambmaster.view.PlayingScreen
import hr.ferit.antoniocarevic.jambmaster.view.RegisterScreen
import hr.ferit.antoniocarevic.jambmaster.view.ResultsScreen
import hr.ferit.antoniocarevic.jambmaster.view.StartScreen
import hr.ferit.antoniocarevic.jambmaster.viewmodel.PlayedGamesViewModel
import kotlin.getValue


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContent {
            JambMasterTheme {
                val navController = rememberNavController()
                val playedGamesMasterViewModel by viewModels<PlayedGamesViewModel>()
                NavHost(navController = navController, startDestination = "beginning") {
                    composable("login") {
                        LoginScreen(
                            navController = navController
                        )
                    }
                    composable("start") {
                        StartScreen(
                            navController = navController
                        )
                    }
                    composable("play_options") {
                        PlayOptions(
                            navController = navController
                        )
                    }
                    composable("played_games") {
                        PlayedGamesScreen(
                            navController = navController,
                            viewModel = playedGamesMasterViewModel,
                        )
                    }
                    composable("playing_screen/{playerNames}") { backStackEntry ->
                        val playerNames = backStackEntry.arguments?.getString("playerNames") ?: ""
                        PlayingScreen(
                            navController = navController,
                            playerNames = playerNames
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            navController = navController
                        )
                    }
                    composable("beginning") {
                        BegginingScreen(
                            navController = navController
                        )
                    }

                    composable("results_screen/{resultsData}") { backStackEntry ->
                        val resultsData = backStackEntry.arguments?.getString("resultsData") ?: ""
                        ResultsScreen(
                            navController = navController,
                            resultsData = resultsData
                        )
                    }
                }
            }
        }
    }
}



