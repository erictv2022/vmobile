package co.cueric.fishes.features.authentication.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import co.cueric.fishes.core.BaseViewModel
import co.cueric.fishes.core.STATEFLOW_STARTED
import co.cueric.fishes.core.errors.AuthenticationError
import co.cueric.fishes.core.errors.DataError
import co.cueric.fishes.core.errors.ERRORCODE
import co.cueric.fishes.managers.AuthManager
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserProfileViewModel(application: Application) :BaseViewModel(application) {
    companion object {
        private val TAG = UserProfileViewModel.javaClass.simpleName
    }

    private val authManager = AuthManager

    private val _displayName = MutableStateFlow("")
    val displayName = _displayName.stateIn(
        scope = viewModelScope,
        started = STATEFLOW_STARTED,
        initialValue = ""
    )

    val isEditing = MutableStateFlow(false)

    init {
        authManager.auth.currentUser?.let { currentUser ->
            _displayName.update { currentUser.displayName.orEmpty() }
        }
    }

    fun startEditProfile(){
        isEditing.update { true }
    }

    fun cancelEdit(){
        isEditing.update { false }
    }

    fun updateDisplayName(name: String){
        _displayName.update { name }
    }

    fun saveUserProfile() {
        showLoading()
        val userReq = userProfileChangeRequest {
            this.displayName = this@UserProfileViewModel.displayName.value
        }

        authManager.auth.currentUser?.updateProfile(userReq)?.addOnCompleteListener { task ->
            try {
                if (task.isSuccessful) {
                    Log.d(TAG, "User profile updated.")
                    isEditing.update { false }
                }
                else {
                    showError(DataError(errorCode = ERRORCODE.FAIL.ordinal, message = "Update fail"))
                }
            } catch (e: Exception) {
                    showError(DataError(errorCode = ERRORCODE.FAIL.ordinal, message = e.localizedMessage))
            }
            finally {
                dismissLoading()
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
            try {
                AuthManager.signOut()
            } catch (e: Exception) {
                // 400 as bad request
                showError(AuthenticationError(errorCode = 400, message = e.localizedMessage))
            }
        }
    }
}