package com.smarttoolfactory.colorpicker.selector.gradient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colorpicker.model.GradientColorState
import com.smarttoolfactory.colorpicker.ui.Blue400
import com.smarttoolfactory.colorpicker.ui.GradientOffset
import com.smarttoolfactory.colorpicker.ui.Green400
import com.smarttoolfactory.colorpicker.ui.Orange400
import com.smarttoolfactory.colorpicker.ui.Red400
import com.smarttoolfactory.colorpicker.widget.ExpandableColumnWithTitle
import com.smarttoolfactory.colorpicker.widget.ExposedSelectionMenu
import com.smarttoolfactory.colorpicker.widget.drawChecker
import com.smarttoolfactory.extendedcolors.util.fractionToIntPercent
import com.smarttoolfactory.slider.ColorfulSlider
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor
import com.smarttoolfactory.slider.ui.InactiveTrackColor

enum class GradientType {
    Linear, Radial, Sweep
}

internal val gradientOptions = listOf(
    "Linear",
    "Radial",
    "Sweep"
)

internal val gradientTileModeOptions = listOf("Clamp", "Repeated", "Mirror", "Decal")

@Composable
fun GradientSelector(
    modifier: Modifier = Modifier,
    color: Color,
    gradientColorState: GradientColorState,
    onBrushChange: (Brush) -> Unit
) {

    Column(
        modifier = modifier
    ) {

        onBrushChange(gradientColorState.brush)

        GradientProperties(gradientColorState = gradientColorState)

        // Gradient type selection
        when (gradientColorState.gradientType) {
            GradientType.Linear ->
                LinearGradientSelection(
                    gradientColorState,
                    gradientColorState.size
                ) { offset: GradientOffset ->
                    gradientColorState.gradientOffset = offset
                }

            GradientType.Radial ->
                RadialGradientSelection(
                    gradientColorState
                ) { centerFriction: Offset, radiusFriction: Float ->
                    gradientColorState.centerFriction = centerFriction
                    gradientColorState.radiusFriction = radiusFriction
                }

            GradientType.Sweep ->
                SweepGradientSelection(
                    gradientColorState
                ) { centerFriction: Offset ->
                    gradientColorState.centerFriction = centerFriction
                }
        }

        // Color Stops and Colors
        ColorStopSelection(
            color = color,
            colorStops = gradientColorState.colorStops,
            onRemoveClick = { index: Int ->
                if (gradientColorState.colorStops.size > 2) {
                    gradientColorState.colorStops.removeAt(index)
                }
            },
            onValueChange = { index: Int, pair: Pair<Float, Color> ->
                gradientColorState.colorStops[index] = pair.copy()
            },
            onAddColorStop = { pair: Pair<Float, Color> ->
                gradientColorState.colorStops.add(pair)
            }
        )
    }
}

