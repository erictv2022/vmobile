package co.cueric.fishes.features.authentication.profile

import android.app.Application
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.viewModelScope
import co.cueric.fishes.config.locationiqAPIKey
import co.cueric.fishes.core.BaseViewModel
import co.cueric.fishes.core.STATEFLOW_STARTED
import co.cueric.fishes.core.errors.AuthenticationError
import co.cueric.fishes.core.errors.BaseError
import co.cueric.fishes.core.errors.DataError
import co.cueric.fishes.core.errors.ERRORCODE
import co.cueric.fishes.managers.AuthManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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

    val _takePhoto = Channel<Unit>(Channel.BUFFERED)
    val takePhoto = _takePhoto.receiveAsFlow()
    val cameraPermissionGranted = MutableStateFlow(false)

    val _getCurrentLocation = Channel<Unit>(Channel.BUFFERED)
    val getCurrentLocation = _getCurrentLocation.receiveAsFlow()
    val currentLocation = MutableStateFlow<Location?>(null)
    val locationPermissionGranted = MutableStateFlow(false)
    val gpsLatitude = MutableStateFlow<Double?>(null)
    val gpsLongitude = MutableStateFlow<Double?>(null)
    private val _addressLine1 = MutableStateFlow("")
    val addressLine1 = _addressLine1.stateIn(
        scope = viewModelScope,
        started = STATEFLOW_STARTED,
        initialValue = ""
    )
    private val _addressLine2 = MutableStateFlow("")
    val addressLine2 = _addressLine2.stateIn(
        scope = viewModelScope,
        started = STATEFLOW_STARTED,
        initialValue = ""
    )

    init {
        authManager.auth.currentUser?.let { currentUser ->
            _displayName.update { currentUser.displayName.orEmpty() }
        }
    }

    fun startEditProfile() {
        isEditing.update { true }
    }

    fun cancelEdit(){
        isEditing.update { false }
    }

    fun updateDisplayName(name: String){
        _displayName.update { name }
    }

    fun updateAddress1(address: String?){
        _addressLine1.update { address.orEmpty() }
    }

    fun updateAddress2(address: String?){
        _addressLine2.update { address.orEmpty() }
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
            } finally {
                dismissLoading()
            }
        }
    }

    fun takePhoto() {
        _takePhoto.trySend(Unit)
    }

    fun getCurrentLocation() {
        _getCurrentLocation.trySend(Unit)
    }

    fun updateCameraPermission(granted: Boolean) {
        cameraPermissionGranted.update { granted }
    }

    fun updateLocationPermission(granted: Boolean) {
        locationPermissionGranted.update { granted }
    }

    fun updateLocation(location: Location?, context: Context) {
        currentLocation.update { location }
        viewModelScope.launch {
            reverseGeoLocation(
                lat = location?.latitude.toString(),
                long = location?.longitude.toString(),
                context = context
            )
        }
    }

    fun reverseGeoLocation(lat: String, long: String, context: Context) {
        val queue = Volley.newRequestQueue(context)
        val url =
            "https://us1.locationiq.com/v1/reverse?key=${locationiqAPIKey}&lat=${lat}&lon=${long}&format=json"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                val address = response.getJSONObject("address")
                _addressLine1.update {
                    val address = response.getString("display_name").split(',').take(2).joinToString(",")
                    address
                }
                _addressLine2.update {
                    val address = "${address["road"]}, ${address["city"]}, ${address["state"]}, ${address["country"]}"
                   address
                }
                "Response: %s".format(response.toString())
            },
            Response.ErrorListener { error ->
                showError(
                    BaseError(
                        errorCode = error.networkResponse.statusCode,
                        message = error.localizedMessage
                    )
                )
            }
        )

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }

    fun logout() {
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