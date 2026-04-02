package com.example.comisapp.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.comisapp.R
import com.example.comisapp.data.models.CategoryModel
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import java.util.Locale
import java.util.Locale.getDefault

@Composable
fun CategoryItem(
    categoryModel: CategoryModel,
    onGoCategoria: (String) -> Unit = {}
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(70.dp).clip(CircleShape).
            clickable(
                enabled = true,
                onClick = { onGoCategoria(categoryModel.id) }
            )
        ){
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ){}
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(categoryModel.imagenUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = categoryModel.nombre,
                contentScale = ContentScale.Fit,
                onLoading = {
                },
                modifier = Modifier
                    .size(95.dp)
                    .rotate(5f)
                    .offset(y = (-2).dp)
            )
        }
        Text(text = categoryModel.nombre.uppercase(getDefault()),style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            fontSize = 9.sp
            )
        )
    }

}

@Composable
fun CategoryItemSkeleton() {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.Window)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .shimmer(shimmerInstance)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .width(45.dp)
                .height(9.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer(shimmerInstance)
                .background(Color.LightGray)
        )
    }
}
