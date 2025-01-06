/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.ai_upscale.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageSelector
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.feature.ai_upscale.presentation.screenLogic.AiUpscaleComponent

@Composable
fun AiUpscaleContent(
    component: AiUpscaleComponent
) {
    var value by remember {
        mutableStateOf<Any?>(null)
    }
    val essentials = rememberLocalEssentials()

    Column(
        modifier = Modifier.safeDrawingPadding()
    ) {
        ImageSelector(
            value = component.uris.firstOrNull(),
            onValueChange = { component.setUris(listOf(it)) },
            subtitle = null
        )
        Row(
            modifier = Modifier.weight(1f)
        ) {
            Picture(
                model = component.uris.firstOrNull(),
                modifier = Modifier.weight(1f),
                contentScale = ContentScale.Inside
            )
            Picture(
                model = value,
                modifier = Modifier.weight(1f),
                contentScale = ContentScale.Inside
            )
        }
        Button(
            onClick = {
                component.preformUpscale(
                    onSuccess = {
                        value = it
                    },
                    onFailure = essentials::showFailureToast
                )
            }
        ) {
            Text("AIIIII")
        }
    }
}