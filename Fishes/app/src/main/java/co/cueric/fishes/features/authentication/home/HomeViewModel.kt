package co.cueric.fishes.features.authentication.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.cueric.fishes.core.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) :BaseViewModel(application) {
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