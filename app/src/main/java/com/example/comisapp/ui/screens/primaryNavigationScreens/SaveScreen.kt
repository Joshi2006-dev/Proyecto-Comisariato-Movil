package com.example.comisapp.ui.screens.primaryNavigationScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.comisapp.ui.screens.ViewModels.SaveViewModel
import com.example.comisapp.ui.screens.components.EmptyAnimation
import com.example.comisapp.ui.screens.components.NotFounAnimation
import com.example.comisapp.ui.screens.components.ProductCard
import com.example.comisapp.ui.screens.components.ProductCardSkeleton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveScreen(
    viewModel: SaveViewModel,
    onGoDetail: (String) -> Unit
) {
    val favorites = viewModel.productsFlow.collectAsLazyPagingItems()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var isManualRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(favorites.loadState.refresh) {
        if (favorites.loadState.refresh is LoadState.NotLoading) {
            isManualRefreshing = false
        }
    }

    val isFavoriteLoading = favorites.loadState.refresh is LoadState.Loading


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .shadow(if (scrollBehavior.state.collapsedFraction > 0.5f) 4.dp else 0.dp)
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Mis Favoritos",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isManualRefreshing,
            onRefresh = {
                isManualRefreshing = true
                favorites.refresh()
            },
            modifier = Modifier
                .padding(paddingValues)
                .padding(bottom = 55.dp)
                .fillMaxSize()
                .background(Color.White),
        ){

            if (!isFavoriteLoading && favorites.itemCount == 0) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyAnimation()
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        count = if (isFavoriteLoading) 6 else favorites.itemCount,
                        key = if (isFavoriteLoading) null else favorites.itemKey { it.id }
                    ) { index ->
                        if (isFavoriteLoading) {
                            ProductCardSkeleton()
                        } else {
                            val producto = favorites[index]
                            producto?.let {
                                ProductCard(
                                    product = it,
                                    onGoDetail = onGoDetail,
                                    isFavoriteUi = true
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}
