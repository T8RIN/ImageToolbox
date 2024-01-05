package ru.tech.imageresizershrinker.core.ui.icons.emoji

import android.net.Uri
import androidx.compose.ui.graphics.vector.ImageVector

data class EmojiData(
    val title: String,
    val icon: ImageVector,
    val emojis: List<Uri>
)