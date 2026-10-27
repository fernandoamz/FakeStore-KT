package com.example.fakestore.feature.products.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable

/** Scaffold topBar + bottomBar already apply system insets. */
val ScaffoldZeroInsets: WindowInsets = WindowInsets(0, 0, 0, 0)

/** Scaffold has TopAppBar only — keep bottom/horizontal safe drawing. */
@Composable
fun scaffoldTopBarOnlyInsets(): WindowInsets =
    WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)

/** Scaffold has no bars — apply full safe drawing (e.g. Login). */
@Composable
fun scaffoldSafeDrawingInsets(): WindowInsets = WindowInsets.safeDrawing
