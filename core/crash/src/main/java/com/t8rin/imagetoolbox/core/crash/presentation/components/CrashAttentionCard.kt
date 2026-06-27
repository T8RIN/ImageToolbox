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

package com.t8rin.imagetoolbox.core.crash.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.EmojiItem

@Composable
internal fun CrashAttentionCard(
    title: String,
    description: String,
    emoji: String
) {
    val isNightMode = LocalSettingsState.current.isNightMode
    Spacer(modifier = Modifier.height(16.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .container(
                shape = ShapeDefaults.large,
                resultPadding = 16.dp,
                color = takeColorFromScheme {
                    if (isNightMode) {
                        errorContainer.blend(surfaceContainerLow, 0.75f)
                    } else {
                        errorContainer.blend(surfaceContainerLow, 0.65f)
                    }
                }
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val contentColor = takeColorFromScheme {
            if (isNightMode) {
                onError.blend(onSurface, 0.75f)
            } else {
                error.blend(onSurface, 0.6f)
            }
        }
        EmojiItem(
            emoji = "",
            fontSize = 0.sp,
            animatedEmoji = emoji,
            shape = ShapeDefaults.default,
            modifier = Modifier
                .size(80.dp)
                .statusBarsPadding(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            lineHeight = 26.sp,
            color = contentColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            color = contentColor
        )
    }
}