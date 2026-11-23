package com.example.fakestore.feature.products.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fakestore.domain.model.SearchHistory

val BrightYellow = Color(0xFFFFE600)
val DarkCharcoal = Color(0xFF1F1F1F)

@Composable
fun YellowECommerceHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String?,
    categories: List<String>,
    onCategorySelected: (String?) -> Unit,
    isCollapsed: Boolean,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onLocationClick: () -> Unit,
    onCameraClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    isFavoriteSelected: Boolean = false,
    searchHistory: List<SearchHistory> = emptyList(),
    onHistoryItemClick: (String) -> Unit = {},
    onClearHistory: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Smooth GPU-accelerated collapse progress (0f = expanded, 1f = collapsed)
    val collapseProgress by animateFloatAsState(
        targetValue = if (isCollapsed) 1f else 0f,
        animationSpec = spring(stiffness = 300f, dampingRatio = 0.8f),
        label = "headerCollapse"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BrightYellow)
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top Search Row (Always stable & pinned)
        TopSearchRow(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onProfileClick = onProfileClick,
            onCameraClick = onCameraClick,
            onNotificationClick = onNotificationClick,
            onSearchClick = onSearchClick,
            onFavoritesClick = onFavoritesClick,
            isFavoriteSelected = isFavoriteSelected
        )

        // Recent Search Chips Row right under search bar when query is blank & history exists
        if (searchQuery.isBlank() && searchHistory.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Text(
                        text = "Recientes:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                }
                items(searchHistory, key = { it.id }) { history ->
                    Surface(
                        onClick = { onHistoryItemClick(history.query) },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.Gray
                            )
                            Text(
                                text = history.query,
                                style = MaterialTheme.typography.labelMedium,
                                color = DarkCharcoal
                            )
                        }
                    }
                }
            }
        }

        // Subheader & Category Bar with GPU-accelerated alpha & translation (Zero layout jank)
        if (collapseProgress < 1f) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .graphicsLayer {
                        alpha = 1f - collapseProgress
                        translationY = -collapseProgress * 30f
                    },
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Location / Postal Code Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onLocationClick)
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = DarkCharcoal,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "CP 28869  >",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                }

                // Category Bar: Stable curated categories
                val displayCategories = listOf("Beauty", "Smartphones", "Laptops", "Furniture")

                LazyRow(
                    contentPadding = PaddingValues(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        val isAllSelected = selectedCategory == null && !isFavoriteSelected
                        CategoryChip(
                            text = "All",
                            isSelected = isAllSelected,
                            onClick = { onCategorySelected(null) }
                        )
                    }

                    items(displayCategories) { category ->
                        val isSelected = selectedCategory.equals(category, ignoreCase = true)
                        CategoryChip(
                            text = category,
                            isSelected = isSelected,
                            onClick = { onCategorySelected(if (isSelected) null else category.lowercase()) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopSearchRow(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onProfileClick: () -> Unit,
    onCameraClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    isFavoriteSelected: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            onClick = onProfileClick,
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.12f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "FA",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // Search Input Surface clickable to open dedicated SearchScreen
        Surface(
            onClick = onSearchClick,
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = if (searchQuery.isEmpty()) "Buscar en Fake Store" else searchQuery,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (searchQuery.isEmpty()) Color.Gray else Color.Black,
                        maxLines = 1
                    )
                }
            }
        }

        // Favorites Icon Button
        IconButton(
            onClick = onFavoritesClick,
            modifier = Modifier.size(38.dp)
        ) {
            Icon(
                imageVector = if (isFavoriteSelected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favoritos",
                tint = if (isFavoriteSelected) Color.Red else Color.Black,
                modifier = Modifier.size(22.dp)
            )
        }

        IconButton(
            onClick = {
                Log.d("YellowECommerceHeader", "Camera icon clicked!")
                onCameraClick()
            },
            modifier = Modifier.size(38.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Escanear",
                tint = Color.Black,
                modifier = Modifier.size(22.dp)
            )
        }

        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier.size(38.dp)
        ) {
            BadgedBox(
                badge = {
                    Badge(containerColor = Color.Red, contentColor = Color.White) {
                        Text("7")
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = DarkCharcoal.copy(alpha = if (isSelected) 1.0f else 0.8f)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .height(2.5.dp)
                .width(if (text == "All") 22.dp else 28.dp)
                .background(
                    color = if (isSelected) DarkCharcoal else Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}
