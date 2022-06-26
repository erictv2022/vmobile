package co.cueric.fishes.features.authentication.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import co.cueric.fishes.R
import co.cueric.fishes.core.navigation.tabItemTextStyle

@Composable
fun CartScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.teal_700))
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Cart",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = tabItemTextStyle
        )
    }
}