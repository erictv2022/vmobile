package co.cueric.fishes.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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
    val isLoading by viewModel.isLoading.collectAsState()
    var dialogState  = remember { mutableStateOf(false) }
    if (isLoading){
        LoadingDialog(message = "Loading", dialogState = dialogState)
    }

    LazyColumn(state = rememberLazyListState(), verticalArrangement = Arrangement.Center) {
        item {
            Row(horizontalArrangement = Arrangement.Center) {
                Column() {
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Row() {
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
                                    text = "have account? Go to login",
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
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