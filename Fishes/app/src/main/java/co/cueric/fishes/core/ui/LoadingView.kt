package co.cueric.fishes.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * ProgressDialog in jetpack compose. (2021, May 15). Stack Overflow. https://stackoverflow.com/questions/67546275/progressdialog-in-jetpack-compose/67546683#67546683
 */
@Composable
fun LoadingDialog(message: String, dialogState: MutableState<Boolean>) {
    Dialog(
        onDismissRequest = { dialogState.value = false },
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .background(White, shape = RoundedCornerShape(12.dp))
        ) {
            Column {
                CircularProgressIndicator(modifier = Modifier.padding(6.dp, 0.dp, 0.dp, 0.dp))
                Text(text = "Loading...", Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp))
            }
        }
    }
}