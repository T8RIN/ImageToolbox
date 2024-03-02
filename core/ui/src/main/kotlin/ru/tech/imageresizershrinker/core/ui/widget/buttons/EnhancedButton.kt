/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.widget.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.mixedContainer
import ru.tech.imageresizershrinker.core.ui.theme.onMixedContainer
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.modifier.materialShadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = contentColor(containerColor),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant(onTopOf = containerColor),
    shape: Shape = ButtonDefaults.outlinedShape,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    isShadowClip: Boolean = containerColor.alpha != 1f,
    shadowModifier: @Composable () -> Modifier = {
        val settingsState = LocalSettingsState.current
        Modifier.materialShadow(
            shape = shape,
            elevation = animateDpAsState(
                if (settingsState.borderWidth > 0.dp || !enabled) 0.dp else 0.5.dp
            ).value,
            enabled = LocalSettingsState.current.drawButtonShadows,
            isClipped = isShadowClip
        )
    },
    content: @Composable RowScope.() -> Unit
) {
    val settingsState = LocalSettingsState.current
    val haptics = LocalHapticFeedback.current

    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        Box {
            OutlinedButton(
                onClick = {
                    onClick()
                    haptics.performHapticFeedback(
                        HapticFeedbackType.LongPress
                    )
                },
                modifier = modifier
                    .then(shadowModifier()),
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    contentColor = animateColorAsState(
                        if (enabled) contentColor
                        else MaterialTheme.colorScheme.onSurface.copy(0.38f)
                    ).value,
                    containerColor = animateColorAsState(
                        if (enabled) containerColor
                        else MaterialTheme.colorScheme.onSurface.copy(0.12f)
                    ).value
                ),
                enabled = true,
                border = BorderStroke(
                    width = settingsState.borderWidth,
                    color = borderColor
                ),
                contentPadding = contentPadding,
                interactionSource = interactionSource,
                content = content
            )

            if (!enabled) {
                Surface(color = Color.Transparent, modifier = Modifier.matchParentSize()) {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = Color.Transparent,
    contentColor: Color = contentColor(containerColor),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant(onTopOf = containerColor),
    shape: Shape = IconButtonDefaults.outlinedShape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    forceMinimumInteractiveComponentSize: Boolean = true,
    enableAutoShadowAndBorder: Boolean = containerColor != Color.Transparent,
    isShadowClip: Boolean = containerColor.alpha != 1f || !enabled,
    content: @Composable () -> Unit
) {
    val settingsState = LocalSettingsState.current
    val haptics = LocalHapticFeedback.current

    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        OutlinedIconButton(
            onClick = {
                onClick()
                haptics.performHapticFeedback(
                    HapticFeedbackType.LongPress
                )
            },
            modifier = modifier
                .then(
                    if (forceMinimumInteractiveComponentSize) Modifier.minimumInteractiveComponentSize()
                    else Modifier
                )
                .materialShadow(
                    shape = shape,
                    isClipped = isShadowClip,
                    enabled = LocalSettingsState.current.drawButtonShadows,
                    elevation = if (settingsState.borderWidth > 0.dp || !enableAutoShadowAndBorder) 0.dp else 0.7.dp
                ),
            shape = shape,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = contentColor,
                containerColor = containerColor
            ),
            enabled = enabled,
            border = BorderStroke(
                width = animateDpAsState(
                    if (enableAutoShadowAndBorder) settingsState.borderWidth
                    else 0.dp
                ).value,
                color = animateColorAsState(
                    if (enableAutoShadowAndBorder) borderColor
                    else Color.Transparent
                ).value
            ),
            interactionSource = interactionSource,
            content = content
        )
    }
}

@Composable
private fun contentColor(
    backgroundColor: Color
) = MaterialTheme.colorScheme.contentColorFor(backgroundColor).takeOrElse {
    if (backgroundColor == MaterialTheme.colorScheme.mixedContainer) MaterialTheme.colorScheme.onMixedContainer
    else LocalContentColor.current
}