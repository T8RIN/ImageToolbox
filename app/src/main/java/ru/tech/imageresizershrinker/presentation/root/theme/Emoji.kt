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
import androidx.compose.material.icons.outlined.EmojiFoodBeverage
import androidx.compose.material.icons.outlined.EmojiNature
import androidx.compose.material.icons.outlined.EmojiObjects
import androidx.compose.material.icons.outlined.EmojiSymbols
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        val context = LocalContext.current
        if (
            listOf(
                Emotions,
                Food,
                Nature,
                Objects,
                Symbols
            ).all { it != null }
        ) return (Emotions!! + Food!! + Nature!! + Objects!! + Symbols!!)
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
        Symbols = context
            .listAssetFiles("svg/symbols")
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
            .map { context.getFileFromAssets("symbols", it) }
        return remember { (Emotions!! + Food!! + Nature!! + Objects!! + Symbols!!) }
    }

val Emoji.allIconsCategorized: List<EmojiData>
    @Synchronized
    @Composable
    get() {
        val context = LocalContext.current
        if (
            listOf(
                Emotions,
                Food,
                Nature,
                Objects,
                Symbols
            ).all { it != null }
        ) return listOf(
            EmojiData(stringResource(R.string.emotions), Icons.Outlined.EmojiEmotions, Emotions!!),
            EmojiData(
                stringResource(R.string.food_and_drink),
                Icons.Outlined.EmojiFoodBeverage,
                Food!!
            ),
            EmojiData(
                stringResource(R.string.nature_and_animals),
                Icons.Outlined.EmojiNature,
                Nature!!
            ),
            EmojiData(stringResource(R.string.objects), Icons.Outlined.EmojiObjects, Objects!!),
            EmojiData(stringResource(R.string.symbols), Icons.Outlined.EmojiSymbols, Symbols!!)
        )
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
        Symbols = context
            .listAssetFiles("svg/symbols")
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
            .map { context.getFileFromAssets("symbols", it) }
        return listOf(
            EmojiData(stringResource(R.string.emotions), Icons.Outlined.EmojiEmotions, Emotions!!),
            EmojiData(
                stringResource(R.string.food_and_drink),
                Icons.Outlined.EmojiFoodBeverage,
                Food!!
            ),
            EmojiData(
                stringResource(R.string.nature_and_animals),
                Icons.Outlined.EmojiNature,
                Nature!!
            ),
            EmojiData(stringResource(R.string.objects), Icons.Outlined.EmojiObjects, Objects!!),
            EmojiData(stringResource(R.string.symbols), Icons.Outlined.EmojiSymbols, Symbols!!)
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