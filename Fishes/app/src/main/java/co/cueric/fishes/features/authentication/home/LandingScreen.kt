package co.cueric.fishes.features.authentication.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.cueric.fishes.core.currencyText
import co.cueric.fishes.core.ui.theme.AppTypography
import co.cueric.fishes.core.ui.theme.ProductTitle
import coil.compose.AsyncImage

@Composable
fun LandingScreen(navController: NavController, viewModel: HomeViewModel) {
    val products by viewModel.products.collectAsState()
    val exchageRate by viewModel.exchageRate.collectAsState()

    LazyColumn(
        state = rememberLazyListState(),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        products.forEach { product ->
            item {
                Card(modifier = Modifier.fillMaxWidth(), ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = product.productUrls?.firstOrNull().orEmpty(),
                            modifier = Modifier
                                .size(120.dp)
                                .background(Color.LightGray)
                                .border(1.dp, Color.LightGray)
                                .padding(end = 4.dp),
                            contentDescription = "product image",
                            contentScale = ContentScale.Fit
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = product.name, style = AppTypography.ProductTitle, maxLines = 2)
                            Text(text = "Description:", fontSize = 12.sp)
                            Text(text = product.productDescription.orEmpty())
                            Text(text = "${product.weightInG.toString()}g")
                            Row {
                                Text(text = "Price: ")
                                Text(
                                    text = currencyText("HKD", product.price),
                                    fontWeight = FontWeight.SemiBold
                                )

                                exchageRate?.run {
                                    Text(
                                        text = currencyText("GBP", product.price * this),
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}