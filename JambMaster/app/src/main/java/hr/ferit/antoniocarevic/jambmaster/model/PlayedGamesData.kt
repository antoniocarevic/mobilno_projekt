package hr.ferit.antoniocarevic.jambmaster.model

data class PlayedGamesState(
    val games: List<FirestoreGameRecord> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)