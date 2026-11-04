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

package com.t8rin.imagetoolbox.feature.emoji_kitchen.domain

data class EmojiKitchenState(
    val isLoading: Boolean = true,
    val collection: EmojiKitchenCollection? = null,
    val progress: EmojiKitchenProgress? = null
)

data class EmojiKitchenCollection(
    val emojis: List<EmojiKitchenEmoji>,
    val combinationCount: Int,
    val imageCount: Int,
    val size: Long
)

data class EmojiKitchenEmoji(
    val id: String,
    val emoji: String,
    val name: String,
    val category: EmojiKitchenCategory,
    val imageUri: String,
    val keywords: List<String> = emptyList()
)

data class EmojiKitchenVariant(
    val uri: String,
    val date: String,
    val isLatest: Boolean
)

data class EmojiKitchenCombination(
    val otherEmojiId: String,
    val variant: EmojiKitchenVariant
)

data class EmojiKitchenProgress(
    val stage: Stage,
    val completed: Long = 0,
    val total: Long = 0,
    val source: Stage = stage
) {
    val fraction: Float?
        get() = total.takeIf { it > 0 }?.let {
            (completed.toFloat() / it).coerceIn(0f, 1f)
        }

    enum class Stage {
        Downloading,
        Importing,
        Installing
    }
}