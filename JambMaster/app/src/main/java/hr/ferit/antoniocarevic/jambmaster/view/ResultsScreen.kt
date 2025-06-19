package hr.ferit.antoniocarevic.jambmaster.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.ferit.antoniocarevic.jambmaster.ui.theme.BluePrimary
import hr.ferit.antoniocarevic.jambmaster.ui.theme.OrangeSecondary
import hr.ferit.antoniocarevic.jambmaster.ui.theme.playFontFamily
import androidx.compose.material3.CircularProgressIndicator
import hr.ferit.antoniocarevic.jambmaster.viewmodel.GameViewModel


data class PlayerResult(
    val playerId: Int,
    val playerName: String,
    val totalScore: Int
)

@Composable
fun ResultsScreen(
    navController: NavController,
    resultsData: String,
    viewModel: GameViewModel = viewModel()
) {
    val results = remember {
        if (resultsData.isNotEmpty()) {
            resultsData.split("|").map { playerData ->
                val parts = playerData.split(":")
                val playerId = parts[0].toInt()
                val playerName = if (parts.size >= 2) parts[1] else "Player $playerId"
                val totalScore = if (parts.size >= 3) parts[2].toInt() else 0
                PlayerResult(
                    playerId = playerId,
                    playerName = playerName,
                    totalScore = totalScore
                )
            }.sortedByDescending { it.totalScore }
        } else {
            emptyList()
        }
    }

    var isSaving by remember { mutableStateOf(false) }
    var saveError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BluePrimary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Game Results",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = playFontFamily,
            modifier = Modifier.padding(bottom = 24.dp, top = 16.dp)
        )

        if (results.isNotEmpty()) {
            Text(
                text = "🏆 ${results.first().playerName} Wins! 🏆",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = OrangeSecondary,
                fontFamily = playFontFamily,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(results.mapIndexed { index, result -> index + 1 to result }) { (position, result) ->
                    ResultCard(
                        position = position,
                        result = result,
                        isWinner = position == 1
                    )
                }
            }
        }


        saveError?.let { error ->
            Text(
                text = "Error saving game: $error",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { navController.navigate("play_options") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = BluePrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                Text(
                    text = "Play Again",
                    fontFamily = playFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Button(
                onClick = {
                    isSaving = true
                    saveError = null

                    viewModel.saveGameToFirestore(
                        players = results,
                        onSuccess = {
                            isSaving = false
                            navController.navigate("start")
                        },
                        onFailure = { exception ->
                            isSaving = false
                            saveError = exception.message
                        }
                    )
                },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeSecondary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "Save & Home",
                        fontFamily = playFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}


@Composable
fun ResultCard(
    position: Int,
    result: PlayerResult,
    isWinner: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isWinner) OrangeSecondary.copy(alpha = 0.9f) else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (isWinner) Color.White else BluePrimary,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#$position",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isWinner) OrangeSecondary else Color.White,
                        fontFamily = playFontFamily
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = result.playerName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isWinner) Color.White else BluePrimary,
                        fontFamily = playFontFamily
                    )

                    if (isWinner) {
                        Text(
                            text = "Winner!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.8f),
                            fontFamily = playFontFamily
                        )
                    }
                }
            }


            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${result.totalScore}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isWinner) Color.White else OrangeSecondary,
                    fontFamily = playFontFamily
                )
                Text(
                    text = "points",
                    fontSize = 12.sp,
                    color = if (isWinner) Color.White.copy(alpha = 0.8f) else Color.Gray,
                    fontFamily = playFontFamily
                )
            }
        }
    }
}
