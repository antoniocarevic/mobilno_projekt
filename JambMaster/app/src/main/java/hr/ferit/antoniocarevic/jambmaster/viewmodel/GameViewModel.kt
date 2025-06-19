package hr.ferit.antoniocarevic.jambmaster.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.antoniocarevic.jambmaster.view.Player
import hr.ferit.antoniocarevic.jambmaster.view.PlayerResult
import hr.ferit.antoniocarevic.jambmaster.model.FirestoreGameRecord
import hr.ferit.antoniocarevic.jambmaster.model.FirestorePlayer
import hr.ferit.antoniocarevic.jambmaster.model.GameState
import hr.ferit.antoniocarevic.jambmaster.model.JambSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    fun initializeGame(players: List<Player>) {
        val playerSheets = players.associate { player ->
            player.id to JambSheet(player.id)
        }

        _gameState.value = _gameState.value.copy(
            players = players,
            playerSheets = playerSheets,
            currentPlayerIndex = 0,
            rollsLeft = 3
        )
    }

    fun rollDice() {
        val currentState = _gameState.value
        if (currentState.rollsLeft <= 0) return

        val newDiceValues = currentState.diceValues.mapIndexed { index, currentValue ->
            if (currentState.lockedDice[index]) {
                currentValue
            } else {
                Random.Default.nextInt(1, 7)
            }
        }

        _gameState.value = currentState.copy(
            diceValues = newDiceValues,
            rollsLeft = currentState.rollsLeft - 1
        )
    }

    fun toggleDiceLock(diceIndex: Int) {
        val currentState = _gameState.value
        if (currentState.rollsLeft == 3) return

        val newLockedDice = currentState.lockedDice.toMutableList()


        if (!newLockedDice[diceIndex]) {
            val currentlyLocked = newLockedDice.count { it }

            if (currentlyLocked >= 5) {
                return
            }
        }

       
        newLockedDice[diceIndex] = !newLockedDice[diceIndex]

        _gameState.value = currentState.copy(
            lockedDice = newLockedDice
        )
    }

    fun scoreCategory(category: String) {
        val currentState = _gameState.value
        val currentPlayer = currentState.players[currentState.currentPlayerIndex]
        val currentSheet = currentState.playerSheets[currentPlayer.id] ?: return

        if (currentSheet.scores[category] != null) return


        val score = calculateScore(category, currentState.diceValues, currentState.lockedDice)
        currentSheet.scores[category] = score

        val updatedSheets = currentState.playerSheets.toMutableMap()
        updatedSheets[currentPlayer.id] = currentSheet


        _gameState.value = currentState.copy(
            playerSheets = updatedSheets
        )


        val allPlayersComplete = updatedSheets.values.all { it.isComplete() }

        if (allPlayersComplete) {

            viewModelScope.launch {
                delay(1000)
                _gameState.value = _gameState.value.copy(
                    gameFinished = true
                )
            }
        } else {

            viewModelScope.launch {
                delay(1000)
                nextPlayer(updatedSheets)
            }
        }
    }

    private fun nextPlayer(updatedSheets: Map<Int, JambSheet>) {
        val currentState = _gameState.value
        val nextPlayerIndex = (currentState.currentPlayerIndex + 1) % currentState.players.size


        val newRound = if (nextPlayerIndex == 0) currentState.currentRound + 1 else currentState.currentRound

        _gameState.value = currentState.copy(
            currentPlayerIndex = nextPlayerIndex,
            currentRound = newRound,
            playerSheets = updatedSheets,
            diceValues = listOf(1, 2, 3, 4, 5, 6),
            lockedDice = listOf(false, false, false, false, false, false),
            rollsLeft = 3,
            isProcessingScore = false
        )
    }






    private fun calculateScore(category: String, diceValues: List<Int>, lockedDice: List<Boolean>): Int {

        val lockedDiceValues = diceValues.filterIndexed { index, _ -> lockedDice[index] }


        if (lockedDiceValues.isEmpty()) return 0

        return when (category) {
            "ones" -> lockedDiceValues.count { it == 1 } * 1
            "twos" -> lockedDiceValues.count { it == 2 } * 2
            "threes" -> lockedDiceValues.count { it == 3 } * 3
            "fours" -> lockedDiceValues.count { it == 4 } * 4
            "fives" -> lockedDiceValues.count { it == 5 } * 5
            "sixes" -> lockedDiceValues.count { it == 6 } * 6
            "three_of_kind" -> if (hasNOfAKind(lockedDiceValues, 3)) lockedDiceValues.sum()+10 else 0
            "four_of_kind" -> if (hasNOfAKind(lockedDiceValues, 4)) lockedDiceValues.sum()+20 else 0
            "full_house" -> if (isFullHouse(lockedDiceValues)) 25+30 else 0
            "small_straight" -> if (isSmallStraight(lockedDiceValues)) 30 else 0
            "large_straight" -> if (isLargeStraight(lockedDiceValues)) 40 else 0
            "yahtzee" -> if (hasNOfAKind(lockedDiceValues, 5)) lockedDiceValues.sum()+50 else 0
            "chance" -> lockedDiceValues.sum()
            else -> 0
        }
    }

    private fun hasNOfAKind(diceValues: List<Int>, n: Int): Boolean {
        return diceValues.groupBy { it }.values.any { it.size >= n }
    }

    private fun isFullHouse(diceValues: List<Int>): Boolean {
        val groups = diceValues.groupBy { it }.values.map { it.size }.sorted()
        return groups == listOf(2, 3)
    }

    private fun isSmallStraight(diceValues: List<Int>): Boolean {
        val sorted = diceValues.distinct().sorted()
        val straights = listOf(
            listOf(1, 2, 3, 4),
            listOf(2, 3, 4, 5),
            listOf(3, 4, 5, 6)
        )
        return straights.any { straight -> straight.all { it in sorted } }
    }

    private fun isLargeStraight(diceValues: List<Int>): Boolean {
        val sorted = diceValues.distinct().sorted()
        return sorted == listOf(1, 2, 3, 4, 5) || sorted == listOf(2, 3, 4, 5, 6)
    }

    fun saveGameToFirestore(
        players: List<PlayerResult>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        val gameId = UUID.randomUUID().toString()
        val winner = players.maxByOrNull { it.totalScore }

        val playersMap = players.associate { player ->
            "player${player.playerId}" to FirestorePlayer(
                isWinner = player.playerId == winner?.playerId,
                playerName = player.playerName,
                totalScore = player.totalScore
            )
        }

        val gameRecord = FirestoreGameRecord(
            gameId = gameId,
            timestamp = Timestamp.Companion.now(),
            players = playersMap,
            userIds = listOf(currentUser.uid)
        )

        firestore.collection("games")
            .document(gameId)
            .set(gameRecord)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}