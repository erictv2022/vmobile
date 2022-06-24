package co.cueric.fishes.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import co.cueric.fishes.core.ui.LoadingDialog
import co.cueric.fishes.features.authentication.AuthenticationViewModel
import co.cueric.fishes.features.authentication.Mode

@Composable
fun AuthenticationScreen(viewModel: AuthenticationViewModel) {
    RegistrationForm(viewModel)
}

@Composable
fun RegistrationForm(viewModel: AuthenticationViewModel) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val mode by viewModel.mode.collectAsState()
    var showPassword by rememberSaveable { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()
    var dialogState  = remember { mutableStateOf(false) }
    if (isLoading){
        LoadingDialog(message = "Loading", dialogState = dialogState)
    }

    LazyColumn(state = rememberLazyListState(), verticalArrangement = Arrangement.Center) {
        item {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { viewModel.setEmail(it) },
                        label = { Text("Username") }
                    )

                    OutlinedTextField(
                        modifier = Modifier.padding(top = 16.dp),
                        value = password,
                        onValueChange = { viewModel.setPassword(it) },
                        label = { Text("Password") },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (showPassword)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff
                            val description =
                                if (showPassword) "Hide password" else "Show password"

                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(imageVector = image, description)
                            }
                        }
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        when (mode) {
                            Mode.LOGIN -> {
                                Button(
                                    modifier = Modifier.padding(top = 16.dp),
                                    onClick = {
                                        viewModel.login(
                                            email = email,
                                            password = password
                                        )
                                    }) {
                                    Text(text = "Login")
                                }

                                Text(
                                    text = "Register account",
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 16.dp)
                                        .clickable {
                                            viewModel.changeMode(Mode.REGISTER)
                                        })
                            }
                            Mode.REGISTER -> {
                                Button(
                                    modifier = Modifier.padding(top = 16.dp),
                                    onClick = {
                                        viewModel.register(
                                            email = email,
                                            password = password
                                        )
                                    }) {
                                    Text(text = "Register")
                                }

                                Text(
                                    text = "Have account? Go to login",
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 16.dp)
                                        .clickable {
                                            viewModel.changeMode(Mode.LOGIN)
                                        })
                            }
                        }
                    }
                }
            }
        }
    }
}