package hr.ferit.antoniocarevic.jambmaster.view


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.ferit.antoniocarevic.jambmaster.model.JambSheet
import hr.ferit.antoniocarevic.jambmaster.model.ShakeDetector
import hr.ferit.antoniocarevic.jambmaster.ui.theme.BluePrimary
import hr.ferit.antoniocarevic.jambmaster.ui.theme.OrangeSecondary
import hr.ferit.antoniocarevic.jambmaster.ui.theme.playFontFamily
import hr.ferit.antoniocarevic.jambmaster.viewmodel.GameViewModel

@Composable
fun PlayingScreen(
    navController: NavController,
    playerNames: String,
    viewModel: GameViewModel = viewModel()
) {

    val context = LocalContext.current
    val players = remember {
        playerNames.split(",").mapIndexed { index, name ->
            Player(index + 1, name.trim())
        }
    }

    val gameState by viewModel.gameState.collectAsState()

    val shakeDetector = remember {
        ShakeDetector(context) {
            if (gameState.rollsLeft > 0) {
                viewModel.rollDice()
            }
        }
    }


    DisposableEffect(Unit) {
        shakeDetector.start()
        onDispose {
            shakeDetector.stop()
        }
    }

    LaunchedEffect(players) {
        viewModel.initializeGame(players)
    }

    if (gameState.gameFinished) {
        LaunchedEffect(Unit) {
            val resultsData = gameState.players.joinToString("|") { player ->
                val sheet = gameState.playerSheets[player.id]
                "${player.id}:${player.name}:${sheet?.getTotalScore() ?: 0}"
            }
            navController.navigate("results_screen/$resultsData")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BluePrimary)
            .padding(16.dp)
    ) {
        if (gameState.players.isNotEmpty()) {
            val currentPlayer = gameState.players[gameState.currentPlayerIndex]
            Spacer(modifier = Modifier.height(45.dp))

            Text(
                text = "${currentPlayer.name}'s Turn",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = playFontFamily,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DiceSection(
                diceValues = gameState.diceValues,
                lockedDice = gameState.lockedDice,
                rollsLeft = gameState.rollsLeft,
                onDiceClick = { index -> viewModel.toggleDiceLock(index) },
                onRollClick = { viewModel.rollDice() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            val currentSheet = gameState.playerSheets[currentPlayer.id]
            if (currentSheet != null) {
                ScoreSheet(
                    sheet = currentSheet,
                    playerName = currentPlayer.name,
                    onCategoryClick = { category -> viewModel.scoreCategory(category) },
                    currentRound = gameState.currentRound
                )
            }
        }
    }
}

@Composable
fun DiceSection(
    diceValues: List<Int>,
    lockedDice: List<Boolean>,
    rollsLeft: Int,
    onDiceClick: (Int) -> Unit,
    onRollClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Rolls Left: $rollsLeft",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BluePrimary,
                fontFamily = playFontFamily
            )


            Text(
                text = "Shake phone or tap button to roll",
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = playFontFamily,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))


            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                diceValues.forEachIndexed { index, value ->
                    DiceItem(
                        value = value,
                        isLocked = lockedDice[index],
                        onClick = { onDiceClick(index) },
                        canLock = rollsLeft < 3
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRollClick,
                enabled = rollsLeft > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeSecondary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (rollsLeft == 3) "First Roll" else "Roll Again",
                    fontFamily = playFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}


@Composable
fun DiceItem(
    value: Int,
    isLocked: Boolean,
    onClick: () -> Unit,
    canLock: Boolean
) {
    Box(
        modifier = Modifier
            .size(47.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isLocked) OrangeSecondary else Color.Gray.copy(alpha = 0.3f)
            )
            .border(
                width = 2.dp,
                color = if (isLocked) OrangeSecondary else BluePrimary,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = canLock) { onClick() },
        contentAlignment = Alignment.Center,

    ) {
        Text(
            text = value.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (isLocked) Color.White else BluePrimary,
            fontFamily = playFontFamily
        )
    }
}

@Composable
fun ScoreSheet(
    sheet: JambSheet,
    playerName: String,
    onCategoryClick: (String) -> Unit,
    currentRound: Int = 1
) {
    var refreshKey by remember { mutableStateOf(0) }
    val scoresSnapshot by remember(sheet.scores.hashCode()) {
        mutableStateOf(sheet.scores.toMap())
    }

    LaunchedEffect(currentRound) {
        refreshKey++
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "$playerName's Score Sheet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BluePrimary,
                fontFamily = playFontFamily,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(300.dp)
            ) {
                val categories = listOf(
                    "ones" to "Ones",
                    "twos" to "Twos",
                    "threes" to "Threes",
                    "fours" to "Fours",
                    "fives" to "Fives",
                    "sixes" to "Sixes",
                    "three_of_kind" to "3 of a Kind",
                    "four_of_kind" to "4 of a Kind",
                    "full_house" to "Full House",
                    "small_straight" to "Small Straight",
                    "large_straight" to "Large Straight",
                    "yahtzee" to "Yahtzee",
                    "chance" to "Chance"
                )

                items(
                    items = categories,
                    key = { (key, _) -> "${key}_${scoresSnapshot[key]}" }
                ) { (key, displayName) ->
                    ScoreCategoryRow(
                        categoryName = displayName,
                        score = scoresSnapshot[key],
                        onClick = { onCategoryClick(key) },
                        isClickable = scoresSnapshot[key] == null
                    )
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Score:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary,
                    fontFamily = playFontFamily
                )
                Text(
                    text = sheet.getTotalScore().toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeSecondary,
                    fontFamily = playFontFamily
                )
            }
        }
    }
}

@Composable
fun ScoreCategoryRow(
    categoryName: String,
    score: Int?,
    onClick: () -> Unit,
    isClickable: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isClickable) { onClick() }
            .background(
                if (isClickable) Color.Gray.copy(alpha = 0.1f) else Color.Transparent,
                RoundedCornerShape(4.dp)
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = categoryName,
            fontSize = 16.sp,
            color = BluePrimary,
            fontFamily = playFontFamily
        )
        Text(
            text = score?.toString() ?: if (isClickable) "Tap to score" else "-",
            fontSize = 16.sp,
            color = if (score != null) OrangeSecondary else Color.Gray,
            fontFamily = playFontFamily,
            textAlign = TextAlign.End
        )
    }
}

