@file:Suppress("PrivatePropertyName")

package ru.tech.imageresizershrinker.presentation.root.theme

import android.content.Context
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.EmojiFoodBeverage
import androidx.compose.material.icons.outlined.EmojiNature
import androidx.compose.material.icons.outlined.EmojiObjects
import androidx.compose.material.icons.outlined.EmojiSymbols
import androidx.compose.material.icons.outlined.EmojiTransportation
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.widget.image.Picture
import ru.tech.imageresizershrinker.presentation.root.widget.image.pictureImageLoader
import java.io.File


object Emoji

private var Emotions: List<Uri>? = null
private var Food: List<Uri>? = null
private var Nature: List<Uri>? = null
private var Objects: List<Uri>? = null
private var Events: List<Uri>? = null
private var Transportation: List<Uri>? = null
private var Symbols: List<Uri>? = null

private fun Context.listAssetFiles(path: String): List<String> {
    return assets.list(path)?.toMutableList() ?: emptyList()
}

private fun Context.getFileFromAssets(cat: String, filename: String): Uri =
    File(getDir(cat, Context.MODE_PRIVATE), filename)
        .also {
            if (!it.exists()) {
                it.outputStream().use { cache ->
                    assets.open("svg/$cat/$filename").use { inputStream ->
                        inputStream.copyTo(cache)
                    }
                }
            }
        }.toUri()

val Emoji.allIcons: List<Uri>
    @Synchronized
    @Composable
    get() {
        initializeEmojis(LocalContext.current)
        return remember { (Emotions!! + Food!! + Nature!! + Objects!! + Events!! + Transportation!! + Symbols!!) }
    }

private fun initializeEmojis(context: Context) {
    if (
        !listOf(
            Emotions,
            Food,
            Nature,
            Objects,
            Events,
            Transportation,
            Symbols
        ).all { it != null }
    ) {
        Emotions = context
            .listAssetFiles("svg/emotions")
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
            .map { context.getFileFromAssets("emotions", it) }
        Food = context
            .listAssetFiles("svg/food")
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
            .map { context.getFileFromAssets("food", it) }
        Nature = context
            .listAssetFiles("svg/nature")
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
            .map { context.getFileFromAssets("nature", it) }
        Objects = context
            .listAssetFiles("svg/objects")
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
            .map { context.getFileFromAssets("objects", it) }
        Events = context
            .listAssetFiles("svg/events")
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
            .map { context.getFileFromAssets("events", it) }
        Transportation = context
            .listAssetFiles("svg/transportation")
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
            .map { context.getFileFromAssets("transportation", it) }
        Symbols = context
            .listAssetFiles("svg/symbols")
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
            .map { context.getFileFromAssets("symbols", it) }
    }
}

val Emoji.allIconsCategorized: List<EmojiData>
    @Synchronized
    @Composable
    get() {
        initializeEmojis(LocalContext.current)
        return listOf(
            EmojiData(
                title = stringResource(R.string.emotions),
                icon = Icons.Outlined.EmojiEmotions,
                emojis = Emotions!!
            ),
            EmojiData(
                title = stringResource(R.string.food_and_drink),
                icon = Icons.Outlined.EmojiFoodBeverage,
                emojis = Food!!
            ),
            EmojiData(
                title = stringResource(R.string.nature_and_animals),
                icon = Icons.Outlined.EmojiNature,
                emojis = Nature!!
            ),
            EmojiData(
                title = stringResource(R.string.objects),
                icon = Icons.Outlined.EmojiObjects,
                emojis = Objects!!
            ),
            EmojiData(
                title = stringResource(R.string.activities),
                icon = Icons.Outlined.EmojiEvents,
                emojis = Events!!
            ),
            EmojiData(
                stringResource(R.string.travels_and_places),
                Icons.Outlined.EmojiTransportation,
                Transportation!!
            ),
            EmojiData(
                title = stringResource(R.string.symbols),
                icon = Icons.Outlined.EmojiSymbols,
                emojis = Symbols!!
            )
        )
    }

data class EmojiData(
    val title: String,
    val icon: ImageVector,
    val emojis: List<Uri>
)

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
                    filterQuality = FilterQuality.None,
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