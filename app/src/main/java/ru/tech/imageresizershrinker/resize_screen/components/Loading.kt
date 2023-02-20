package ru.tech.imageresizershrinker.resize_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun BoxScope.Loading() {
    Box(
        Modifier
            .size(84.dp)
            .clip(RoundedCornerShape(24.dp))
            .shadow(
                8.dp,
                RoundedCornerShape(24.dp)
            )
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .align(Alignment.Center)
    ) {
        CircularProgressIndicator(
            Modifier.align(
                Alignment.Center
            ),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun LoadingDialog() {
    Dialog(onDismissRequest = { }) {
        Box(Modifier.fillMaxSize()) { Loading() }
    }
}