package co.cueric.fishes.features.authentication

import android.app.Application
import co.cueric.fishes.core.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

enum class Mode {
    REGISTER, LOGIN
}

class AuthenticationViewModel(application: Application) : BaseViewModel(application) {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authenticationError = Channel<String>(Channel.BUFFERED)
    val authenticationError = _authenticationError.receiveAsFlow()

    private val _startHome = Channel<Unit>(Channel.BUFFERED)
    val startHome = _startHome.receiveAsFlow()

    val mode = MutableStateFlow(Mode.LOGIN)

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    fun changeMode(mode: Mode) {
        email.update { "" }
        password.update { "" }

        this.mode.update { mode }
    }

    fun setEmail(email: String) {
        this.email.update { email }
    }

    fun setPassword(password: String) {
        this.password.update { password }
    }

    fun login(email: String, password: String) {
        showLoading()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            when (it.isSuccessful) {
                true -> _startHome.trySend(Unit)
            }
            dismissLoading()
        }
            .addOnFailureListener {
                _authenticationError.trySend(it.localizedMessage)
            }
    }

    fun register(email: String, password: String) {
        try {
            showLoading()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    when (it.isSuccessful) {
                        true -> {
                            changeMode(Mode.LOGIN)
                        }
                        false -> {
                        }
                    }
                }.addOnFailureListener {
                    _authenticationError.trySend(it.localizedMessage)
                }
        } catch (e: Exception) {
            _authenticationError.trySend(e.localizedMessage)
        } finally {
            dismissLoading()
        }
    }
}