package com.example.comisapp.ui.screens.primaryNavigationScreens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.example.comisapp.R
import com.example.comisapp.data.repository.NotificationRepository
import com.example.comisapp.ui.screens.ViewModels.HomeViewModel
import com.example.comisapp.ui.screens.ViewModels.NotificationViewModel
import com.example.comisapp.ui.screens.components.CategoryItem
import com.example.comisapp.ui.screens.components.CategoryItemSkeleton
import com.example.comisapp.ui.screens.components.ProductCard
import com.example.comisapp.ui.screens.components.ProductCardSkeleton
import com.example.comisapp.ui.theme.CardBorderColor
import com.example.comisapp.ui.theme.TextPurple


data class ProfileMenuItem(
    val icon: ImageVector,
    val label: String,
    val onClick: ()-> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onGoDetail: (String) -> Unit,
    onNavigateToCuenta: ()->Unit = {},
    onLogOut: ()-> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onGoCategoria: (String) -> Unit = {}
){
    val categoriasItems = viewModel.categoriesFlow.collectAsLazyPagingItems()
    val productosItems = viewModel.productsFlow.collectAsLazyPagingItems()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val profileUrl= viewModel.currentUser?.fotoUrl


    val isLoadingCategories = categoriasItems.loadState.refresh is LoadState.Loading
    val isLoadingProducts = productosItems.loadState.refresh is LoadState.Loading


    val notificationViewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModel.Factory(NotificationRepository())
    )
    val noLeidas by notificationViewModel.noLeidasCount.collectAsState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppHeader(
                profileImageUrl = profileUrl,
                onNavigateToCuenta = onNavigateToCuenta,
                onCerrarSesion = onLogOut,
                scrollBehavior = scrollBehavior,
                onNotificationsClick = onNotificationsClick,
                unreadCount = noLeidas

            )
        },

        ){ paddingValues ->
//            val favoriteStates = remember { mutableStateListOf(*Array(10) { false }) }

        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize().background(Color.White)

        ){

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.padding(bottom = 60.dp  )
            ){
                item(span = {GridItemSpan(2)}) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "QUIZAS PODRÍA INTERESARTE",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                letterSpacing = 1.1.sp,
                                color = Color(0xFF474747)
                            ),
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.img_mas_destacado),
                                    contentDescription = "destacado",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                )

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(25.dp)
                                ){
                                    Column(
                                    ) {
                                        Text(
                                            text = "Lo Más \nDestacado",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontSize = 36.sp,
                                                fontWeight = FontWeight.Bold,
                                                lineHeight = 40.sp,
                                                letterSpacing = -1.8.sp
                                            ),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Conoce lo productos mas solicitados por nuestros usuarios",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White.copy(alpha = 0.6f),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Normal,
                                                lineHeight = 20.sp,
                                                letterSpacing = 0.sp
                                            ),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                    }

                                }

                            }
                            Spacer(modifier = Modifier.height(25.dp))
                            Text(
                                text = "CATEGORÍAS SUGERIDAS",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.1.sp,
                                    color = Color(0xFF474747)
                                ),
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(15.dp)

                            ) {
                                if(isLoadingCategories) {
                                    items(5) {
                                        CategoryItemSkeleton()
                                    }
                                }else{
                                    items(
                                        count = categoriasItems.itemCount,
                                        key = categoriasItems.itemKey { it.id }
                                    ){ index ->
                                        val categoria = categoriasItems[index]
                                        categoria.let {
                                            CategoryItem(
                                                categoryModel = it!!,
                                                onGoCategoria = onGoCategoria
                                            )
                                        }
                                    }
                                }

                            }
                            Spacer(modifier = Modifier.height(25.dp))
                            Text(text = "Recomendaciones para ti",style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = -(0.6).sp,
                                fontSize = 24.sp
                            )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(thickness = 0.5.dp, color = CardBorderColor.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(10.dp))

                        }
                    }

                }

                items(
                    count = if (isLoadingProducts) 6 else  productosItems.itemCount,
                    key = if (isLoadingProducts) null else productosItems.itemKey { it.id }
                ){
                    index ->
                    if(isLoadingProducts){
                        ProductCardSkeleton()
                    }else{
                        val producto = productosItems[index]
                        producto?.let {
                            ProductCard(
                                product = it,
                                onGoDetail = onGoDetail
                            )
                        }
                    }

                }
            }


        }


    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
    scrollBehavior: TopAppBarScrollBehavior,
    profileImageUrl: String? = null,
    onNavigateToCuenta: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    unreadCount: Int = 0,


) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        modifier = Modifier.shadow(elevation = 0.5.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Comis",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                )
                Text(
                    text = "App",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPurple
                    )
                )
            }
        },
        actions = {

            IconButton(
                onClick = {
                    onNotificationsClick()
                }
            ) {
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            Badge()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notificaciones",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            ProfilePopover(
                profileImageUrl = profileImageUrl,
                onNavigateToCuenta = onNavigateToCuenta,
                onCerrarSesion = onCerrarSesion
            )

            Spacer(modifier = Modifier.width(4.dp))
        }
    )
}

@Composable
fun ProfilePopover(
    profileImageUrl: String? = null,
    onNavigateToCuenta: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    val profileMenuItems = listOf(
        ProfileMenuItem(
            icon = Icons.Outlined.ManageAccounts,
            label = "Cuenta",
            onClick = {
                expanded = false
                onNavigateToCuenta()
            }
        ),
        ProfileMenuItem(
            icon = Icons.Outlined.Logout,
            label = "Cerrar Sesión",
            onClick = {
                expanded = false
                onCerrarSesion()
            }
        )
    )

    Box {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { expanded = true },
            contentAlignment = Alignment.Center
        ) {
            if (profileImageUrl != null) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Perfil",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = 0.dp,y = 15.dp),
            modifier = Modifier
                .width(180.dp)
                .background(Color.White)
                .clip(RoundedCornerShape(15.dp))
        ) {
            profileMenuItems.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (item.label == "Cerrar Sesión")
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = item.onClick,
                    colors = MenuDefaults.itemColors(
                        textColor = if (item.label == "Cerrar Sesión")
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}
