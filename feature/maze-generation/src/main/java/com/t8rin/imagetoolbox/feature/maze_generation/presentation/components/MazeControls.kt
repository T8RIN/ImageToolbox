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

package com.t8rin.imagetoolbox.feature.maze_generation.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BarChart
import com.t8rin.imagetoolbox.core.resources.icons.Build
import com.t8rin.imagetoolbox.core.resources.icons.GridOn
import com.t8rin.imagetoolbox.core.resources.icons.Route
import com.t8rin.imagetoolbox.core.ui.utils.provider.ProvideContainerDefaults
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.SeedSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.maze_generation.domain.MazeAlgorithm
import com.t8rin.imagetoolbox.feature.maze_generation.domain.MazeCap
import com.t8rin.imagetoolbox.feature.maze_generation.domain.MazeDirection
import com.t8rin.imagetoolbox.feature.maze_generation.domain.MazeEntrance
import com.t8rin.imagetoolbox.feature.maze_generation.domain.MazeMask
import com.t8rin.imagetoolbox.feature.maze_generation.domain.MazeRecipe
import com.t8rin.imagetoolbox.feature.maze_generation.domain.MazeTopology
import com.t8rin.imagetoolbox.feature.maze_generation.presentation.screenLogic.MazeGenerationComponent
import kotlin.math.roundToInt

