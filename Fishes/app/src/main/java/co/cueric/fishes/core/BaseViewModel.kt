package co.cueric.fishes.core

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.cueric.fishes.core.errors.BaseError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

const val STATE_FLOW_STOP_TIMEOUT_MILLIS: Long = 5000L

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = false
    )

    private val _error = Channel<BaseError?>(Channel.BUFFERED)
    val error = _error.receiveAsFlow()

    fun showLoading(){
        _isLoading.update { true }
    }

    fun dismissLoading(){
        _isLoading.update { false }
    }
}