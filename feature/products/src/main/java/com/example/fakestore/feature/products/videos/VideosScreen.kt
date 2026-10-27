package com.example.fakestore.feature.products.videos

import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.fakestore.feature.products.components.AppBottomBar
import com.example.fakestore.feature.products.components.BottomTab

data class VideoClip(
    val id: Int,
    val views: String,
    val username: String,
    val videoUrl: String,
    val productName: String,
    val productPrice: String,
    val shippingInfo: String,
    val productThumbnail: String
)

val SampleVideoClips = listOf(
    VideoClip(
        id = 1,
        views = "44,824 visualizaciones",
        username = "FAKE_USER",
        videoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=900",
        productName = "Sin Quejas - Juego De Cartas Versión...",
        productPrice = "$599",
        shippingInfo = "Envío gratis",
        productThumbnail = "https://images.unsplash.com/photo-1610890716171-6b1bb98ffd09?w=300"
    ),
    VideoClip(
        id = 2,
        views = "12,910 visualizaciones",
        username = "TECH_REVIEWER_MX",
        videoUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=900",
        productName = "Smartphone Pro Max 256GB - Libre",
        productPrice = "$12,499",
        shippingInfo = "Envío gratis",
        productThumbnail = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=300"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideosScreen(
    onProductClick: (Int) -> Unit = {},
    onTabSelected: (BottomTab) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { SampleVideoClips.size })

    Scaffold(
        contentWindowInsets = com.example.fakestore.feature.products.components.ScaffoldZeroInsets,
        bottomBar = {
            AppBottomBar(
                currentRoute = BottomTab.VIDEOS.route,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
        ) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val clip = SampleVideoClips[page]
                VideoReelItem(clip = clip, onProductClick = onProductClick)
            }
        }
    }
}

@Composable
private fun VideoReelItem(
    clip: VideoClip,
    onProductClick: (Int) -> Unit
) {
    var isLiked by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Full screen video background image
        AsyncImage(
            model = clip.videoUrl,
            contentDescription = clip.username,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark Gradient Scrim Overlay at bottom for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                        startY = 500f
                    )
                )
        )

        // Right side action buttons (Favoritos, Compartir, More)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(bottom = 140.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { isLiked = !isLiked },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favoritos",
                        tint = if (isLiked) Color.Red else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = "Favoritos",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {},
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Compartir",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = "Compartir",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(
                onClick = {},
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreHoriz,
                    contentDescription = "Más opciones",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        // Bottom overlay: Views count, Username & Linked Product Floating Card
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Views & Username
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = clip.views,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = clip.username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Floating Product Card (Mercado Libre Clip Product Overlay)
            Card(
                onClick = { onProductClick(1) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Product Thumbnail
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.LightGray.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    ) {
                        AsyncImage(
                            model = clip.productThumbnail,
                            contentDescription = clip.productName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Product Details & CTA Button
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = clip.productName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = clip.productPrice,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            text = clip.shippingInfo,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF16A34A) // Green shipping text
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Primary "Comprar ahora" blue button
                            Button(
                                onClick = { onProductClick(1) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2563EB), // Mercado Libre blue button
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "Comprar ahora",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
