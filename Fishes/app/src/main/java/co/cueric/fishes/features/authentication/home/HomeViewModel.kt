package co.cueric.fishes.features.authentication.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import co.cueric.fishes.core.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) :BaseViewModel(application) {
    companion object {
        private const val TAG = "HomeViewModel"
    }

    val auth = FirebaseAuth.getInstance()
    private val _promptBiometric = Channel<Unit>(Channel.BUFFERED)
    val promptBiometric = _promptBiometric.receiveAsFlow()
    val username = MutableStateFlow("")

    init {
        viewModelScope.launch {
            auth.currentUser?.run {
                _promptBiometric.trySend(Unit)
            }
        }
    }

    fun biometricSuccess(){
        viewModelScope.launch {
            username.update { auth.currentUser?.email.orEmpty() }
        }
    }
}