package co.cueric.fishes.features.authentication.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.cueric.fishes.core.compose.ComposeFileProvider
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(navController: NavController, viewModel: UserProfileViewModel) {
    val displayName by viewModel.displayName.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val error by viewModel.error.collectAsState(initial = null)
    val showAlertDialog by viewModel.showAlertDialog.collectAsState()
    val context = LocalContext.current

    //region camera
    var hasImage by remember {
        mutableStateOf(false)
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            hasImage = uri != null
            imageUri = uri
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success
        }
    )

    LaunchedEffect(key1 = Unit) {
        viewModel.cameraPermissionGranted.collectLatest { cameraPermission ->
            if (cameraPermission){
                viewModel.updateCameraPermission(false)
                val uri = ComposeFileProvider.getImageUri(context)
                imageUri = uri
                cameraLauncher.launch(uri)
            }
        }
    }
    //endregion

    if (showAlertDialog && error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissAlertDialog() },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissAlertDialog() })
                { Text(text = "OK") }
            },
            title = { Text(text = "Error") },
            text = { Text(text = error?.message.orEmpty()) }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Profile") },
                actions = {
                    when (isEditing) {
                        true -> {
                            IconButton(onClick = { viewModel.cancelEdit() }) {
                                Icon(imageVector = Icons.Filled.Cancel, contentDescription = "cancel")
                            }
                            IconButton(onClick = { viewModel.saveUserProfile() }) {
                                Icon(imageVector = Icons.Filled.Done, contentDescription = "done")
                            }
                        }
                        false -> {
                            IconButton(onClick = { viewModel.startEditProfile() }) {
                                Icon(imageVector = Icons.Filled.Edit, contentDescription = "edit")
                            }
                        }
                    }
                })
        },
    ) {
        LazyColumn(state = rememberLazyListState(), modifier = Modifier.fillMaxWidth()) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                ) {
                    if (hasImage && imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .border(1.dp, Color.LightGray),
                            contentDescription = "profile image",
                        )
                    }
                    Button(onClick = {
                        viewModel.takePhoto()
                    }) {
                        Text(text = "Change Profile Photo")
                    }

                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { viewModel.updateDisplayName(it) },
                        label = { Text("Display Name") },
                        readOnly = !isEditing
                    )

                    AddressSection(viewModel = viewModel)

                    TextButton(onClick = { viewModel.logout() }) {
                        Text("Sign Out")
                    }
                }
            }
        }
    }
}

@Composable
fun AddressSection(viewModel: UserProfileViewModel) {
    val address1 by viewModel.addressLine1.collectAsState()
    val address2 by viewModel.addressLine2.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Default Delivery Address", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        OutlinedTextField(
            value = address1,
            onValueChange = { viewModel.updateDisplayName(it) },
            label = { Text("Address Line 1") },
            readOnly = !isEditing,
            placeholder = { Text(text = "Address Line 1") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address2,
            onValueChange = { viewModel.updateDisplayName(it) },
            label = { Text("Address Line 2") },
            readOnly = !isEditing,
            placeholder = { Text(text = "Address Line 2") },
            modifier = Modifier.fillMaxWidth()
        )
    }

    Button(onClick = { viewModel.getCurrentLocation() }) {
        Text("Get Current Location")
    }
}