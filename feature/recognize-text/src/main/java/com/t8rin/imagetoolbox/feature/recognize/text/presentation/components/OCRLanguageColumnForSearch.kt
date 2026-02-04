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

package com.t8rin.imagetoolbox.feature.recognize.text.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.feature.recognize.text.domain.OCRLanguage
import com.t8rin.imagetoolbox.feature.recognize.text.domain.RecognitionType

@Composable
internal fun OCRLanguageColumnForSearch(
    languagesForSearch: List<OCRLanguage>,
    value: List<OCRLanguage>,
    currentRecognitionType: RecognitionType,
    onValueChange: (List<OCRLanguage>, RecognitionType) -> Unit,
    allowMultipleLanguagesSelection: Boolean
) {
    fun onValueChangeImpl(
        selected: Boolean,
        type: RecognitionType,
        lang: OCRLanguage
    ) {
        if (allowMultipleLanguagesSelection) {
            if (selected) {
                onValueChange(
                    (value - lang).distinct(),
                    type
                )
            } else onValueChange(
                (value + lang).distinct(),
                type
            )
        } else onValueChange(listOf(lang), type)
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        flingBehavior = enhancedFlingBehavior()
    ) {
        itemsIndexed(
            items = languagesForSearch,
            key = { _, l -> l.code }
        ) { index, lang ->
            val selected by remember(value, lang) {
                derivedStateOf {
                    lang in value
                }
            }
            PreferenceItem(
                title = lang.name,
                subtitle = lang.localizedName.takeIf { it != lang.name },
                onClick = {
                    onValueChangeImpl(
                        selected = selected,
                        type = currentRecognitionType,
                        lang = lang
                    )
                },
                containerColor = animateColorAsState(
                    if (selected) {
                        MaterialTheme.colorScheme.surfaceColorAtElevation(
                            20.dp
                        )
                    } else EnhancedBottomSheetDefaults.contentContainerColor
                ).value,
                shape = ShapeDefaults.byIndex(
                    index = index,
                    size = languagesForSearch.size
                ),
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
            )
        }
    }
}