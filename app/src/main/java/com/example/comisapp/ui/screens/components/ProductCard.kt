package com.example.comisapp.ui.screens.components


import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comisapp.ui.theme.TextPurple

// ─────────────────────────────────────────────
// DATA MODEL
// ─────────────────────────────────────────────

data class Product(
    val id: Int,
    val brand: String,
    val name: String,
    val price: String,
    @DrawableRes val imageRes: Int,
    val isFavorite: Boolean = false
)

// ─────────────────────────────────────────────
// PRODUCT CARD COMPOSABLE
//
// Uso básico:
//   ProductCard(
//       product = product,
//       onFavoriteToggle = { id, newState ->
//           viewModel.toggleFavorite(id, newState)
//       }
//   )
//
// Uso en LazyVerticalGrid:
//   LazyVerticalGrid(
//       columns = GridCells.Fixed(2),
//       horizontalArrangement = Arrangement.spacedBy(12.dp),
//       verticalArrangement = Arrangement.spacedBy(12.dp),
//       contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
//   ) {
//       items(products) { product ->
//           ProductCard(
//               product = product,
//               onFavoriteToggle = { id, state -> viewModel.toggleFavorite(id, state) }
//           )
//       }
//   }
// ─────────────────────────────────────────────

@Composable
fun ProductCard(
    product: Product,
    onFavoriteToggle: (productId: Int, isFavorite: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(5.dp)
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                ){
                    FavoriteButton(
                        isFavorite = product.isFavorite,
                        onToggle = { newState ->
                            onFavoriteToggle(product.id, newState)
                        },
                        )
                }



            }

            Column(
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 10.dp,
                    bottom = 12.dp
                )
            ) {
                // Brand
                Text(
                    text = product.brand.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextPurple,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Product name
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.price,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}