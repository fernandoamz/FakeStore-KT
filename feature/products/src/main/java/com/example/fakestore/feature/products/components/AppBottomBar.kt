package com.example.fakestore.feature.products.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fakestore.core.common.R

enum class BottomTab(
    val route: String,
    @StringRes val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(
        route = "products",
        titleRes = R.string.navigation_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    VIDEOS(
        route = "videos",
        titleRes = R.string.navigation_videos,
        selectedIcon = Icons.Filled.PlayCircle,
        unselectedIcon = Icons.Outlined.PlayCircle
    ),
    CART(
        route = "cart",
        titleRes = R.string.navigation_cart,
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart
    ),
    MELI(
        route = "llm",
        titleRes = R.string.navigation_ai,
        selectedIcon = Icons.Filled.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome
    ),
    PROFILE(
        route = "profile",
        titleRes = R.string.navigation_profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}

@Composable
fun AppBottomBar(
    currentRoute: String,
    cartItemCount: Int = 0,
    onTabSelected: (BottomTab) -> Unit
) {
    val isVideoTab = currentRoute == "videos"
    val containerColor = if (isVideoTab) Color.Black else MaterialTheme.colorScheme.surfaceContainer
    val contentColor = if (isVideoTab) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    val selectedColor = if (isVideoTab) Color.White else MaterialTheme.colorScheme.primary

    NavigationBar(
        containerColor = containerColor,
        tonalElevation = if (isVideoTab) 0.dp else 6.dp
    ) {
        BottomTab.entries.forEach { tab ->
            val isSelected = currentRoute == tab.route
            val title = stringResource(tab.titleRes)

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                label = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) selectedColor else contentColor.copy(alpha = 0.7f)
                    )
                },
                icon = {
                    val icon = if (isSelected) tab.selectedIcon else tab.unselectedIcon
                    val iconTint = if (isSelected) selectedColor else contentColor

                    if (tab == BottomTab.CART && cartItemCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge {
                                    Text("$cartItemCount")
                                }
                            }
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = iconTint
                            )
                        }
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconTint
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    unselectedIconColor = contentColor,
                    unselectedTextColor = contentColor,
                    indicatorColor = if (isVideoTab) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
