package co.cueric.fishes.features.authentication.home

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import co.cueric.fishes.core.navigation.TabNavigationItem
import co.cueric.fishes.features.authentication.add.AddScreen
import co.cueric.fishes.features.authentication.cart.CartScreen
import co.cueric.fishes.core.ui.theme.FishesTheme
import co.cueric.fishes.features.authentication.notification.NotificationScreen
import co.cueric.fishes.features.authentication.profile.ProfileScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class HomeActivity : AppCompatActivity() {
    private val viewModel by viewModels<HomeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FishesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    HomeScreen()
                }
            }
        }

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
    }

    //region Biometric
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    fun showBioMetricAuth() {
        executor = ContextCompat.getMainExecutor(this)
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Unlock using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    viewModel.biometricSuccess()
//                    Toast.makeText(
//                        applicationContext,
//                        "Authentication succeeded!",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        biometricPrompt.authenticate(promptInfo)
    }
    //endregion

    fun observeViewModel() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.promptBiometric.collectLatest {
                    showBioMetricAuth()
                }
            }
        }
    }
}

@Composable
fun Greeting2(viewModel: HomeViewModel) {
    val email by viewModel.username.collectAsState()
    Text(text = "You login with ${email}")
}

@Composable
fun HomeScreen() {
    val items = listOf(
        TabNavigationItem.Home,
        TabNavigationItem.Cart,
        TabNavigationItem.Add,
        TabNavigationItem.Notification,
        TabNavigationItem.Profile
    )
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { item ->
                    BottomNavigationItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.screen_route } == true,
                        onClick = {
                            navController.navigate(item.screen_route) {
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = TabNavigationItem.Home.screen_route,
            Modifier.padding(innerPadding)
        ) {
            composable(TabNavigationItem.Home.screen_route) { LandingScreen(navController) }
            composable(TabNavigationItem.Cart.screen_route) { CartScreen(navController) }
            composable(TabNavigationItem.Add.screen_route) { AddScreen(navController) }
            composable(TabNavigationItem.Notification.screen_route) {
                NotificationScreen(
                    navController
                )
            }
            composable(TabNavigationItem.Profile.screen_route) { ProfileScreen(navController) }
        }
    }
}