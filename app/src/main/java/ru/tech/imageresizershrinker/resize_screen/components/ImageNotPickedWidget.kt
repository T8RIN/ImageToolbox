package ru.tech.imageresizershrinker.resize_screen.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.BrokenImage
import androidx.compose.material.icons.twotone.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.block

@Composable
fun ImageNotPickedWidget(onPickImage: () -> Unit) {
    Column(
        Modifier.block(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        FilledIconButton(
            onClick = onPickImage,
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(16.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                Icons.TwoTone.Image,
                null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            stringResource(R.string.pick_image),
            Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BadImageWidget() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .block()
            .padding(8.dp)
    ) {
        Text(
            stringResource(R.string.image_too_large_preview),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Icon(
            Icons.TwoTone.BrokenImage,
            null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun Picture(bitmap: Bitmap?, visible: Boolean = true) {
    bitmap?.asImageBitmap()
        ?.takeIf { visible }
        ?.let {
            Box(
                modifier = Modifier.block(RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier.clip(RoundedCornerShape(4.dp))
                )
            }
        }
}