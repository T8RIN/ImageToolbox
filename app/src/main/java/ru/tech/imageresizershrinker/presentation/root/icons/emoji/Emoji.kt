@file:Suppress("PrivatePropertyName")

package ru.tech.imageresizershrinker.presentation.root.icons.emoji

import android.content.Context
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.EmojiFoodBeverage
import androidx.compose.material.icons.outlined.EmojiNature
import androidx.compose.material.icons.outlined.EmojiObjects
import androidx.compose.material.icons.outlined.EmojiSymbols
import androidx.compose.material.icons.outlined.EmojiTransportation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import ru.tech.imageresizershrinker.R
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