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

package com.t8rin.imagetoolbox.texture_generation.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams

@Composable
internal fun RaymarchParams(
    value: TextureParams,
    onValueChange: (TextureParams) -> Unit
) {
    val params = value.raymarchParams
    ParamColumn {
        PreferenceRowSwitch(
            title = stringResource(R.string.texture_use_environment),
            checked = params.environment != null,
            onClick = { onValueChange(value.copy(raymarchParams = params.copy(environment = if (it) "" else null))) },
            applyHorizontalPadding = false,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = ShapeDefaults.top,
            additionalContent = {
                AnimatedVisibility(
                    visible = params.environment != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ImageSelector(
                        value = params.environment,
                        title = stringResource(R.string.texture_environment_image),
                        subtitle = null,
                        modifier = Modifier.padding(bottom = 8.dp),
                        onValueChange = {
                            onValueChange(
                                value.copy(
                                    raymarchParams = params.copy(
                                        environment = it.toString()
                                    )
                                )
                            )
                        }
                    )
                }
            }
        )
        if (value.textureFilterType == TextureFilterType.EmptiedCube3D) {
            FloatParam(
                value = params.innerRadius,
                title = stringResource(R.string.texture_inner_radius),
                range = 0.5f..1.2f,
                onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(innerRadius = it))) }
            )
        }
        ColorParam(
            title = stringResource(R.string.texture_object_color),
            value = value.foregroundColor.toColor(),
            onValueChange = { onValueChange(value.copy(foregroundColor = it.toModel())) }
        )
        ColorParam(
            title = stringResource(R.string.background_color),
            value = value.backgroundColor.toColor(),
            onValueChange = { onValueChange(value.copy(backgroundColor = it.toModel())) }
        )
        FloatParam(
            value = params.size,
            title = stringResource(R.string.texture_object_size),
            range = 0.25f..2.5f,
            onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(size = it))) }
        )
        if (value.textureFilterType !in setOf(
                TextureFilterType.Sphere3D, TextureFilterType.Cube3D,
                TextureFilterType.CubeStair3D, TextureFilterType.CubeOctahedron3D,
                TextureFilterType.EmptiedCube3D
            )
        ) {
            FloatParam(
                value = params.thickness,
                title = stringResource(R.string.texture_thickness),
                range = 0.03f..0.4f,
                onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(thickness = it))) }
            )
        }
        if (value.textureFilterType in setOf(
                TextureFilterType.Cube3D, TextureFilterType.CubeStair3D,
                TextureFilterType.EmptiedCube3D, TextureFilterType.MobiusTorus3D,
                TextureFilterType.TorusGem3D, TextureFilterType.CubeOctahedron3D,
                TextureFilterType.InfiniteStructure3D
            )
        ) {
            FloatParam(
                value = params.roundness,
                title = stringResource(R.string.texture_roundness),
                range = 0.0f..0.2f,
                onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(roundness = it))) }
            )
        }
        FloatParam(
            value = params.rotationX,
            title = stringResource(R.string.texture_rotation_x),
            range = -180.0f..180.0f,
            onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(rotationX = it))) }
        )
        FloatParam(
            value = params.rotationY,
            title = stringResource(R.string.texture_rotation_y),
            range = -180.0f..180.0f,
            onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(rotationY = it))) }
        )
        FloatParam(
            value = params.rotationZ,
            title = stringResource(R.string.texture_rotation_z),
            range = -180.0f..180.0f,
            onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(rotationZ = it))) }
        )
        FloatParam(
            value = params.cameraDistance,
            title = stringResource(R.string.texture_camera_distance),
            range = 1.5f..8.0f,
            onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(cameraDistance = it))) }
        )
        FloatParam(
            value = params.cameraYaw,
            title = stringResource(R.string.texture_camera_yaw),
            range = -180.0f..180.0f,
            onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(cameraYaw = it))) }
        )
        FloatParam(
            value = params.cameraPitch,
            title = stringResource(R.string.texture_camera_pitch),
            range = -80.0f..80.0f,
            onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(cameraPitch = it))) }
        )
        FloatParam(
            value = params.fieldOfView,
            title = stringResource(R.string.texture_field_of_view),
            range = 20.0f..90.0f,
            onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(fieldOfView = it))) }
        )
        FloatParam(
            value = params.metallic,
            title = stringResource(R.string.texture_metallic),
            range = 0.0f..1.0f,
            onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(metallic = it))) }
        )
        FloatParam(
            value = params.roughness,
            title = stringResource(R.string.texture_roughness),
            range = 0.03f..1.0f,
            onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(roughness = it))) }
        )
        FloatParam(
            value = params.transmission,
            title = stringResource(R.string.texture_transmission),
            range = 0.0f..1.0f,
            onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(transmission = it))) }
        )
        FloatParam(
            value = params.lightAngle,
            title = stringResource(R.string.texture_light_angle),
            range = -180.0f..180.0f,
            onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(lightAngle = it))) }
        )
        FloatParam(
            value = params.lightElevation,
            title = stringResource(R.string.texture_light_elevation),
            range = -80.0f..80.0f,
            onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(lightElevation = it))) },
            shape = if (value.textureFilterType == TextureFilterType.MobiusTorus3D) {
                ShapeDefaults.center
            } else ShapeDefaults.bottom
        )
        if (value.textureFilterType == TextureFilterType.MobiusTorus3D) {
            IntParam(
                value = params.count,
                title = stringResource(R.string.procedural_count),
                range = 1f..12f,
                onValueChange = { onValueChange(value.copy(raymarchParams = params.copy(count = it))) },
                shape = ShapeDefaults.bottom
            )
        }
    }
}