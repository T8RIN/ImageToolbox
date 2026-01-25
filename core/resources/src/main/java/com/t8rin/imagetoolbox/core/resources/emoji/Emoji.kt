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
import android.content.res.Resources
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.resources.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

object Emoji {
    private var Emotions: List<Uri>? = null
    private var Food: List<Uri>? = null
    private var Nature: List<Uri>? = null
    private var Objects: List<Uri>? = null
    private var Events: List<Uri>? = null
    private var Transportation: List<Uri>? = null
    private var Symbols: List<Uri>? = null

    @Composable
    fun allIcons(
        context: Context = LocalContext.current
    ): ImmutableList<Uri> = remember {
        derivedStateOf {
            initializeEmojis(context)
            (Emotions!! + Food!! + Nature!! + Objects!! + Events!! + Transportation!! + Symbols!!).toPersistentList()
        }.value
    }

    @Composable
    fun allIconsCategorized(
        context: Context = LocalContext.current,
        resources: Resources = LocalResources.current
    ): ImmutableList<EmojiData> = remember {
        derivedStateOf {
            initializeEmojis(context)
            persistentListOf(
                EmojiData(
                    title = resources.getString(R.string.emotions),
                    icon = Icons.Outlined.EmojiEmotions,
                    emojis = Emotions!!
                ),
                EmojiData(
                    title = resources.getString(R.string.food_and_drink),
                    icon = Icons.Outlined.EmojiFoodBeverage,
                    emojis = Food!!
                ),
                EmojiData(
                    title = resources.getString(R.string.nature_and_animals),
                    icon = Icons.Outlined.EmojiNature,
                    emojis = Nature!!
                ),
                EmojiData(
                    title = resources.getString(R.string.objects),
                    icon = Icons.Outlined.EmojiObjects,
                    emojis = Objects!!
                ),
                EmojiData(
                    title = resources.getString(R.string.activities),
                    icon = Icons.Outlined.EmojiEvents,
                    emojis = Events!!
                ),
                EmojiData(
                    title = resources.getString(R.string.travels_and_places),
                    icon = Icons.Outlined.EmojiTransportation,
                    emojis = Transportation!!
                ),
                EmojiData(
                    title = resources.getString(R.string.symbols),
                    icon = Icons.Outlined.EmojiSymbols,
                    emojis = Symbols!!
                )
            )
        }.value
    }

    private fun Context.listAssetFiles(
        path: String
    ): List<String> = assets
        .list(path)
        ?.toMutableList() ?: emptyList()

    /**
     * Generates Uri of the assets path.
     */
    private fun getFileFromAssets(
        cat: String,
        filename: String
    ): Uri = "file:///android_asset/svg/$cat/$filename".toUri()

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
                .map {
                    getFileFromAssets(
                        cat = "emotions",
                        filename = it
                    )
                }
            Food = context
                .listAssetFiles("svg/food")
                .sortedWith(String.CASE_INSENSITIVE_ORDER)
                .map {
                    getFileFromAssets(
                        cat = "food",
                        filename = it
                    )
                }
            Nature = context
                .listAssetFiles("svg/nature")
                .sortedWith(String.CASE_INSENSITIVE_ORDER)
                .map {
                    getFileFromAssets(
                        cat = "nature",
                        filename = it
                    )
                }
            Objects = context
                .listAssetFiles("svg/objects")
                .sortedWith(String.CASE_INSENSITIVE_ORDER)
                .map {
                    getFileFromAssets(
                        cat = "objects",
                        filename = it
                    )
                }
            Events = context
                .listAssetFiles("svg/events")
                .sortedWith(String.CASE_INSENSITIVE_ORDER)
                .map {
                    getFileFromAssets(
                        cat = "events",
                        filename = it
                    )
                }
            Transportation = context
                .listAssetFiles("svg/transportation")
                .sortedWith(String.CASE_INSENSITIVE_ORDER)
                .map {
                    getFileFromAssets(
                        cat = "transportation",
                        filename = it
                    )
                }
            Symbols = context
                .listAssetFiles("svg/symbols")
                .sortedWith(String.CASE_INSENSITIVE_ORDER)
                .map {
                    getFileFromAssets(
                        cat = "symbols",
                        filename = it
                    )
                }
        }
    }
}