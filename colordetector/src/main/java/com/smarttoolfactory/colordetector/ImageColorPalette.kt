package com.smarttoolfactory.colordetector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.smarttoolfactory.extendedcolors.parser.ColorNameParser
import com.smarttoolfactory.extendedcolors.parser.rememberColorParser
import com.smarttoolfactory.extendedcolors.util.fractionToIntPercent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map

/**
 * Composable that creates color lists [Palette.Swatch] using **Palette API**
 * @param imageBitmap is the image that colors are generated from
 * @param selectedIndex whether only primary swatches or every swatch that is generated
 * should be displayed
 * @param colorNameParser parses color name from [Color]
 * @param maximumColorCount maximum number of [Palette.Swatch]es that should be generated
 * from [imageBitmap]. Maximum number might not be achieved based on image color composition
 * @param onColorChange callback to notify that user moved and picked a color
 */
@Composable
fun ImageColorPalette(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    selectedIndex: Int,
    colorNameParser: ColorNameParser = rememberColorParser(),
    maximumColorCount: Int = 16,
    onColorChange: (ColorData) -> Unit
) {
    val paletteData = remember {
        mutableStateListOf<PaletteData>()
    }

    var colorProfileMap by remember {
        mutableStateOf<Map<String, PaletteData>>(LinkedHashMap())
    }

    LaunchedEffect(key1 = imageBitmap) {
        snapshotFlow {
            imageBitmap
        }.map {
            val palette = Palette
                .from(imageBitmap.asAndroidBitmap())
                .maximumColorCount(maximumColorCount)
                .generate()

            paletteData.clear()

            val numberOfPixels: Float = palette.swatches.sumOf {
                it.population
            }.toFloat()

            palette.swatches.forEach { paletteSwatch: Palette.Swatch? ->
                paletteSwatch?.let { swatch: Palette.Swatch ->
                    val color = Color(swatch.rgb)
                    val name = colorNameParser.parseColorName(color)
                    val colorData = ColorData(color, name)
                    val percent: Float = swatch.population / numberOfPixels
                    paletteData.add(PaletteData(colorData = colorData, percent))
                }
            }

            val colorMap = linkedMapOf<String, PaletteData>()

            palette.lightVibrantSwatch?.rgb.let {
                if (it != null) {
                    val color = Color(it)
                    val name = colorNameParser.parseColorName(color)
                    val colorData = ColorData(color, name)

                    val percent: Float =
                        (palette.lightVibrantSwatch?.population ?: 0) / numberOfPixels
                    colorMap["Light Vibrant"] = PaletteData(colorData = colorData, percent)
                }
            }

            palette.vibrantSwatch?.rgb.let {
                if (it != null) {
                    val color = Color(it)
                    val name = colorNameParser.parseColorName(color)
                    val colorData = ColorData(color, name)

                    val percent: Float =
                        (palette.vibrantSwatch?.population ?: 0) / numberOfPixels
                    colorMap["Vibrant"] = PaletteData(colorData = colorData, percent)
                }
            }

            palette.darkVibrantSwatch?.rgb.let {
                if (it != null) {
                    val color = Color(it)
                    val name = colorNameParser.parseColorName(color)
                    val colorData = ColorData(color, name)

                    val percent: Float =
                        (palette.darkVibrantSwatch?.population ?: 0) / numberOfPixels
                    colorMap["Light Vibrant"] = PaletteData(colorData = colorData, percent)
                }
            }

            palette.lightMutedSwatch?.rgb.let {
                if (it != null) {
                    val color = Color(it)
                    val name = colorNameParser.parseColorName(color)
                    val colorData = ColorData(color, name)

                    val percent: Float =
                        (palette.lightMutedSwatch?.population ?: 0) / numberOfPixels
                    colorMap["Light Muted"] = PaletteData(colorData = colorData, percent)
                }
            }

            palette.mutedSwatch?.rgb.let {
                if (it != null) {
                    val color = Color(it)
                    val name = colorNameParser.parseColorName(color)
                    val colorData = ColorData(color, name)

                    val percent: Float =
                        (palette.mutedSwatch?.population ?: 0) / numberOfPixels
                    colorMap["Muted"] = PaletteData(colorData = colorData, percent)
                }
            }

            palette.darkMutedSwatch?.rgb.let {
                if (it != null) {
                    val color = Color(it)
                    val name = colorNameParser.parseColorName(color)
                    val colorData = ColorData(color, name)

                    val percent: Float =
                        (palette.darkMutedSwatch?.population ?: 0) / numberOfPixels
                    colorMap["Dark Muted"] = PaletteData(colorData = colorData, percent)
                }
            }

            colorProfileMap = colorMap
        }
            .flowOn(Dispatchers.Default)
            .launchIn(this)
    }

    ColorProfileList(
        modifier = modifier,
        index = selectedIndex,
        colorProfileMap = colorProfileMap,
        paletteDataList = paletteData,
        onColorChange = onColorChange
    )
}

@Composable
private fun ColorProfileList(
    modifier: Modifier,
    index: Int = 0,
    colorProfileMap: Map<String, PaletteData>,
    paletteDataList: List<PaletteData>,
    onColorChange: (ColorData) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {


        if (index == 0) {
            colorProfileMap.forEach {

                val profile = it.key
                val paletteData = it.value
                val percent = paletteData.percent.fractionToIntPercent()
                val colorData = paletteData.colorData

                item {
                    Column {
                        ColorItemRow(
                            modifier = Modifier
                                .shadow(.5.dp, RoundedCornerShape(50))
                                .background(MaterialTheme.colors.background)
                                .fillMaxWidth(),
                            profile = "($profile) ",
                            populationPercent = "$percent%",
                            colorData = colorData,
                            onClick = onColorChange
                        )
                    }
                }
            }
        } else {
            items(paletteDataList) { paletteData: PaletteData ->

                val percent = paletteData.percent.fractionToIntPercent()
                val colorData = paletteData.colorData

                ColorItemRow(
                    modifier = Modifier
                        .shadow(.5.dp, RoundedCornerShape(50))
                        .background(MaterialTheme.colors.background)
                        .fillMaxWidth(),
                    colorData = colorData,
                    populationPercent = "$percent%",
                    onClick = onColorChange
                )
            }
        }
    }
}

private data class PaletteData(val colorData: ColorData, val percent: Float)