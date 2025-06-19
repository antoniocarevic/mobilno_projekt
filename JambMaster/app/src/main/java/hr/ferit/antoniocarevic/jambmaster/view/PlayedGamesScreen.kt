package hr.ferit.antoniocarevic.jambmaster.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.antoniocarevic.jambmaster.R
import hr.ferit.antoniocarevic.jambmaster.model.FirestoreGameRecord
import hr.ferit.antoniocarevic.jambmaster.ui.theme.BluePrimary
import hr.ferit.antoniocarevic.jambmaster.ui.theme.OrangeSecondary
import hr.ferit.antoniocarevic.jambmaster.ui.theme.playFontFamily
import hr.ferit.antoniocarevic.jambmaster.viewmodel.PlayedGamesViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PlayedGamesScreen(
    navController: NavController,
    viewModel: PlayedGamesViewModel = viewModel()
) {

    val currentUser = FirebaseAuth.getInstance().currentUser

    if (currentUser == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BluePrimary)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Please log in to view your games",
                fontSize = 20.sp,
                color = Color.White,
                fontFamily = playFontFamily,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("login") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeSecondary
                )
            ) {
                Text("Go to Login")
            }
        }
        return
    }
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BluePrimary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_jambmaster3),
            contentDescription = "JambMaster Logo",
            modifier = Modifier
                .size(125.dp)
                .padding(bottom = 16.dp, top = 18.dp)
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Played Games",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = playFontFamily
            )

            IconButton(
                onClick = { viewModel.refreshGames() }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            state.error != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error loading games",
                        fontSize = 22.sp,
                        color = Color.White,
                        fontFamily = playFontFamily
                    )
                    Text(
                        text = state.error!!,
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontFamily = playFontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Button(
                        onClick = { viewModel.refreshGames() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeSecondary
                        ),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(
                            text = "Retry",
                            fontSize = 18.sp
                        )
                    }
                }
            }

            state.games.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No games played yet",
                        fontSize = 22.sp,
                        color = Color.White,
                        fontFamily = playFontFamily
                    )
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.games) { game ->
                        GameCard(game = game)
                    }
                }
            }
        }
    }
}

@Composable
fun GameCard(game: FirestoreGameRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = formatTimestamp(game.timestamp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BluePrimary,
                fontFamily = playFontFamily,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))


            val winner = game.players.values.maxByOrNull { it.totalScore }

            if (winner != null && winner.playerName.isNotEmpty()) {

                Text(
                    text = winner.playerName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeSecondary,
                    fontFamily = playFontFamily,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${winner.totalScore} points",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = BluePrimary,
                    fontFamily = playFontFamily,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Unknown winner",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontFamily = playFontFamily
                )
            }
        }
    }
}


private fun formatTimestamp(timestamp: Timestamp): String {
    val date = timestamp.toDate()
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return "${dateFormat.format(date)} at ${timeFormat.format(date)}"
}
