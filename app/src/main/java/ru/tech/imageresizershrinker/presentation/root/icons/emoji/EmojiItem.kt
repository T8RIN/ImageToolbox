package ru.tech.imageresizershrinker.presentation.root.icons.emoji

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import ru.tech.imageresizershrinker.presentation.root.widget.image.pictureImageLoader

@Composable
fun EmojiItem(
    emoji: Uri?,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = LocalTextStyle.current.fontSize,
    fontScale: Float,
    onNoEmoji: @Composable (size: Dp) -> Unit = {}
) {
    AnimatedContent(
        targetState = emoji to fontSize,
        modifier = modifier
    ) { (emoji, fontSize) ->
        val dens = LocalDensity.current
        val density = remember(dens) {
            Density(
                density = dens.density,
                fontScale = fontScale
            )
        }
        val size by remember(fontSize, density) {
            derivedStateOf {
                with(density) {
                    fontSize.toDp()
                }
            }
        }
        emoji?.let {
            Box {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(emoji)
                        .crossfade(true)
                        .build(),
                    imageLoader = pictureImageLoader()
                )
                Icon(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(size)
                        .offset(1.dp, 1.dp),
                    tint = Color(0, 0, 0, 40)
                )
                Icon(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(size),
                    tint = Color.Unspecified
                )
            }
        } ?: onNoEmoji(size)
    }
}