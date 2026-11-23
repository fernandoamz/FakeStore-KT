package com.example.fakestore.feature.products.catalog

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.fakestore.domain.model.Product
import com.example.fakestore.feature.products.address.presentation.components.PostalCodeBottomSheet
import com.example.fakestore.feature.products.cart.CartViewModel
import com.example.fakestore.feature.products.components.BottomTab
import com.example.fakestore.feature.products.components.HeroBanner
import com.example.fakestore.feature.products.components.ProductCardSkeleton
import com.example.fakestore.feature.products.components.RatingBar
import com.example.fakestore.feature.products.components.YellowECommerceHeader
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    onProductClick: (Int) -> Unit,
    onTabSelected: (BottomTab) -> Unit,
    onProfileClick: () -> Unit,
    onCameraClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cartUiState by cartViewModel.uiState.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var showAddressBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Determine collapse state for YellowECommerceHeader based on scroll offset with derivedStateOf
    val isCollapsed by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 30
        }
    }

    // Automatic Infinite Scroll Pagination Trigger
    LaunchedEffect(listState, uiState) {
        val currentState = uiState
        if (currentState is ProductsUiState.Success) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .collect { lastVisibleIndex ->
                    if (lastVisibleIndex != null) {
                        val totalItems = listState.layoutInfo.totalItemsCount
                        if (lastVisibleIndex >= totalItems - 3 &&
                            currentState.hasMorePages &&
                            !currentState.isLoadingMore &&
                            currentState.selectedCategory == null &&
                            currentState.searchQuery.isEmpty()
                        ) {
                            viewModel.loadMoreProducts()
                        }
                    }
                }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            YellowECommerceHeader(
                searchQuery = when (uiState) {
                    is ProductsUiState.Success -> (uiState as ProductsUiState.Success).searchQuery
                    else -> ""
                },
                onSearchQueryChange = viewModel::onSearchQueryChange,
                selectedCategory = when (uiState) {
                    is ProductsUiState.Success -> (uiState as ProductsUiState.Success).selectedCategory
                    else -> null
                },
                categories = when (uiState) {
                    is ProductsUiState.Success -> (uiState as ProductsUiState.Success).categories
                    else -> emptyList()
                },
                onCategorySelected = { category ->
                    viewModel.selectCategory(category)
                },
                isCollapsed = isCollapsed,
                onProfileClick = onProfileClick,
                onNotificationClick = {
                    scope.launch { snackbarHostState.showSnackbar("7 notificaciones nuevas") }
                },
                onLocationClick = {
                    showAddressBottomSheet = true
                },
                onCameraClick = onCameraClick,
                onSearchClick = onSearchClick,
                searchHistory = searchHistory,
                onHistoryItemClick = { query ->
                    viewModel.onSearchQueryChange(query)
                },
                onFavoritesClick = {
                    viewModel.selectCategory("__FAVORITES__")
                },
                isFavoriteSelected = when (uiState) {
                    is ProductsUiState.Success -> (uiState as ProductsUiState.Success).selectedCategory == "__FAVORITES__"
                    else -> false
                }
            )
        },
        bottomBar = {
            com.example.fakestore.feature.products.components.AppBottomBar(
                currentRoute = BottomTab.HOME.route,
                cartItemCount = cartUiState.totalItems,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->

        when (val state = uiState) {
            is ProductsUiState.Loading -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(3) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) { ProductCardSkeleton() }
                            Box(modifier = Modifier.weight(1f)) { ProductCardSkeleton() }
                        }
                    }
                }
            }

            is ProductsUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = viewModel::loadData) {
                        Text("Reintentar")
                    }
                }
            }

            is ProductsUiState.Success -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Full-width Hero Banner Slider (Only shown on "Todos" view)
                    if (state.selectedCategory == null && state.searchQuery.isEmpty()) {
                        item(key = "hero_banner") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                HeroBanner(
                                    products = state.products,
                                    onProductClick = onProductClick
                                )
                            }
                        }
                    }

                    if (state.filteredProducts.isEmpty()) {
                        item(key = "empty_state") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (state.selectedCategory == "__FAVORITES__")
                                        "No tienes productos favoritos aún"
                                    else
                                        "No se encontraron productos"
                                )
                            }
                        }
                    } else {
                        // Products Chunked in Rows of 2 with stable keys
                        items(
                            items = state.filteredProducts.chunked(2),
                            key = { row -> row.joinToString("_") { it.id.toString() } }
                        ) { rowProducts ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                for (product in rowProducts) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        ProductCard(
                                            product = product,
                                            isFavorite = state.favoriteProductIds.contains(product.id),
                                            onFavoriteToggle = { viewModel.toggleFavorite(product.id) },
                                            onAddToCart = {
                                                cartViewModel.addProduct(
                                                    com.example.fakestore.domain.model.CartItem(product = product, quantity = 1)
                                                )
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("¡${product.title} agregado al carrito!")
                                                }
                                            },
                                            onClick = { onProductClick(product.id) }
                                        )
                                    }
                                }
                                if (rowProducts.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        // Automatic Infinite Scroll Loading Indicator (at bottom)
                        if (state.isLoadingMore && state.hasMorePages && state.selectedCategory == null && state.searchQuery.isEmpty()) {
                            item(key = "infinite_scroll_loading") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                        Text(
                                            text = "Cargando más productos...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Transparent Action Sheet / Bottom Sheet with crisp black outline borders
        if (showAddressBottomSheet) {
            PostalCodeBottomSheet(
                onDismissRequest = { showAddressBottomSheet = false },
                onConfirmPostalCode = { code ->
                    showAddressBottomSheet = false
                    scope.launch {
                        snackbarHostState.showSnackbar("Código postal $code guardado")
                    }
                },
                sheetState = sheetState
            )
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onAddToCart: () -> Unit,
    onClick: () -> Unit
) {
    val heartColor by animateColorAsState(
        targetValue = if (isFavorite) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "heartColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heartScale"
    )

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentScale = ContentScale.Fit
                )

                // Enhanced Favorite Heart Toggle Button with Glassmorphic Circle Backdrop
                Surface(
                    onClick = onFavoriteToggle,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .scale(scale)
                        .size(36.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = heartColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                RatingBar(
                    rating = product.rating.rate,
                    count = product.rating.count
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$${"%.2f".format(product.price)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (product.discountPercentage > 0) {
                            Text(
                                text = "$${"%.2f".format(product.originalPrice)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }

                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AddShoppingCart,
                            contentDescription = "Añadir rápido",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
