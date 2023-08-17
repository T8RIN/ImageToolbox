package ru.tech.imageresizershrinker.presentation.root.theme

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import ru.tech.imageresizershrinker.presentation.root.widget.image.Picture
import ru.tech.imageresizershrinker.presentation.root.widget.image.pictureImageLoader
import java.io.File


object Emoji

private var EmojiList: List<Uri>? = null

private fun Context.listAssetFiles(path: String): List<String> {
    return assets.list(path)?.toMutableList()?.apply {
        this.forEach {
            getFileFromAssets(it)
        }
        remove("sparkles.svg")
    } ?: emptyList()
}

private fun Context.getFileFromAssets(fileName: String): Uri =
    File(getDir("svg", Context.MODE_PRIVATE), fileName)
        .also {
            if (!it.exists()) {
                it.outputStream().use { cache ->
                    assets.open("svg/$fileName").use { inputStream ->
                        inputStream.copyTo(cache)
                    }
                }
            }
        }.toUri()

val Emoji.allIcons: List<Uri>
    @Synchronized
    @Composable
    get() {
        val context = LocalContext.current
        EmojiList?.let { return it }
        EmojiList = listOf(Emoji.Sparkles) + context
            .listAssetFiles("svg")
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
            .map { context.getFileFromAssets(it) }
        return remember { EmojiList!! }
    }

val Emoji.Sparkles: Uri
    @Composable
    get() = LocalContext.current.getFileFromAssets("sparkles.svg")

@Composable
fun EmojiItem(
    emoji: Uri?,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = LocalTextStyle.current.fontSize,
    onNoEmoji: @Composable (size: Dp) -> Unit = {}
) {
    AnimatedContent(
        targetState = emoji to fontSize,
        modifier = modifier
    ) { (emoji, fontSize) ->
        val size = with(LocalDensity.current) { fontSize.toDp() }
        emoji?.let {
            Box {
                Icon(
                    painter = rememberAsyncImagePainter(
                        model = emoji,
                        imageLoader = pictureImageLoader()
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(size)
                        .offset(1.dp, 1.dp),
                    tint = Color(0, 0, 0, 40)
                )
                Picture(
                    model = emoji,
                    shape = RoundedCornerShape(2.dp),
                    contentDescription = null,
                    modifier = Modifier.size(size)
                )
            }
        } ?: onNoEmoji(size)
    }
}