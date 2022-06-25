package co.cueric.fishes.features.authentication.profile

import android.app.Application
import androidx.lifecycle.viewModelScope
import co.cueric.fishes.core.BaseViewModel
import co.cueric.fishes.core.STATEFLOW_STARTED
import co.cueric.fishes.managers.AuthManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserProfileViewModel(application: Application) :BaseViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private var firebaseUser: FirebaseUser? = auth.currentUser

    private val _displayName = MutableStateFlow("")
    val displayName = _displayName.stateIn(
        scope = viewModelScope,
        started = STATEFLOW_STARTED,
        initialValue = ""
    )

    val isEditing = MutableStateFlow(false)

    init {
        auth.currentUser?.let { currentUser ->
            _displayName.update { currentUser.displayName.orEmpty() }
        }
    }

    fun startEditProfile(){
        isEditing.update { true }
    }

    fun updateDisplayName(name: String){
        val userReq = userProfileChangeRequest {
            this.displayName = "${name}"
        }
    }

    fun saveUserProfile(){
        isEditing.update { false }
    }

    fun logout(){
        viewModelScope.launch {
            try {
                AuthManager.signOut()
            } catch (e: Exception) {

            }
        }
    }
}