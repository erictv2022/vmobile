package co.cueric.fishes.managers

import com.google.firebase.auth.FirebaseAuth

object AuthManager {
    val auth = FirebaseAuth.getInstance()

    fun isLogined() = auth.currentUser != null
}