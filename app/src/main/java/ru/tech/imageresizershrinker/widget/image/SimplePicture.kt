package ru.tech.imageresizershrinker.widget.image

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.utils.modifier.block

@Composable
fun SimplePicture(
    bitmap: Bitmap?,
    scale: ContentScale = ContentScale.Inside,
    modifier: Modifier = Modifier,
    boxModifier: Modifier = Modifier,
    loading: Boolean = false,
    visible: Boolean = true
) {
    bitmap?.asImageBitmap()
        ?.takeIf { visible }
        ?.let {
            Box(
                modifier = boxModifier
                    .block()
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = it,
                    contentScale = scale,
                    contentDescription = null,
                    modifier = modifier
                        .clip(MaterialTheme.shapes.medium)
                        .shimmer(loading, MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp))
                )
            }
        }
}