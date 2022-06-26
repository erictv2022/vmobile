package co.cueric.fishes.features.authentication.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
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
import androidx.core.app.ActivityCompat
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
import co.cueric.fishes.core.startRegister
import co.cueric.fishes.core.ui.theme.FishesTheme
import co.cueric.fishes.features.authentication.add.AddScreen
import co.cueric.fishes.features.authentication.cart.CartScreen
import co.cueric.fishes.features.authentication.notification.NotificationScreen
import co.cueric.fishes.features.authentication.profile.ProfileScreen
import co.cueric.fishes.features.authentication.profile.UserProfileViewModel
import co.cueric.fishes.managers.AuthManager
import com.google.android.gms.location.*
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class HomeActivity : AppCompatActivity() {
    private val viewModel by viewModels<HomeViewModel>()
    private val userProfileViewModel by viewModels<UserProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FishesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    HomeScreen(
                        homeViewModel = viewModel,
                        userProfileViewModel = userProfileViewModel
                    )
                }
            }
        }

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
                    viewModel.fetchExchangeRate(from = "HKD", to = "GBP", context = this@HomeActivity)
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

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                AuthManager.didSignout.collectLatest {
                    startRegister(this@HomeActivity)
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                userProfileViewModel.getCurrentLocation.collectLatest {
                    // Request location permissions
                    PermissionX.init(this@HomeActivity)
                        .permissions(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        .request { _, grantedList, deniedList ->
                            if (!grantedList.isNullOrEmpty()) {
                                userProfileViewModel.updateLocationPermission(true)
                            } else {
                                userProfileViewModel.updateLocationPermission(false)
                                Toast.makeText(
                                    this@HomeActivity,
                                    "These permissions are denied: $deniedList",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                userProfileViewModel.takePhoto.collectLatest {
                    // Request camera permissions
                    PermissionX.init(this@HomeActivity)
                        .permissions(
                            Manifest.permission.CAMERA,
                        )
                        .request { allGranted, grantedList, deniedList ->
                            if (allGranted) {
                                userProfileViewModel.updateCameraPermission(allGranted)
                            } else {
                                userProfileViewModel.updateCameraPermission(false)
                                Toast.makeText(
                                    this@HomeActivity,
                                    "These permissions are denied: $deniedList",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                userProfileViewModel.locationPermissionGranted.collectLatest { locationPermissionGranted ->
                    if (locationPermissionGranted) {
                        getCurrentLocationFromGPS()
                    }
                }
            }
        }
    }

    //region Location
    protected var mLastLocation: Location? = null
    protected var mLocationRequest: LocationRequest? = null
    protected var mLocationProvider: FusedLocationProviderClient? = null

    var mLocationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            mLastLocation = result.lastLocation
            userProfileViewModel.updateLocation(mLastLocation, context = this@HomeActivity)
        }
    }

    fun getCurrentLocationFromGPS() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            mLocationProvider = LocationServices.getFusedLocationProviderClient(this)
            mLocationRequest = LocationRequest.create()
            mLocationProvider?.requestLocationUpdates(
                mLocationRequest!!,
                mLocationCallBack,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            Log.d("", e.localizedMessage)
        }
    }
    //endregion
}

@Composable
fun Greeting2(viewModel: HomeViewModel) {
    val email by viewModel.username.collectAsState()
    Text(text = "You login with ${email}")
}

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    userProfileViewModel: UserProfileViewModel
) {
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
            composable(TabNavigationItem.Home.screen_route) {
                LandingScreen(
                    navController,
                    viewModel = homeViewModel
                )
            }
            composable(TabNavigationItem.Cart.screen_route) { CartScreen(navController) }
            composable(TabNavigationItem.Add.screen_route) { AddScreen(navController) }
            composable(TabNavigationItem.Notification.screen_route) {
                NotificationScreen(
                    navController
                )
            }
            composable(TabNavigationItem.Profile.screen_route) {
                ProfileScreen(
                    navController,
                    viewModel = userProfileViewModel
                )
            }
        }
    }
}