package co.cueric.fishes.managers

import co.cueric.fishes.core.errors.BaseError
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object AuthManager {
    val auth = FirebaseAuth.getInstance()
    private val _didSignout = Channel<Unit>(Channel.BUFFERED)
    val didSignout = _didSignout.receiveAsFlow()

    init {
        redirectAfterSignOut()
    }

    fun isLogined() = auth.currentUser != null

    fun signOut(){
        auth.signOut()
    }

    fun redirectAfterSignOut(){
        auth.addAuthStateListener {
            if (it.currentUser == null){
                _didSignout.trySend(Unit)
            }
        }
    }
}