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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.RoundedCorner
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.settings.domain.model.ShapeType
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
fun ShapeTypeSettingItem(
    onValueChange: (ShapeType) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    var showSheet by rememberSaveable {
        mutableStateOf(false)
    }

    PreferenceItem(
        modifier = modifier,
        title = stringResource(id = R.string.shapes_type),
        startIcon = Icons.Rounded.RoundedCorner,
        subtitle = stringResource(settingsState.shapesType.title()),
        onClick = {
            showSheet = true
        },
        shape = shape,
        endIcon = Icons.Rounded.MiniEdit
    )

    EnhancedModalBottomSheet(
        visible = showSheet,
        onDismiss = { showSheet = it },
        title = {
            TitleItem(
                text = stringResource(id = R.string.shape_type),
                icon = Icons.Rounded.RoundedCorner
            )
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    showSheet = false
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            val entries = ShapeType.entries

            entries.forEachIndexed { index, type ->
                val selected = type::class.isInstance(settingsState.shapesType)
                PreferenceItemOverload(
                    onClick = { onValueChange(type.copy(settingsState.shapesType.strength)) },
                    title = stringResource(type.title()),
                    subtitle = stringResource(type.subtitle()),
                    containerColor = takeColorFromScheme {
                        if (selected) secondaryContainer
                        else SafeLocalContainerColor
                    },
                    shape = ShapeDefaults.byIndex(index, entries.size),
                    startIcon = {
                        Spacer(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(2.dp)
                                .border(
                                    width = 2.dp,
                                    color = LocalContentColor.current,
                                    shape = AutoCornersShape(
                                        size = when (type) {
                                            is ShapeType.Smooth -> 8.dp
                                            is ShapeType.Squircle -> 24.dp
                                            else -> 6.dp
                                        },
                                        shapesType = type
                                    )
                                )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = settingsState.borderWidth,
                            color = animateColorAsState(
                                if (selected) MaterialTheme.colorScheme
                                    .onSecondaryContainer
                                    .copy(alpha = 0.5f)
                                else Color.Transparent
                            ).value,
                            shape = ShapeDefaults.byIndex(index, entries.size)
                        ),
                    endIcon = {
                        Icon(
                            imageVector = if (selected) {
                                Icons.Rounded.RadioButtonChecked
                            } else Icons.Rounded.RadioButtonUnchecked,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

private fun ShapeType.title() = when (this) {
    is ShapeType.Cut -> R.string.cut
    is ShapeType.Rounded -> R.string.rounded
    is ShapeType.Smooth -> R.string.smooth
    is ShapeType.Squircle -> R.string.squircle
}

private fun ShapeType.subtitle() = when (this) {
    is ShapeType.Cut -> R.string.cut_shapes_sub
    is ShapeType.Rounded -> R.string.rounded_shapes_sub
    is ShapeType.Smooth -> R.string.smooth_shapes_sub
    is ShapeType.Squircle -> R.string.squircle_shapes_sub
}
