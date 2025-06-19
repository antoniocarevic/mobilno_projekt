package hr.ferit.antoniocarevic.jambmaster.model

import hr.ferit.antoniocarevic.jambmaster.view.Player

data class GameState(
    val players: List<Player> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val currentRound: Int = 1,
    val diceValues: List<Int> = listOf(1, 2, 3, 4, 5, 6),
    val lockedDice: List<Boolean> = listOf(false, false, false, false, false, false),
    val rollsLeft: Int = 3,
    val gameFinished: Boolean = false,
    val playerSheets: Map<Int, JambSheet> = emptyMap(),
    val isProcessingScore: Boolean = false
)