package co.cueric.fishes.managers

import co.cueric.fishes.core.errors.BaseError
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object AuthManager {
    val auth = FirebaseAuth.getInstance()

    fun isLogined() = auth.currentUser != null
}