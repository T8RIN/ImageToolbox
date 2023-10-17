package ru.tech.imageresizershrinker.presentation.root.icons.emoji

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.shimmer
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalImageLoader

@Composable
fun EmojiItem(
    emoji: String?,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = LocalTextStyle.current.fontSize,
    fontScale: Float,
    onNoEmoji: @Composable (size: Dp) -> Unit = {}
) {
    val context = LocalContext.current
    val dens = LocalDensity.current
    val density = remember(dens) {
        Density(
            density = dens.density,
            fontScale = fontScale
        )
    }
    var shimmering by remember { mutableStateOf(true) }
    val painter = rememberAsyncImagePainter(
        model = remember(emoji) {
            derivedStateOf {
                ImageRequest.Builder(context)
                    .data(emoji)
                    .listener(
                        onSuccess = { _, _ ->
                            shimmering = false
                        }
                    )
                    .crossfade(true)
                    .build()
            }
        }.value,
        imageLoader = LocalImageLoader.current
    )

    AnimatedContent(
        targetState = emoji to fontSize,
        modifier = modifier
    ) { (emoji, fontSize) ->
        val size by remember(fontSize, density) {
            derivedStateOf {
                with(density) {
                    fontSize.toDp()
                }
            }
        }
        emoji?.let {
            Box {
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
                    modifier = Modifier
                        .size(size)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmer(shimmering),
                    tint = Color.Unspecified
                )
            }
        } ?: onNoEmoji(size)
    }
}