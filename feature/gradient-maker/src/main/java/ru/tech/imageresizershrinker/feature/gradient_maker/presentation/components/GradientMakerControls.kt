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

package ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.DensitySmall
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.AlphaSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageCounter
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.screenLogic.GradientMakerComponent
import kotlin.math.roundToInt

@Composable
internal fun GradientMakerControls(component: GradientMakerComponent) {
    var showPickImageFromUrisSheet by rememberSaveable { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageCounter(
            imageCount = component.uris.size.takeIf { it > 1 },
            onRepick = {
                showPickImageFromUrisSheet = true
            }
        )
        AnimatedContent(
            component.allowPickingImage == false
        ) { canChangeSize ->
            if (canChangeSize) {
                GradientSizeSelector(
                    value = component.gradientSize,
                    onWidthChange = component::updateWidth,
                    onHeightChange = component::updateHeight
                )
            } else {
                AlphaSelector(
                    value = component.gradientAlpha,
                    onValueChange = component::updateGradientAlpha,
                    modifier = Modifier
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        if (component.isMeshGradient) {
            Column(
                modifier = Modifier.container(
                    resultPadding = 0.dp
                )
            ) {
                Spacer(Modifier.height(16.dp))
                TitleItem(
                    text = stringResource(R.string.points_customization),
                    icon = Icons.Rounded.Build,
                    modifier = Modifier.padding(
                        horizontal = 16.dp
                    )
                )
                MeshGradientEditor(
                    state = component.meshGradientState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(16.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            EnhancedSliderItem(
                value = component.meshGradientState.gridSize,
                title = stringResource(R.string.grid_size),
                icon = Icons.Rounded.GridOn,
                valueRange = 2f..6f,
                internalStateTransformation = { it.roundToInt() },
                onValueChange = { value ->
                    if (value.roundToInt() != component.meshGradientState.gridSize) {
                        val size = value.roundToInt()
                        component.setResolution(lerp(1f, 16f, 2f / size))
                        component.meshGradientState.points.apply {
                            clear()
                            addAll(generateMesh(size))
                        }
                    }
                }
            )
            Spacer(Modifier.height(8.dp))
            EnhancedSliderItem(
                value = component.meshResolutionX,
                title = stringResource(R.string.resolution),
                icon = Icons.Rounded.DensitySmall,
                valueRange = 1f..64f,
                internalStateTransformation = { it.roundToInt() },
                onValueChange = component::setResolution
            )
        } else {
            GradientTypeSelector(
                value = component.gradientType,
                onValueChange = component::setGradientType
            ) {
                GradientPropertiesSelector(
                    gradientType = component.gradientType,
                    linearAngle = component.angle,
                    onLinearAngleChange = component::updateLinearAngle,
                    centerFriction = component.centerFriction,
                    radiusFriction = component.radiusFriction,
                    onRadialDimensionsChange = component::setRadialProperties
                )
            }
            Spacer(Modifier.height(8.dp))
            ColorStopSelection(
                colorStops = component.colorStops,
                onRemoveClick = component::removeColorStop,
                onValueChange = component::updateColorStop,
                onAddColorStop = component::addColorStop
            )
            Spacer(Modifier.height(8.dp))
            TileModeSelector(
                value = component.tileMode,
                onValueChange = component::setTileMode
            )
        }
        if (component.allowPickingImage == true) {
            Spacer(Modifier.height(8.dp))
            SaveExifWidget(
                checked = component.keepExif,
                imageFormat = component.imageFormat,
                onCheckedChange = component::toggleKeepExif
            )
        }
        Spacer(Modifier.height(8.dp))
        ImageFormatSelector(
            value = component.imageFormat,
            forceEnabled = component.allowPickingImage == false,
            onValueChange = component::setImageFormat
        )
    }

    val transformations by remember(
        component.brush,
        component.isMeshGradient,
        component.meshPoints,
        component.meshResolutionX,
        component.meshResolutionY,
        component.gradientAlpha
    ) {
        derivedStateOf {
            listOf(
                component.getGradientTransformation()
            )
        }
    }

    val isPortrait by isPortraitOrientationAsState()
    val essentials = rememberLocalEssentials()

    PickImageFromUrisSheet(
        transformations = transformations,
        visible = showPickImageFromUrisSheet,
        onDismiss = {
            showPickImageFromUrisSheet = false
        },
        uris = component.uris,
        selectedUri = component.selectedUri,
        onUriPicked = { uri ->
            component.updateSelectedUri(
                uri = uri,
                onFailure = essentials::showFailureToast
            )
        },
        onUriRemoved = { uri ->
            component.updateUrisSilently(removedUri = uri)
        },
        columns = if (isPortrait) 2 else 4,
    )
}