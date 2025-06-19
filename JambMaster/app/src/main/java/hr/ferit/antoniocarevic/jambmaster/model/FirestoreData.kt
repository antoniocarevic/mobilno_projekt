package hr.ferit.antoniocarevic.jambmaster.model

import com.google.firebase.Timestamp

data class FirestoreGameRecord(
    val gameId: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val players: Map<String, FirestorePlayer> = emptyMap(),
    val userIds: List<String> = emptyList()
)

data class FirestorePlayer(
    val isWinner: Boolean = false,
    val playerName: String = "",
    val totalScore: Int = 0
)