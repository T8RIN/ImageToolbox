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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.text.EmojiSupportMatch
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.t8rin.imagetoolbox.core.resources.utils.defaultPdfFontFile
import com.t8rin.imagetoolbox.core.utils.appContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun rememberPdfTextStyle(): TextStyle {
    val fontFile by produceState(initialValue = LocalTextStyle.current) {
        val fontFile = withContext(Dispatchers.IO) {
            runCatching { appContext.defaultPdfFontFile }.getOrNull()
        }

        value = TextStyle(
            fontFamily = fontFile?.let {
                FontFamily(
                    Font(
                        file = it,
                        weight = FontWeight.Bold
                    )
                )
            } ?: FontFamily.Default,
            fontWeight = FontWeight.Bold,
            platformStyle = PlatformTextStyle(
                emojiSupportMatch = EmojiSupportMatch.None
            )
        )
    }

    return fontFile
}