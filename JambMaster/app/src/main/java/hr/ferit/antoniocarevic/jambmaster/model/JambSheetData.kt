package hr.ferit.antoniocarevic.jambmaster.model

import kotlinx.serialization.Serializable

@Serializable
data class JambSheet(
    val playerId: Int,
    val scores: MutableMap<String, Int?> = mutableMapOf(
        "ones" to null,
        "twos" to null,
        "threes" to null,
        "fours" to null,
        "fives" to null,
        "sixes" to null,
        "three_of_kind" to null,
        "four_of_kind" to null,
        "full_house" to null,
        "small_straight" to null,
        "large_straight" to null,
        "yahtzee" to null,
        "chance" to null
    )
) {
    fun getTotalScore(): Int {
        return scores.values.filterNotNull().sum()
    }

    fun isComplete(): Boolean {
        return scores.values.all { it != null }
    }
}