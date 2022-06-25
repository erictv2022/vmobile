package co.cueric.fishes.features.authentication.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController, viewModel: UserProfileViewModel) {
    val displayName by viewModel.displayName.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Profile") },
                actions = {
                    when (isEditing) {
                        true -> {
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
                    Row(modifier = Modifier.fillMaxWidth()) {
                        //profile pic
                    }

                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { viewModel.updateDisplayName(it) },
                        label = { Text("Display Name") },
                        readOnly = !isEditing
                    )
                    
                    TextButton(onClick = { viewModel.logout() }) {
                        Text("Sign Out")
                    }
                }
            }
        }
    }
}