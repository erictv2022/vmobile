package co.cueric.fishes

import android.os.Bundle
import androidx.activity.ComponentActivity
import co.cueric.fishes.core.startHome
import co.cueric.fishes.core.startRegister
import co.cueric.fishes.managers.AuthManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AuthManager.isLogined()) {
            startHome(this)
        } else {
            startRegister(this)
        }
    }
}