package com.example.comisapp.ui.screens.primaryNavigationScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.comisapp.R
import com.example.comisapp.data.models.CategoryModel
import com.example.comisapp.ui.screens.ViewModels.CatalogViewModel
import com.example.comisapp.ui.screens.ViewModels.EditCreditState
import com.example.comisapp.ui.screens.ViewModels.HomeViewModel
import com.example.comisapp.ui.screens.components.NotFounAnimation
import com.example.comisapp.ui.screens.components.Product
import com.example.comisapp.ui.screens.components.ProductCard
import com.example.comisapp.ui.screens.components.ProductCardSkeleton
import com.example.comisapp.ui.theme.TextPurple
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel,
    initialCategoriaId: String? = null,
    onGoDetail: (String) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }


    val categories by viewModel.categorias.collectAsState()
    val productos = viewModel.productsFlow.collectAsLazyPagingItems()
    val tabs = listOf(CategoryModel(id="", nombre = "Todos"))+categories

    val isLoadingProducts = productos.loadState.refresh is LoadState.Loading


    LaunchedEffect(initialCategoriaId, categories) {
        if (categories.isNotEmpty()) {
            viewModel.inicializarCategoria(initialCategoriaId)

            if (initialCategoriaId == null) {
                selectedTab = 0
            } else {
                val index = tabs.indexOfFirst { it.id == initialCategoriaId }
                if (index != -1) selectedTab = index
            }
        }
    }

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
                            "Catálogo",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )

                SearchAndFilterBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )

                CatalogTabs(
                    categories = tabs,
                    selectedTabIndex = selectedTab,
                    onTabSelected = {
                        index ->
                        selectedTab = index
                        val categoriaId = if (index == 0) null else tabs[index].id
                        viewModel.seleccionarCategoria(categoriaId)
                    }
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize().background(Color.White)

        ) {
            if(productos.itemCount==0 && !isLoadingProducts){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth().fillMaxHeight(),
                    ){
                        NotFounAnimation()
                    }

            }else{
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(
                        count = if(isLoadingProducts) 6 else productos.itemCount,
                        key = if(isLoadingProducts) null else productos.itemKey { it.id }
                    ){
                            index ->
                        if(isLoadingProducts){
                            ProductCardSkeleton()
                        }else{
                            val producto = productos[index]
                            producto?.let{
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
}

@Composable
fun SearchAndFilterBar(query: String, onQueryChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth().
                background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.weight(1f).height(50.dp),
            color = Color(0xFFF2F2F2),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) Text("Buscar productos...", color = Color.Gray)
                        innerTextField()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
                .clickable { /* Mostrar Filtros */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_filter_list),
                contentDescription = "Filtro",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@Composable
fun CatalogTabs(
    categories: List<CategoryModel>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.White,
        contentColor = TextPurple,
        edgePadding = 16.dp,
        divider = {},
        indicator = { tabPositions ->
            if (selectedTabIndex < tabPositions.size) {

                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    height = 3.dp,
                    color = TextPurple
                )
            }
        }
    ) {
        categories.forEachIndexed { index, categoria ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = categoria.nombre,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTabIndex == index) TextPurple else Color.Gray
                        )
                    )
                }
            )
        }
    }
}