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

import android.app.Application
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
    private var _context: Application? = null
    private val context: Context
        get() = checkNotNull(_context) {
            "Emoji.init(application) must be called before reading emoji assets."
        }

    fun Application.initEmoji() {
        _context = this
        allIcons
    }

    val allIcons: ImmutableList<Uri> by lazy {
        allIconsCategorized
            .flatMap(EmojiData::emojis)
            .toPersistentList()
    }

    val allIconsCategorized: ImmutableList<EmojiData> by lazy {
        persistentListOf(
            emojiData(
                title = R.string.emotions,
                icon = Icons.Outlined.EmojiEmotions,
                category = "emotions"
            ),
            emojiData(
                title = R.string.food_and_drink,
                icon = Icons.Outlined.EmojiFoodBeverage,
                category = "food"
            ),
            emojiData(
                title = R.string.nature_and_animals,
                icon = Icons.Outlined.EmojiNature,
                category = "nature"
            ),
            emojiData(
                title = R.string.objects,
                icon = Icons.Outlined.EmojiObjects,
                category = "objects"
            ),
            emojiData(
                title = R.string.activities,
                icon = Icons.Outlined.EmojiEvents,
                category = "events"
            ),
            emojiData(
                title = R.string.travels_and_places,
                icon = Icons.Outlined.EmojiTransportation,
                category = "transportation"
            ),
            emojiData(
                title = R.string.symbols,
                icon = Icons.Rounded.EmojiSymbols,
                category = "symbols"
            )
        )
    }

    private fun emojiData(
        @StringRes title: Int,
        icon: ImageVector,
        category: String
    ) = EmojiData(
        title = title,
        icon = icon,
        emojis = context.assets
            .list("svg/$category")?.toList().orEmpty()
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
            .map { filename ->
                "file:///android_asset/svg/$category/$filename".toUri()
            }
            .toPersistentList()
    )

}