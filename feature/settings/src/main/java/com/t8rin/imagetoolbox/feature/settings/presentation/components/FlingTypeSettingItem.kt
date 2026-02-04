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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Animation
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
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
import com.t8rin.imagetoolbox.core.resources.icons.Animation
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.settings.domain.model.FlingType
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
fun FlingTypeSettingItem(
    onValueChange: (FlingType) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current

    var showSheet by rememberSaveable {
        mutableStateOf(false)
    }

    PreferenceItem(
        modifier = modifier,
        title = stringResource(id = R.string.fling_type),
        startIcon = Icons.Outlined.Animation,
        subtitle = settingsState.flingType.title,
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
                text = stringResource(id = R.string.fling_type),
                icon = Icons.Rounded.Animation
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
                .enhancedVerticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            val entries = FlingType.entries

            entries.forEachIndexed { index, type ->
                val selected = type == settingsState.flingType
                PreferenceItem(
                    onClick = { onValueChange(type) },
                    title = type.title,
                    subtitle = type.subtitle,
                    containerColor = takeColorFromScheme {
                        if (selected) secondaryContainer
                        else SafeLocalContainerColor
                    },
                    shape = ShapeDefaults.byIndex(index, entries.size),
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
                    endIcon = if (selected) {
                        Icons.Rounded.RadioButtonChecked
                    } else Icons.Rounded.RadioButtonUnchecked
                )
            }
        }
    }
}

private val FlingType.title: String
    @Composable
    get() = when (this) {
        FlingType.DEFAULT -> stringResource(R.string.android_native)
        FlingType.SMOOTH -> stringResource(R.string.smooth)
        FlingType.IOS_STYLE -> stringResource(R.string.ios_style)
        FlingType.SMOOTH_CURVE -> stringResource(R.string.smooth_curve)
        FlingType.QUICK_STOP -> stringResource(R.string.quick_stop)
        FlingType.BOUNCY -> stringResource(R.string.bouncy)
        FlingType.FLOATY -> stringResource(R.string.floaty)
        FlingType.SNAPPY -> stringResource(R.string.snappy)
        FlingType.ULTRA_SMOOTH -> stringResource(R.string.ultra_smooth)
        FlingType.ADAPTIVE -> stringResource(R.string.adaptive)
        FlingType.ACCESSIBILITY_AWARE -> stringResource(R.string.accessibility_aware)
        FlingType.REDUCED_MOTION -> stringResource(R.string.reduced_motion)
    }

private val FlingType.subtitle: String
    @Composable
    get() = when (this) {
        FlingType.DEFAULT -> stringResource(R.string.android_native_sub)
        FlingType.SMOOTH -> stringResource(R.string.smooth_sub)
        FlingType.IOS_STYLE -> stringResource(R.string.ios_style_sub)
        FlingType.SMOOTH_CURVE -> stringResource(R.string.smooth_curve_sub)
        FlingType.QUICK_STOP -> stringResource(R.string.quick_stop_sub)
        FlingType.BOUNCY -> stringResource(R.string.bouncy_sub)
        FlingType.FLOATY -> stringResource(R.string.floaty_sub)
        FlingType.SNAPPY -> stringResource(R.string.snappy_sub)
        FlingType.ULTRA_SMOOTH -> stringResource(R.string.ultra_smooth_sub)
        FlingType.ADAPTIVE -> stringResource(R.string.adaptive_sub)
        FlingType.ACCESSIBILITY_AWARE -> stringResource(R.string.accessibility_aware_sub)
        FlingType.REDUCED_MOTION -> stringResource(R.string.reduced_motion_sub)
    }