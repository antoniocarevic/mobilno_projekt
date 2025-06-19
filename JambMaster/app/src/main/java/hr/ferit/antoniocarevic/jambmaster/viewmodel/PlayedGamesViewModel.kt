package hr.ferit.antoniocarevic.jambmaster.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import hr.ferit.antoniocarevic.jambmaster.model.FirestoreGameRecord
import hr.ferit.antoniocarevic.jambmaster.model.PlayedGamesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayedGamesViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _state = MutableStateFlow(PlayedGamesState())
    val state: StateFlow<PlayedGamesState> = _state.asStateFlow()

    init {
        loadGames()
    }

    fun loadGames() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _state.value = _state.value.copy(
                isLoading = false,
                error = "User not logged in"
            )
            return
        }
        _state.value = _state.value.copy(isLoading = true, error = null)

        firestore.collection("games")
            .whereArrayContains("userIds", currentUser.uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val games = documents.mapNotNull { doc ->
                    try {
                        doc.toObject(FirestoreGameRecord::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
                _state.value = PlayedGamesState(
                    games = games,
                    isLoading = false,
                    error = null
                )
            }
            .addOnFailureListener { exception ->
                _state.value = PlayedGamesState(
                    games = emptyList(),
                    isLoading = false,
                    error = exception.message
                )
            }
    }

    fun refreshGames() {
        loadGames()
    }
}