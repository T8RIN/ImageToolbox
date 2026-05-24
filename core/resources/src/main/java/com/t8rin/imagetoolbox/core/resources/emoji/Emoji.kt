/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.core.resources.emoji

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.EmojiEmotions
import com.t8rin.imagetoolbox.core.resources.icons.EmojiEvents
import com.t8rin.imagetoolbox.core.resources.icons.EmojiFoodBeverage
import com.t8rin.imagetoolbox.core.resources.icons.EmojiNature
import com.t8rin.imagetoolbox.core.resources.icons.EmojiObjects
import com.t8rin.imagetoolbox.core.resources.icons.EmojiSymbols
import com.t8rin.imagetoolbox.core.resources.icons.EmojiTransportation
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

object Emoji {

    @Volatile
    private var _allIconsCategorized: ImmutableList<EmojiData>? = null

    @Volatile
    private var _allIcons: ImmutableList<Uri>? = null

    @Volatile
    private var _animatedIcons: Map<Uri, Uri>? = null

    val allIcons: ImmutableList<Uri> get() = _allIcons ?: persistentListOf()
    val allIconsCategorized: ImmutableList<EmojiData>
        get() = _allIconsCategorized ?: persistentListOf()

    fun animatedIconFor(icon: Uri): Uri? = _animatedIcons?.get(icon)

    fun Context.initEmoji() {
        synchronized(Emoji) {
            if (!_allIcons.isNullOrEmpty() && !_allIconsCategorized.isNullOrEmpty()) return

            val animatedIcons = mutableMapOf<Uri, Uri>()
            val categories = listOf(
                EmojiCategory(
                    title = R.string.emotions,
                    icon = Icons.Outlined.EmojiEmotions,
                    category = "emotions"
                ),
                EmojiCategory(
                    title = R.string.food_and_drink,
                    icon = Icons.Outlined.EmojiFoodBeverage,
                    category = "food"
                ),
                EmojiCategory(
                    title = R.string.nature_and_animals,
                    icon = Icons.Outlined.EmojiNature,
                    category = "nature"
                ),
                EmojiCategory(
                    title = R.string.objects,
                    icon = Icons.Outlined.EmojiObjects,
                    category = "objects"
                ),
                EmojiCategory(
                    title = R.string.activities,
                    icon = Icons.Outlined.EmojiEvents,
                    category = "events"
                ),
                EmojiCategory(
                    title = R.string.travels_and_places,
                    icon = Icons.Outlined.EmojiTransportation,
                    category = "transportation"
                ),
                EmojiCategory(
                    title = R.string.symbols,
                    icon = Icons.Rounded.EmojiSymbols,
                    category = "symbols"
                )
            ).map { category ->
                emojiData(
                    title = category.title,
                    icon = category.icon,
                    category = category.category,
                    animatedIcons = animatedIcons
                )
            }.toPersistentList()

            _allIconsCategorized = categories
            _allIcons = categories
                .flatMap(EmojiData::emojis)
                .toPersistentList()
            _animatedIcons = animatedIcons
        }
    }

    private fun Context.emojiData(
        @StringRes title: Int,
        icon: ImageVector,
        category: String,
        animatedIcons: MutableMap<Uri, Uri>
    ) = EmojiData(
        title = title,
        icon = icon,
        emojis = assets.list("lottie/$category")?.toSet().orEmpty().let { animatedFiles ->
            assets.list("svg/$category")?.toList().orEmpty()
                .sortedWith(String.CASE_INSENSITIVE_ORDER)
                .map { filename ->
                    val iconUri = "file:///android_asset/svg/$category/$filename".toUri()
                    val animatedFilename = filename.replaceAfterLast(".", "lottie")

                    if (animatedFilename in animatedFiles) {
                        animatedIcons[iconUri] =
                            "file:///android_asset/lottie/$category/$animatedFilename".toUri()
                    }

                    iconUri
                }
                .toPersistentList()
        }
    )

    private data class EmojiCategory(
        @StringRes val title: Int,
        val icon: ImageVector,
        val category: String
    )

}