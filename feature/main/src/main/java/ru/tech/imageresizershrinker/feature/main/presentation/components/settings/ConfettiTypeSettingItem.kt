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

package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ViewKanban
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeContainer
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.feature.main.presentation.components.Particles

@Composable
fun ConfettiTypeSettingItem(
    onValueChange: (Int) -> Unit,
    shape: Shape = ContainerShapeDefaults.bottomShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.2f)
    val settingsState = LocalSettingsState.current
    val items = remember {
        Particles.Type.entries
    }

    val enabled = settingsState.isConfettiEnabled

    val confettiHostState = LocalConfettiHostState.current

    Box {
        Column(
            modifier = modifier
                .container(
                    shape = shape,
                    color = backgroundColor
                )
                .alpha(
                    animateFloatAsState(
                        if (enabled) 1f
                        else 0.5f
                    ).value
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconShapeContainer(
                    enabled = true,
                    underlyingColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.2f),
                    content = {
                        Icon(
                            imageVector = Icons.Outlined.ViewKanban,
                            contentDescription = null
                        )
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.confetti_type),
                    style = LocalTextStyle.current.copy(lineHeight = 18.sp),
                    fontWeight = FontWeight.Medium
                )
            }

            FlowRow(
                verticalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterVertically
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, bottom = 8.dp, end = 8.dp)
            ) {
                val scope = rememberCoroutineScope()
                val value = settingsState.confettiType
                items.forEach {
                    EnhancedChip(
                        onClick = {
                            confettiHostState.currentToastData?.dismiss()
                            onValueChange(it.ordinal)
                            scope.launch {
                                delay(200L)
                                confettiHostState.show()
                            }
                        },
                        selected = it.ordinal == value,
                        label = {
                            Text(text = it.title)
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        selectedColor = MaterialTheme.colorScheme.outlineVariant(
                            0.2f,
                            MaterialTheme.colorScheme.tertiary
                        ),
                        selectedContentColor = MaterialTheme.colorScheme.onTertiary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (!enabled) {
            Surface(
                color = Color.Transparent,
                modifier = Modifier.matchParentSize()
            ) {}
        }
    }
}

private val Particles.Type.title: String
    @Composable
    get() = when (this) {
        Particles.Type.Default -> stringResource(R.string.defaultt)
        Particles.Type.Festive -> stringResource(R.string.festive)
        Particles.Type.Explode -> stringResource(R.string.explode)
        Particles.Type.Rain -> stringResource(R.string.rain)
        Particles.Type.Side -> stringResource(R.string.side)
    }