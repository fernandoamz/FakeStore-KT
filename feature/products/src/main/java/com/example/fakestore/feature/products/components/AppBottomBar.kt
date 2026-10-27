package com.example.fakestore.feature.products.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

private val AssistantAccent = Color(0xFF3483FA)

enum class BottomTab(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(
        route = "products",
        title = "Inicio",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    VIDEOS(
        route = "videos",
        title = "Videos",
        selectedIcon = Icons.Filled.PlayCircle,
        unselectedIcon = Icons.Outlined.PlayCircle
    ),
    CART(
        route = "cart",
        title = "Carrito",
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart
    ),
    LLM(
        route = "llm",
        title = "IA",
        selectedIcon = Icons.Filled.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome
    ),
    MORE(
        route = "profile",
        title = "Más",
        selectedIcon = Icons.Filled.Menu,
        unselectedIcon = Icons.Outlined.Menu
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

    NavigationBar(
        containerColor = containerColor,
        tonalElevation = if (isVideoTab) 0.dp else 6.dp,
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        BottomTab.entries.forEach { tab ->
            val isSelected = currentRoute == tab.route
            val selectedColor = when {
                isVideoTab -> Color.White
                tab == BottomTab.LLM -> AssistantAccent
                else -> MaterialTheme.colorScheme.primary
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                label = {
                    Text(
                        text = tab.title,
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
                                contentDescription = tab.title,
                                tint = iconTint
                            )
                        }
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.title,
                            tint = iconTint
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    unselectedIconColor = contentColor,
                    unselectedTextColor = contentColor,
                    indicatorColor = when {
                        isVideoTab -> Color.White.copy(alpha = 0.2f)
                        tab == BottomTab.LLM -> AssistantAccent.copy(alpha = 0.12f)
                        else -> MaterialTheme.colorScheme.primaryContainer
                    }
                )
            )
        }
    }
}
