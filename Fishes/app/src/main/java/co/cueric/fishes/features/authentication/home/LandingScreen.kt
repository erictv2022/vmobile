package co.cueric.fishes.features.authentication.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.cueric.fishes.core.ui.theme.AppTypography
import co.cueric.fishes.core.ui.theme.ProductTitle

@Composable
fun LandingScreen(navController: NavController, viewModel: HomeViewModel) {
    val products by viewModel.products.collectAsState()

    LazyColumn(
        state = rememberLazyListState(),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        products.forEach { product ->
            item {
                Card(modifier = Modifier.fillMaxWidth(), ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = product.name, style = AppTypography.ProductTitle)
                        Text(text = product.productDescription.orEmpty())
                        Text(text = "${product.weightInG.toString()}g")
                    }
                }
            }
        }
    }
}