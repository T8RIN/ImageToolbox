package ru.tech.imageresizershrinker.widget.image

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.BrokenImage
import androidx.compose.material.icons.twotone.Image
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.modifier.block
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState

@Composable
fun ImageNotPickedWidget(
    modifier: Modifier = Modifier,
    onPickImage: () -> Unit,
    text: String = stringResource(R.string.pick_image),
) {
    val settingsState = LocalSettingsState.current
    Column(
        modifier = modifier.block(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        FilledIconButton(
            onClick = onPickImage,
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(16.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(
                Icons.TwoTone.Image,
                null,
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        settingsState.borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(0.2f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text,
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