@Composable
private fun GradientProperties(gradientColorState: GradientColorState) {

    var tileModeSelection by remember {
        mutableStateOf(
            when (gradientColorState.tileMode) {
                TileMode.Clamp -> 0
                TileMode.Repeated -> 1
                TileMode.Mirror -> 2
                else -> 3
            }
        )
    }

    ExpandableColumnWithTitle(
        title = "Gradient Properties",
        color = Blue400,
        initialExpandState = false
    ) {
        Column {
            ExposedSelectionMenu(
                index = gradientColorState.gradientType.ordinal,
                title = "Gradient Type",
                options = if (gradientColorState.size == Size.Zero) gradientOptions.subList(
                    0,
                    1
                ) else gradientOptions,
                onSelected = {
                    gradientColorState.gradientType = GradientType.values()[it]
                },
            )

            Spacer(modifier = Modifier.height(5.dp))

            if (gradientColorState.gradientType != GradientType.Sweep) {
                ExposedSelectionMenu(
                    index = tileModeSelection,
                    title = "Tile Mode",
                    options = gradientTileModeOptions,
                    onSelected = {
                        tileModeSelection = it
                        gradientColorState.tileMode = when (tileModeSelection) {
                            0 -> TileMode.Clamp
                            1 -> TileMode.Repeated
                            2 -> TileMode.Mirror
                            else -> TileMode.Decal
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BrushDisplay(
    modifier: Modifier = Modifier,
    gradientColorState: GradientColorState
) {
    // Display Brush
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {

        val size = gradientColorState.size
        val contentWidth = size.width.coerceAtLeast(2f)
        val contentHeight = size.height.coerceAtLeast(1f)
        val contentAspectRatio = contentWidth / contentHeight

        var boxHeight: Float = constraints.maxHeight.toFloat()
        var boxWidth: Float = boxHeight * contentAspectRatio

        if (boxWidth > constraints.maxWidth) {
            boxWidth = constraints.maxWidth.toFloat()
            boxHeight = (boxWidth / contentAspectRatio)

        }

        val gradientType = gradientColorState.gradientType
        val colorStops = gradientColorState.colorStops
        val gradientOffset = gradientColorState.gradientOffset
        val tileMode = gradientColorState.tileMode
        val centerFriction = gradientColorState.centerFriction
        val radiusFriction = gradientColorState.radiusFriction

        val center = Offset(
            boxWidth * centerFriction.x,
            boxHeight * centerFriction.y
        )

        val boxWidthInDp: Dp
        val boxHeightInDp: Dp
        with(LocalDensity.current) {
            boxWidthInDp = boxWidth.toDp()
            boxHeightInDp = boxHeight.toDp()
        }

        val radius = (boxHeight * radiusFriction).coerceAtLeast(0.01f)

        val brush = when (gradientType) {
            GradientType.Linear -> {
                if (colorStops.size == 1) {
                    val brushColor = colorStops.first().second
                    Brush.linearGradient(listOf(brushColor, brushColor))
                } else {
                    Brush.linearGradient(
                        colorStops = colorStops.toTypedArray(),
                        start = gradientOffset.start,
                        end = gradientOffset.end,
                        tileMode = tileMode
                    )
                }
            }

            GradientType.Radial -> {
                Brush.radialGradient(
                    colorStops = colorStops.toTypedArray(),
                    center = center,
                    radius = radius,
                    tileMode = tileMode
                )
            }

            GradientType.Sweep -> {
                Brush.sweepGradient(
                    colorStops = colorStops.toTypedArray(),
                    center = center
                )
            }
        }

        Box(
            modifier = Modifier
                .height(boxHeightInDp)
                .width(boxWidthInDp)
                .drawChecker(RoundedCornerShape(5.dp))
                .background(brush, RoundedCornerShape(5.dp))
        )
    }
}

@Composable
internal fun ColorStopSelection(
    color: Color,
    colorStops: List<Pair<Float, Color>>,
    onRemoveClick: (Int) -> Unit,
    onAddColorStop: (Pair<Float, Color>) -> Unit,
    onValueChange: (Int, Pair<Float, Color>) -> Unit
) {

    ExpandableColumnWithTitle(
        title = "Colors and Stops",
        color = Orange400
    ) {
        Column {
            colorStops.forEachIndexed { index: Int, pair: Pair<Float, Color> ->
                GradientColorStopSelection(
                    index = index,
                    color = pair.second,
                    value = pair.first,
                    onRemoveClick = {
                        onRemoveClick(it)

                    },
                    onValueChange = { newPair: Pair<Float, Color> ->
                        onValueChange(index, newPair)
                    }
                )
            }

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onAddColorStop(Pair(1f, color))
                }
            ) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = "Add new Color",
                    color = Green400,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun GradientColorStopSelection(
    index: Int,
    color: Color,
    value: Float,
    onRemoveClick: (Int) -> Unit,
    onValueChange: (Pair<Float, Color>) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .drawChecker(RoundedCornerShape(percent = 25))
                    .background(color)
                    .size(40.dp)
            )

            Text(
                text = "${value.fractionToIntPercent()}%",
                fontSize = 10.sp,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.width(10.dp))

        ColorfulSlider(
            modifier = Modifier.weight(1f),
            value = value,
            onValueChange = { value ->
                onValueChange(
                    Pair(value, color)
                )
            },
            thumbRadius = 10.dp,
            trackHeight = 8.dp,
            colors = MaterialSliderDefaults.materialColors(
                inactiveTrackColor = SliderBrushColor(InactiveTrackColor)
            )
        )

        Spacer(modifier = Modifier.width(4.dp))
        FloatingActionButton(
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp)
                .size(16.dp),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp
            ),
            backgroundColor = Red400,
            contentColor = Color.White,
            onClick = { onRemoveClick(index) }) {
            Icon(
                modifier = Modifier.size(12.dp),
                imageVector = Icons.Filled.Close,
                contentDescription = "close",
            )
        }
    }
}
