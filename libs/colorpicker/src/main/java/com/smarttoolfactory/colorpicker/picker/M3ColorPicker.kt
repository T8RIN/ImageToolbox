package com.smarttoolfactory.colorpicker.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colordetector.ColorData
import com.smarttoolfactory.colorpicker.widget.ColorDisplayWithClipboard
import com.smarttoolfactory.extendedcolors.ColorSwatch
import com.smarttoolfactory.extendedcolors.parser.rememberColorParser
import com.smarttoolfactory.extendedcolors.util.getColorTonesList
import com.smarttoolfactory.extendedcolors.util.material3ToneRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest


@Composable
fun M3ColorPicker(onColorChange: (Color) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = .8f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val colorNameParser = rememberColorParser()

        // Show primary or accent colors
        var primaryAccentSelection by remember {
            mutableStateOf(0)
        }

        // Index of selected list as main and index of color in that list
        val colorSelectionIndex =
            remember { ColorSelectionIndex(mainSelection = 0, subSelection = 0) }

        // This is the selected color swatch in top grid which is swatch/list of colors
        var colorSwatchIndex by remember { mutableStateOf(0) }

        val colorSwatch: LinkedHashMap<Int, Color> =
            remember(
                colorSwatchIndex,
                primaryAccentSelection
            ) {
                if (primaryAccentSelection == 0)
                    ColorSwatch.primaryColorSwatches[colorSwatchIndex] else
                    ColorSwatch.accentColorSwatches[colorSwatchIndex]
            }

        // Get keys and colors for second grid to display Material Design 2 keys
        // and shades like 100, 200, 900
        val m2ColorIndices: List<Int> = colorSwatch.keys.toList()
        val m2ColorList: List<Color> = colorSwatch.values.toList()

        // Current color picked by user or initial color as Red500
        var currentColor by remember { mutableStateOf(ColorSwatch.primaryHeaderColors.first()) }
        // Always get Material Design3 tonal palette from color picked recently from
        // first or second grid
        var m3Tones by remember { mutableStateOf(getColorTonesList(currentColor)) }
        // Name of the color that is currently picked
        var colorName by remember { mutableStateOf("") }

        LaunchedEffect(key1 = colorNameParser) {

            snapshotFlow { currentColor }
                .distinctUntilChanged()
                .mapLatest { color: Color ->
                    colorNameParser.parseColorName(color)
                }
                .flowOn(Dispatchers.Default)
                .collect { name: String ->
                    colorName = name
                }
        }

        Spacer(modifier = Modifier.height(20.dp))
        PrimaryAccentSelectionTab(
            modifier = Modifier.width(300.dp),
            selectedIndex = primaryAccentSelection
        ) {
            primaryAccentSelection = it
            colorSelectionIndex.mainSelection = 0
            colorSelectionIndex.subSelection = 0
            colorSwatchIndex = 0
            currentColor = ColorSwatch.primaryHeaderColors.first()
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Color Swatch Selection
        ColorSelectionGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            colorSelectionList = if (primaryAccentSelection == 0)
                ColorSwatch.primaryHeaderColors else ColorSwatch.accentHeaderColors,
            selected = { index ->
                (colorSelectionIndex.mainSelection == 0 &&
                        colorSelectionIndex.subSelection == index) || (
                        primaryAccentSelection == 0 && colorSelectionIndex.mainSelection == 1 &&
                                colorSelectionIndex.subSelection == 5 &&
                                index == colorSwatchIndex
                        )
            },
            tint = { Color.Unspecified },
        ) { index: Int, item: Color ->
            currentColor = item
            onColorChange(item)
            colorSelectionIndex.mainSelection = 0
            colorSelectionIndex.subSelection = index
            colorSwatchIndex = index
            m3Tones = getColorTonesList(item)
        }

        Text(
            text = "Material Design2 Shade",
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Primary/Accent Color Selection
        ColorSelectionGrid(
            modifier = Modifier.fillMaxWidth(if (primaryAccentSelection == 0) 1f else .5f),
            columns = GridCells.Fixed(if (primaryAccentSelection == 0) 6 else 4),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            colorSelectionList = m2ColorList,
            colorKeys = m2ColorIndices,
            selected = { index ->
                (primaryAccentSelection == 0 && colorSelectionIndex.mainSelection == 0 && index == 5)
                        || (colorSelectionIndex.mainSelection == 1
                        && colorSelectionIndex.subSelection == index)
            },
            tint = { index ->
                if (index < 5) Color.Black else Color.White
            },

            ) { index: Int, item: Color ->
            currentColor = item
            colorSelectionIndex.mainSelection = 1
            colorSelectionIndex.subSelection = index
            onColorChange(item)
            m3Tones = getColorTonesList(item)
        }

        Text(
            text = "Material Design3 Tonal Palette",
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )


        // M3 Tone Selection
        ColorSelectionGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            colorSelectionList = m3Tones,
            colorKeys = material3ToneRange,
            selected = { index ->
                (colorSelectionIndex.mainSelection == 2
                        && colorSelectionIndex.subSelection == index)
            },
            tint = { index ->
                if (index < 6) Color.White else Color.Black
            },
        ) { index: Int, item: Color ->
            colorSelectionIndex.mainSelection = 2
            colorSelectionIndex.subSelection = index
            currentColor = item
            onColorChange(item)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = colorName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))
        ColorDisplayWithClipboard(colorData = ColorData(currentColor, colorName))
    }
}

