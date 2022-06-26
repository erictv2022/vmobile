package co.cueric.fishes.features.authentication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import co.cueric.fishes.activities.ui.theme.FishesTheme
import co.cueric.fishes.core.startHome
import co.cueric.fishes.views.AuthenticationScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AuthenticationActivity : ComponentActivity() {
    private val viewModel by viewModels<AuthenticationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FishesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AuthenticationScreen(viewModel = viewModel)
                }
            }
        }

        observeViewModel()
    }

    fun observeViewModel(){
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED){
                viewModel.authenticationError.collectLatest { errorMessage ->
                    Toast.makeText(applicationContext, errorMessage,Toast.LENGTH_LONG).show()
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED){
                viewModel.startHome.collectLatest { errorMessage ->
                    startHome(this@AuthenticationActivity)
                    finishAndRemoveTask()
                }
            }
        }
    }
}



