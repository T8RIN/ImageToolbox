package ru.tech.imageresizershrinker.presentation.root.icons.emoji

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import ru.tech.imageresizershrinker.presentation.root.widget.image.Picture
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
        val density = Density(
            density = LocalDensity.current.density,
            fontScale = fontScale
        )
        val size = with(density) {
            fontSize.toDp()
        }
        emoji?.let {
            Box {
                Icon(
                    painter = rememberAsyncImagePainter(
                        model = emoji,
                        imageLoader = pictureImageLoader(),
                        filterQuality = FilterQuality.None
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(size)
                        .offset(1.dp, 1.dp),
                    tint = Color(0, 0, 0, 40)
                )
                Picture(
                    filterQuality = FilterQuality.High,
                    model = emoji,
                    shape = RoundedCornerShape(4.dp),
                    contentDescription = null,
                    modifier = Modifier.size(size),
                    showTransparencyChecker = false,
                )
            }
        } ?: onNoEmoji(size)
    }
}