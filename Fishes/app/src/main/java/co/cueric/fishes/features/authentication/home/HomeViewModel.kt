package co.cueric.fishes.features.authentication.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.cueric.fishes.core.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) :BaseViewModel(application) {
    val auth = FirebaseAuth.getInstance()

    val username = MutableStateFlow("")

    init {
        viewModelScope.launch {
            username.update { auth.currentUser?.email.orEmpty() }
        }
    }
}