@Composable
internal fun MazeControls(component: MazeGenerationComponent) {
    val p = component.recipe
    val change = component::updateRecipe
    Column(
        modifier =
            Modifier.fillMaxWidth().container(shape = ShapeDefaults.large, resultPadding = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TitleItem(
            text = stringResource(R.string.params),
            icon = Icons.Rounded.Build,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        ProvideContainerDefaults(color = MaterialTheme.colorScheme.surface) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Choice(
                    p.topology,
                    MazeTopology.entries,
                    R.string.maze_topology,
                    { it.title },
                    shape = ShapeDefaults.top,
                    onChange = component::setTopology,
                )
                val algorithms = MazeAlgorithm.entries.filter { it.supports(p.topology) }
                Column(
                    modifier =
                        Modifier.container(
                            shape = ShapeDefaults.center,
                            color = MaterialTheme.colorScheme.surface,
                        ),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    DataSelector(
                        value = p.algorithm,
                        entries = algorithms,
                        title = stringResource(R.string.maze_algorithm),
                        titleIcon = Icons.Outlined.Route,
                        badgeContent = { Text(algorithms.size.toString()) },
                        itemContentText = { stringResource(it.title) },
                        onValueChange = { change(p.copy(algorithm = it)) },
                        spanCount = 2,
                        behaveAsContainer = false,
                        shape = ShapeDefaults.center,
                    )
                    Text(
                        text = stringResource(p.algorithm.description),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                    )
                }
                NumberSlider(
                    if (p.topology == MazeTopology.Polar) R.string.maze_rings
                    else R.string.maze_rows,
                    p.rows,
                    2f..100f,
                ) {
                    change(resizeMask(p, it, p.columns))
                }
                if (p.topology != MazeTopology.Polar)
                    NumberSlider(R.string.maze_columns, p.columns, 2f..100f) {
                        change(resizeMask(p, p.rows, it))
                    }
                SeedSelector(
                    value = p.seed,
                    valueRange = -10000f..10000f,
                    onValueChange = { change(p.copy(seed = it.roundToInt())) },
                    allowRandomizeAfterSave = false,
                    shape = ShapeDefaults.center,
                )
                NumberSlider(R.string.maze_braid, p.braid, 0f..100f, suffix = "%") {
                    change(p.copy(braid = it))
                }
                if (p.topology == MazeTopology.Mask) {
                    Choice(
                        p.mask,
                        MazeMask.entries.filter {
                            it != MazeMask.Image || p.imageMask.isNotEmpty()
                        },
                        R.string.maze_mask,
                        { it.title },
                    ) {
                        change(p.copy(mask = it))
                    }
                    var threshold by rememberSaveable { mutableIntStateOf(128) }
                    var invert by rememberSaveable { mutableStateOf(false) }
                    var maskUri by remember { mutableStateOf<Uri?>(null) }
                    val picker =
                        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
                            uri ->
                            if (uri != null) {
                                maskUri = uri
                                component.importMask(uri, threshold, invert)
                            }
                        }
                    NumberSlider(R.string.maze_threshold, threshold, 0f..255f) { threshold = it }
                    Toggle(R.string.maze_invert_mask, invert) { invert = it }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Action(R.string.maze_load_mask) { picker.launch(arrayOf("image/*")) }
                        if (maskUri != null)
                            Action(R.string.maze_apply_mask) {
                                component.importMask(maskUri!!, threshold, invert)
                            }
                    }
                    Text(
                        stringResource(R.string.maze_mask_hint),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                when (p.algorithm) {
                    MazeAlgorithm.GrowingTree -> {
                        NumberSlider(R.string.maze_newest, p.newestWeight, 0f..100f) {
                            change(p.copy(newestWeight = it))
                        }
                        NumberSlider(R.string.maze_random, p.randomWeight, 0f..100f) {
                            change(p.copy(randomWeight = it))
                        }
                        NumberSlider(R.string.maze_oldest, p.oldestWeight, 0f..100f) {
                            change(p.copy(oldestWeight = it))
                        }
                        NumberSlider(R.string.maze_middle, p.middleWeight, 0f..100f) {
                            change(p.copy(middleWeight = it))
                        }
                    }
                    MazeAlgorithm.BinaryTree -> {
                        Choice(
                            p.direction,
                            MazeDirection.entries,
                            R.string.maze_direction,
                            { it.title },
                        ) {
                            change(p.copy(direction = it))
                        }
                        NumberSlider(R.string.maze_horizontal_bias, p.horizontalBias, 0f..100f) {
                            change(p.copy(horizontalBias = it))
                        }
                    }
                    MazeAlgorithm.Sidewinder ->
                        NumberSlider(R.string.maze_horizontal_bias, p.horizontalBias, 0f..100f) {
                            change(p.copy(horizontalBias = it))
                        }
                    MazeAlgorithm.Eller -> {
                        NumberSlider(R.string.maze_eller_join, p.ellerJoin, 0f..100f) {
                            change(p.copy(ellerJoin = it))
                        }
                        NumberSlider(R.string.maze_eller_drop, p.ellerDrop, 0f..100f) {
                            change(p.copy(ellerDrop = it))
                        }
                    }
                    MazeAlgorithm.Houston ->
                        NumberSlider(R.string.maze_houston_switch, p.houstonSwitch, 0f..100f) {
                            change(p.copy(houstonSwitch = it))
                        }
                    MazeAlgorithm.OriginShift ->
                        NumberSlider(
                            R.string.maze_origin_iterations,
                            p.originIterations,
                            1f..100f,
                        ) {
                            change(p.copy(originIterations = it))
                        }
                    MazeAlgorithm.RecursiveDivision -> {
                        NumberSlider(R.string.maze_horizontal_bias, p.horizontalBias, 0f..100f) {
                            change(p.copy(horizontalBias = it))
                        }
                        NumberSlider(R.string.maze_division_room, p.divisionRoom, 1f..20f) {
                            change(p.copy(divisionRoom = it))
                        }
                    }
                    else -> Unit
                }

                Choice(p.entrance, MazeEntrance.entries, R.string.maze_entrance, { it.title }) {
                    change(p.copy(entrance = it))
                }
                if (p.entrance == MazeEntrance.Manual) {
                    val max =
                        (component.statistics?.cells?.minus(1) ?: 1).coerceAtLeast(1).toFloat()
                    NumberSlider(
                        R.string.maze_start_cell,
                        p.startCell.coerceIn(0, max.toInt()),
                        0f..max,
                    ) {
                        change(p.copy(startCell = it))
                    }
                    NumberSlider(
                        R.string.maze_end_cell,
                        p.endCell.coerceIn(0, max.toInt()),
                        0f..max,
                    ) {
                        change(p.copy(endCell = it))
                    }
                }
                Toggle(R.string.maze_open_entrances, p.openEntrances) {
                    change(p.copy(openEntrances = it))
                }
                Toggle(R.string.maze_show_solution, p.style.showSolution) {
                    change(p.copy(style = p.style.copy(showSolution = it)))
                }
                Toggle(R.string.maze_show_markers, p.style.showMarkers) {
                    change(p.copy(style = p.style.copy(showMarkers = it)))
                }
                Toggle(R.string.maze_heatmap, p.style.heatmap) {
                    change(p.copy(style = p.style.copy(heatmap = it)))
                }

                FloatSlider(R.string.maze_wall_width, p.style.wallWidth, 0.01f..0.5f) {
                    change(p.copy(style = p.style.copy(wallWidth = it)))
                }
                if (p.style.showSolution) {
                    FloatSlider(R.string.maze_solution_width, p.style.solutionWidth, 0.02f..0.8f) {
                        change(p.copy(style = p.style.copy(solutionWidth = it)))
                    }
                }
                if (p.style.showMarkers) {
                    FloatSlider(R.string.maze_marker_size, p.style.markerSize, 0.05f..0.6f) {
                        change(p.copy(style = p.style.copy(markerSize = it)))
                    }
                }
                FloatSlider(R.string.maze_margin, p.style.margin, 0.1f..5f) {
                    change(p.copy(style = p.style.copy(margin = it)))
                }
                Choice(p.style.cap, MazeCap.entries, R.string.maze_cap, { it.title }) {
                    change(p.copy(style = p.style.copy(cap = it)))
                }
                MazeColor(R.string.maze_wall_color, p.style.wallColor) {
                    change(p.copy(style = p.style.copy(wallColor = it)))
                }
                MazeColor(
                    R.string.maze_background_color,
                    p.style.backgroundColor,
                    shape =
                        if (!p.style.showSolution && !p.style.showMarkers) ShapeDefaults.bottom
                        else ShapeDefaults.center,
                ) {
                    change(p.copy(style = p.style.copy(backgroundColor = it, transparent = false)))
                }
                if (p.style.showSolution) {
                    MazeColor(
                        R.string.maze_solution_color,
                        p.style.solutionColor,
                        shape =
                            if (!p.style.showMarkers) ShapeDefaults.bottom else ShapeDefaults.center,
                    ) {
                        change(p.copy(style = p.style.copy(solutionColor = it)))
                    }
                }
                if (p.style.showMarkers) {
                    MazeColor(R.string.maze_start_color, p.style.startColor) {
                        change(p.copy(style = p.style.copy(startColor = it)))
                    }
                }
                if (p.style.showMarkers) {
                    MazeColor(
                        R.string.maze_end_color,
                        p.style.endColor,
                        shape = ShapeDefaults.bottom,
                    ) {
                        change(p.copy(style = p.style.copy(endColor = it)))
                    }
                }
            }
        }
    }
    component.statistics?.let { s ->
        ExpandableItem(
            modifier = Modifier.fillMaxWidth(),
            initialState = false,
            visibleContent = {
                TitleItem(
                    text = stringResource(R.string.maze_statistics),
                    icon = Icons.Rounded.BarChart,
                    modifier = Modifier.padding(8.dp),
                )
            },
            expandableContent = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    androidx.compose.runtime.CompositionLocalProvider(
                        androidx.compose.material3.LocalTextStyle provides
                            MaterialTheme.typography.bodyLarge
                    ) {
                        Text(
                            stringResource(
                                R.string.maze_extra_statistics,
                                s.passages - s.cells + 1,
                                s.deadEnds * 100 / s.cells,
                            )
                        )
                        Text(
                            stringResource(
                                R.string.maze_statistics_values,
                                s.cells,
                                s.passages,
                                s.deadEnds,
                                s.junctions,
                                s.solutionLength,
                                component.elapsedMillis,
                            )
                        )
                    }
                }
            },
        )
    }
}

