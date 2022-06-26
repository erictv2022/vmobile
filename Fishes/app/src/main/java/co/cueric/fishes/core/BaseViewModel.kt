package co.cueric.fishes.core

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.cueric.fishes.core.errors.BaseError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

const val STATE_FLOW_STOP_TIMEOUT_MILLIS: Long = 5000L
val STATEFLOW_STARTED = SharingStarted.WhileSubscribed(STATE_FLOW_STOP_TIMEOUT_MILLIS)

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.stateIn(
        scope = viewModelScope,
        started = STATEFLOW_STARTED,
        initialValue = false
    )

    private val _error = Channel<BaseError?>(Channel.BUFFERED)
    val error = _error.receiveAsFlow()

    val showAlertDialog = MutableStateFlow(false)

    fun showLoading(){
        _isLoading.update { true }
    }

    fun dismissLoading(){
        _isLoading.update { false }
    }

    fun showError(error: BaseError){
        showAlertDialog.update { true }
        _error.trySend(error)
    }

    fun dismissAlertDialog(){
        showAlertDialog.update { false }
    }
}