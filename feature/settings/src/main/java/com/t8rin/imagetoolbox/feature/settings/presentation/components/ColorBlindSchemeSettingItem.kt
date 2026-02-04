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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.RemoveRedEye
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.ColorBlindType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
fun ColorBlindSchemeSettingItem(
    onValueChange: (Int?) -> Unit,
    shape: Shape = ShapeDefaults.top,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    var isShowSheet by rememberSaveable { mutableStateOf(false) }

    PreferenceItem(
        shape = shape,
        onClick = { isShowSheet = true },
        title = stringResource(R.string.color_blind_scheme),
        subtitle = settingsState.colorBlindType?.localizedTitle
            ?: stringResource(R.string.disabled),
        startIcon = Icons.Outlined.RemoveRedEye,
        endIcon = Icons.Rounded.MiniEdit,
        modifier = modifier
    )

    EnhancedModalBottomSheet(
        visible = isShowSheet,
        onDismiss = {
            isShowSheet = false
        },
        title = {
            TitleItem(
                text = stringResource(R.string.color_blind_scheme),
                icon = Icons.Rounded.RemoveRedEye
            )
        },
        confirmButton = {
            EnhancedButton(
                onClick = { isShowSheet = false }
            ) {
                Text(text = stringResource(R.string.close))
            }
        },
    ) {
        val entries = remember {
            listOf(null) + ColorBlindType.entries
        }
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(250.dp),
            contentPadding = PaddingValues(16.dp),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            flingBehavior = enhancedFlingBehavior()
        ) {
            items(entries) { type ->
                ColorBlindTypeSelectionItem(
                    type = type,
                    onClick = {
                        onValueChange(type?.ordinal)
                    }
                )
            }
        }
    }
}

@Composable
private fun ColorBlindTypeSelectionItem(
    type: ColorBlindType?,
    onClick: () -> Unit
) {
    val settingsState = LocalSettingsState.current
    val selected = settingsState.colorBlindType == type

    PreferenceItem(
        onClick = onClick,
        title = type?.localizedTitle ?: stringResource(R.string.not_use_color_blind_scheme),
        subtitle = type?.localizedDescription
            ?: stringResource(R.string.not_use_color_blind_scheme_sub),
        containerColor = takeColorFromScheme {
            if (selected) secondaryContainer
            else SafeLocalContainerColor
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
                shape = ShapeDefaults.default
            ),
        endIcon = if (selected) {
            Icons.Rounded.RadioButtonChecked
        } else Icons.Rounded.RadioButtonUnchecked
    )
}

private val ColorBlindType.localizedTitle: String
    @Composable
    get() = stringResource(
        when (this) {
            ColorBlindType.Protanomaly -> R.string.protonomaly
            ColorBlindType.Deuteranomaly -> R.string.deutaromaly
            ColorBlindType.Tritanomaly -> R.string.tritonomaly
            ColorBlindType.Protanopia -> R.string.protanopia
            ColorBlindType.Deuteranopia -> R.string.deutaronotopia
            ColorBlindType.Tritanopia -> R.string.tritanopia
            ColorBlindType.Achromatomaly -> R.string.achromatomaly
            ColorBlindType.Achromatopsia -> R.string.achromatopsia
        }
    )

private val ColorBlindType.localizedDescription: String
    @Composable
    get() = stringResource(
        when (this) {
            ColorBlindType.Protanomaly -> R.string.protanomaly_sub
            ColorBlindType.Deuteranomaly -> R.string.deuteranomaly_sub
            ColorBlindType.Tritanomaly -> R.string.tritanomaly_sub
            ColorBlindType.Protanopia -> R.string.protanopia_sub
            ColorBlindType.Deuteranopia -> R.string.deuteranopia_sub
            ColorBlindType.Tritanopia -> R.string.tritanopia_sub
            ColorBlindType.Achromatomaly -> R.string.achromatomaly_sub
            ColorBlindType.Achromatopsia -> R.string.achromatopsia_sub
        }
    )