@Composable
fun ColorSelectionGrid(
    modifier: Modifier = Modifier,
    columns: GridCells = GridCells.Fixed(8),
    contentPadding: PaddingValues = PaddingValues(8.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(4.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(4.dp),
    colorSelectionList: List<Color>,
    colorKeys: List<Int>? = null,
    selected: (Int) -> Boolean,
    tint: (Int) -> Color,
    onClick: (Int, Color) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = columns,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
    ) {
        itemsIndexed(colorSelectionList) { index: Int, item: Color ->

            ColorDisplayWithTitle(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .aspectRatio(1f)
                    .clickable {
                        onClick(index, item)
                    },
                backgroundColor = item,
                selected = selected(index),
                tint = tint(index),
                title = colorKeys?.get(index)?.toString() ?: ""
            )
        }
    }
}

@Composable
fun PrimaryAccentSelectionTab(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    onTabChange: (Int) -> Unit
) {

    val list = listOf("Primary", "Accent")

    TabRow(selectedTabIndex = selectedIndex,
        backgroundColor = Color.DarkGray,
        modifier = modifier.clip(RoundedCornerShape(10.dp)),
        indicator = {}
    ) {
        list.forEachIndexed { index, text ->
            val selected = selectedIndex == index
            Tab(
                modifier = if (selected) Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xff6FAAEE))
                else Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.DarkGray),
                selected = selected,
                onClick = {
                    onTabChange(index)
                },
                text = {
                    Text(
                        text = text, color = if (selected) Color.White
                        else Color.White.copy(.5f)
                    )
                }
            )
        }
    }
}


@Composable
fun ColorDisplayWithTitle(
    modifier: Modifier,
    title: String = "",
    selected: Boolean,
    tint: Color = Color.Unspecified,
    backgroundColor: Color
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = modifier
                .background(backgroundColor)
        )

        if (title.isNotEmpty()) {
            Text(text = title, color = tint, fontSize = 16.sp)
        }

        if (selected) {
            Icon(
                modifier = modifier
                    .background(tint.copy(alpha = .5f))
                    .padding(4.dp),
                imageVector = Icons.Default.Check,
                contentDescription = "check",
                tint = Color.Green
            )
        }
    }
}

data class ColorSelectionIndex(var mainSelection: Int = 0, var subSelection: Int = 0)