private fun resizeMask(p: MazeRecipe, rows: Int, columns: Int): MazeRecipe {
    val mask =
        if (p.imageMask.size == p.rows * p.columns)
            List(rows * columns) { i ->
                val r = (i / columns * p.rows / rows).coerceAtMost(p.rows - 1)
                val c = (i % columns * p.columns / columns).coerceAtMost(p.columns - 1)
                p.imageMask[r * p.columns + c]
            }
        else emptyList()
    return p.copy(rows = rows, columns = columns, imageMask = mask)
}

@Composable
private fun <T : Any> Choice(
    value: T,
    entries: List<T>,
    title: Int,
    label: (T) -> Int,
    shape: Shape = ShapeDefaults.center,
    onChange: (T) -> Unit,
) {
    DataSelector(
        value = value,
        entries = entries,
        title = stringResource(title),
        titleIcon = Icons.Outlined.GridOn,
        itemContentText = { stringResource(label(it)) },
        onValueChange = onChange,
        spanCount = 2,
        shape = shape,
    )
}

@Composable
private fun NumberSlider(
    title: Int,
    value: Int,
    range: ClosedFloatingPointRange<Float>,
    suffix: String = "",
    onChange: (Int) -> Unit,
) {
    EnhancedSliderItem(
        value = value,
        title = stringResource(title),
        valueRange = range,
        valueSuffix = suffix,
        internalStateTransformation = { it.roundToInt() },
        onValueChange = { onChange(it.roundToInt()) },
        shape = ShapeDefaults.center,
    )
}

@Composable
private fun FloatSlider(
    title: Int,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit,
) {
    EnhancedSliderItem(
        value = value,
        title = stringResource(title),
        valueRange = range,
        internalStateTransformation = { (it * 100).roundToInt() / 100f },
        onValueChange = { onChange((it * 100).roundToInt() / 100f) },
        shape = ShapeDefaults.center,
    )
}

@Composable
private fun Toggle(title: Int, value: Boolean, onChange: (Boolean) -> Unit) {
    PreferenceRowSwitch(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(title),
        checked = value,
        onClick = onChange,
        startIcon = null,
        shape = ShapeDefaults.center,
    )
}

@Composable
private fun Action(title: Int, onClick: () -> Unit) {
    EnhancedButton(onClick = onClick) { Text(stringResource(title)) }
}

@Composable
private fun MazeColor(
    title: Int,
    value: Int,
    shape: Shape = ShapeDefaults.center,
    onChange: (Int) -> Unit,
) {
    ColorRowSelector(
        modifier = Modifier.fillMaxWidth().container(shape = shape),
        value = Color(value),
        onValueChange = { onChange(it.toArgb()) },
        title = stringResource(title),
    )
}
