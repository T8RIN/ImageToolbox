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

@file:Suppress("unused", "MemberVisibilityCanBePrivate", "KotlinConstantConditions")

package com.t8rin.dynamic.theme

import android.Manifest
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Build
import androidx.annotation.FloatRange
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.palette.graphics.Palette
import com.materialkolor.dynamiccolor.MaterialDynamicColors
import com.materialkolor.hct.Hct
import com.materialkolor.palettes.TonalPalette
import com.materialkolor.scheme.DynamicScheme
import com.materialkolor.scheme.SchemeContent
import com.materialkolor.scheme.SchemeExpressive
import com.materialkolor.scheme.SchemeFidelity
import com.materialkolor.scheme.SchemeFruitSalad
import com.materialkolor.scheme.SchemeMonochrome
import com.materialkolor.scheme.SchemeNeutral
import com.materialkolor.scheme.SchemeRainbow
import com.materialkolor.scheme.SchemeVibrant
import com.materialkolor.scheme.Variant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * DynamicTheme allows you to dynamically change the color scheme of the content hierarchy.
 * To do this you just need to update [DynamicThemeState].
 * @param state - current instance of [DynamicThemeState]
 * */
@Composable
fun DynamicTheme(
    state: DynamicThemeState,
    typography: Typography = MaterialTheme.typography,
    density: Density = LocalDensity.current,
    defaultColorTuple: ColorTuple,
    dynamicColor: Boolean = true,
    amoledMode: Boolean = false,
    isDarkTheme: Boolean,
    style: PaletteStyle = PaletteStyle.TonalSpot,
    contrastLevel: Double = 0.0,
    isInvertColors: Boolean = false,
    colorBlindType: ColorBlindType? = null,
    colorAnimationSpec: AnimationSpec<Color> = tween(300),
    dynamicColorsOverride: ((isDarkTheme: Boolean) -> ColorTuple?)? = null,
    content: @Composable () -> Unit,
) {
    val colorTuple = rememberAppColorTuple(
        defaultColorTuple = defaultColorTuple,
        dynamicColor = dynamicColor,
        darkTheme = isDarkTheme
    )
    val configuration = LocalConfiguration.current
    var prevOrientation by rememberSaveable { mutableIntStateOf(configuration.orientation) }

    LaunchedEffect(colorTuple, prevOrientation) {
        if (prevOrientation == configuration.orientation) {
            state.updateColorTuple(colorTuple)
        } else prevOrientation = configuration.orientation
    }

    val lightTheme = !isDarkTheme
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = if (dynamicColor) {
        lightTheme
    } else if (isInvertColors) {
        isDarkTheme
    } else {
        lightTheme
    }

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons,
            isNavigationBarContrastEnforced = false
        )
    }

    CompositionLocalProvider(
        values = arrayOf(
            LocalDynamicThemeState provides state,
            LocalDensity provides density
        ),
        content = {
            MaterialTheme(
                typography = typography,
                colorScheme = rememberColorScheme(
                    amoledMode = amoledMode,
                    isDarkTheme = isDarkTheme,
                    colorTuple = state.colorTuple.value,
                    style = style,
                    contrastLevel = contrastLevel,
                    dynamicColor = dynamicColor,
                    isInvertColors = isInvertColors,
                    colorBlindType = colorBlindType,
                    dynamicColorsOverride = dynamicColorsOverride
                ).animateAllColors(colorAnimationSpec),
                content = content
            )
        }
    )
}

/**Composable representing ColorTuple object **/
@Composable
fun ColorTupleItem(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    colorTuple: ColorTuple,
    shape: Shape = CircleShape,
    containerShape: Shape = MaterialTheme.shapes.medium,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    content: (@Composable BoxScope.() -> Unit)? = null
) {
    val (primary, secondary, tertiary) = remember(colorTuple) {
        derivedStateOf {
            colorTuple.run {
                val hct = Hct.fromInt(colorTuple.primary.toArgb())
                val hue = hct.hue
                val chroma = hct.chroma

                val secondary = colorTuple.secondary?.toArgb().let {
                    if (it != null) {
                        TonalPalette.fromInt(it)
                    } else {
                        TonalPalette.fromHueAndChroma(hue, chroma / 3.0)
                    }
                }
                val tertiary = colorTuple.tertiary?.toArgb().let {
                    if (it != null) {
                        TonalPalette.fromInt(it)
                    } else {
                        TonalPalette.fromHueAndChroma(hue + 60.0, chroma / 2.0)
                    }
                }

                Triple(
                    primary,
                    colorTuple.secondary ?: Color(secondary.tone(70)),
                    colorTuple.tertiary ?: Color(tertiary.tone(70))
                )
            }
        }
    }.value.run {
        Triple(
            animateColorAsState(targetValue = first).value,
            animateColorAsState(targetValue = second).value,
            animateColorAsState(targetValue = third).value
        )
    }

    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = containerShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .clip(shape),
            contentAlignment = Alignment.Center
        ) {
            Column(Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(primary)
                )
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(tertiary)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(secondary)
                    )
                }
            }
            content?.invoke(this)
        }
    }
}

fun Color.calculateSecondaryColor(): Int {
    val hct = Hct.fromInt(this.toArgb())
    val hue = hct.hue
    val chroma = hct.chroma

    return TonalPalette.fromHueAndChroma(hue, chroma / 3.0).tone(80)
}

fun Color.calculateTertiaryColor(): Int {
    val hct = Hct.fromInt(this.toArgb())
    val hue = hct.hue
    val chroma = hct.chroma

    return TonalPalette.fromHueAndChroma(hue + 60.0, chroma / 2.0).tone(80)
}

fun Color.calculateSurfaceColor(): Int {
    val hct = Hct.fromInt(this.toArgb())
    val hue = hct.hue
    val chroma = hct.chroma

    return TonalPalette.fromHueAndChroma(hue, (chroma / 12.0).coerceAtMost(4.0)).tone(90)
}


@SuppressLint("MissingPermission")
@Composable
fun rememberAppColorTuple(
    defaultColorTuple: ColorTuple,
    dynamicColor: Boolean,
    darkTheme: Boolean
): ColorTuple {
    val context = LocalContext.current
    return remember(
        LocalLifecycleOwner.current.lifecycle.observeAsState().value,
        dynamicColor,
        darkTheme,
        defaultColorTuple
    ) {
        derivedStateOf {
            runCatching {
                val wallpaperManager = WallpaperManager.getInstance(context)
                val wallColors =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                        wallpaperManager
                            .getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
                    } else null

                when {
                    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                        if (darkTheme) {
                            dynamicDarkColorScheme(context)
                        } else {
                            dynamicLightColorScheme(context)
                        }.run {
                            ColorTuple(
                                primary = primary,
                                secondary = secondary,
                                tertiary = tertiary,
                                surface = surface
                            )
                        }
                    }

                    dynamicColor && wallColors != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
                        ColorTuple(
                            primary = Color(wallColors.primaryColor.toArgb()),
                            secondary = wallColors.secondaryColor?.toArgb()?.let { Color(it) },
                            tertiary = wallColors.tertiaryColor?.toArgb()?.let { Color(it) }
                        )
                    }

                    dynamicColor && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        val primary = wallpaperManager.drawable?.toBitmap()?.extractPrimaryColor()

                        primary?.let {
                            ColorTuple(
                                primary = primary
                            )
                        }
                    }

                    else -> defaultColorTuple
                }
            }.getOrNull() ?: defaultColorTuple
        }
    }.value
}

@Composable
fun Lifecycle.observeAsState(): State<Lifecycle.Event> {
    val state = remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            state.value = event
        }
        this@observeAsState.addObserver(observer)
        onDispose {
            this@observeAsState.removeObserver(observer)
        }
    }
    return state
}

/**
 * This function animates colors when current color scheme changes.
 *
 * @param animationSpec Animation that will be applied when theming option changes.
 * @return [ColorScheme] with animated colors.
 */
@Composable
fun ColorScheme.animateAllColors(animationSpec: AnimationSpec<Color>): ColorScheme {

    /**
     * Wraps color into [animateColorAsState].
     *
     * @return Animated [Color].
     */
    @Composable
    fun Color.animateColor() = animateColorAsState(this, animationSpec).value

    return this.copy(
        primary = primary.animateColor(),
        onPrimary = onPrimary.animateColor(),
        primaryContainer = primaryContainer.animateColor(),
        onPrimaryContainer = onPrimaryContainer.animateColor(),
        inversePrimary = inversePrimary.animateColor(),
        secondary = secondary.animateColor(),
        onSecondary = onSecondary.animateColor(),
        secondaryContainer = secondaryContainer.animateColor(),
        onSecondaryContainer = onSecondaryContainer.animateColor(),
        tertiary = tertiary.animateColor(),
        onTertiary = onTertiary.animateColor(),
        tertiaryContainer = tertiaryContainer.animateColor(),
        onTertiaryContainer = onTertiaryContainer.animateColor(),
        background = background.animateColor(),
        onBackground = onBackground.animateColor(),
        surface = surface.animateColor(),
        onSurface = onSurface.animateColor(),
        surfaceVariant = surfaceVariant.animateColor(),
        onSurfaceVariant = onSurfaceVariant.animateColor(),
        surfaceTint = surfaceTint.animateColor(),
        inverseSurface = inverseSurface.animateColor(),
        inverseOnSurface = inverseOnSurface.animateColor(),
        error = error.animateColor(),
        onError = onError.animateColor(),
        errorContainer = errorContainer.animateColor(),
        onErrorContainer = onErrorContainer.animateColor(),
        outline = outline.animateColor(),
        outlineVariant = outlineVariant.animateColor(),
        scrim = scrim.animateColor(),
        surfaceBright = surfaceBright.animateColor(),
        surfaceDim = surfaceDim.animateColor(),
        surfaceContainer = surfaceContainer.animateColor(),
        surfaceContainerHigh = surfaceContainerHigh.animateColor(),
        surfaceContainerHighest = surfaceContainerHighest.animateColor(),
        surfaceContainerLow = surfaceContainerLow.animateColor(),
        surfaceContainerLowest = surfaceContainerLowest.animateColor()
    )
}

private fun Int.blend(
    color: Int,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float = 0.5f
): Int = ColorUtils.blendARGB(this, color, fraction)

fun Bitmap.extractPrimaryColor(default: Int = 0, blendWithVibrant: Boolean = true): Color {
    val palette = Palette
        .from(this)
        .generate()

    return Color(
        palette.getDominantColor(default).run {
            if (blendWithVibrant) blend(palette.getVibrantColor(default), 0.5f)
            else this
        }
    )
}

/** Class that represents App color scheme based on three main colors
 *  @param primary primary color
 *  @param secondary secondary color
 *  @param tertiary tertiary color
 */
data class ColorTuple(
    val primary: Color,
    val secondary: Color? = null,
    val tertiary: Color? = null,
    val surface: Color? = null
) {
    override fun toString(): String =
        "ColorTuple(primary=${primary.toArgb()}, secondary=${secondary?.toArgb()}, tertiary=${tertiary?.toArgb()}, surface=${surface?.toArgb()})"
}

/**
 * Creates and remember [DynamicThemeState] instance
 * */
@Composable
fun rememberDynamicThemeState(
    initialColorTuple: ColorTuple = ColorTuple(
        primary = MaterialTheme.colorScheme.primary,
        secondary = MaterialTheme.colorScheme.secondary,
        tertiary = MaterialTheme.colorScheme.tertiary,
        surface = MaterialTheme.colorScheme.surface
    )
): DynamicThemeState = rememberSaveable(saver = DynamicThemeStateSaver) {
    DynamicThemeState(initialColorTuple)
}

val DynamicThemeStateSaver = listSaver(
    save = {
        val colorTuple = it.colorTuple.value
        val list: List<Int> = listOf(
            colorTuple.primary.toArgb(),
            colorTuple.secondary?.toArgb() ?: -1,
            colorTuple.tertiary?.toArgb() ?: -1,
            colorTuple.surface?.toArgb() ?: -1
        )

        list
    },
    restore = { ints: List<Int> ->
        DynamicThemeState(
            initialColorTuple = ColorTuple(
                primary = Color(ints[0]),
                secondary = ints[1].takeIf { it != -1 }?.let { Color(it) },
                tertiary = ints[2].takeIf { it != -1 }?.let { Color(it) },
                surface = ints[3].takeIf { it != -1 }?.let { Color(it) },
            )
        )
    }
)

@Stable
class DynamicThemeState(
    initialColorTuple: ColorTuple
) {
    private val _colorTuple: MutableState<ColorTuple> = mutableStateOf(initialColorTuple)
    val colorTuple: State<ColorTuple> = _colorTuple

    fun updateColor(color: Color) {
        _colorTuple.value = ColorTuple(primary = color, secondary = null, tertiary = null)
    }

    fun updateColorTuple(newColorTuple: ColorTuple) {
        _colorTuple.value = newColorTuple
    }

    fun updateColorByImage(bitmap: Bitmap) {
        CoroutineScope(Dispatchers.Main).launch {
            updateColor(bitmap.saturate(2f).extractPrimaryColor())
        }
    }

    private suspend fun Bitmap.saturate(saturation: Float): Bitmap = withContext(Dispatchers.IO) {
        val src = this@saturate
        val w = src.width.coerceAtMost(600)
        val h = src.height.coerceAtMost(600)
        val bitmapResult = createBitmap(w, h)
        val canvasResult = Canvas(bitmapResult)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(saturation)
        val filter = ColorMatrixColorFilter(colorMatrix)
        paint.setColorFilter(filter)
        canvasResult.drawBitmap(src, 0f, 0f, paint)
        return@withContext bitmapResult
    }
}

@Composable
fun rememberColorScheme(
    isDarkTheme: Boolean,
    amoledMode: Boolean,
    colorTuple: ColorTuple,
    style: PaletteStyle,
    contrastLevel: Double,
    dynamicColor: Boolean,
    isInvertColors: Boolean,
    colorBlindType: ColorBlindType? = null,
    dynamicColorsOverride: ((isDarkTheme: Boolean) -> ColorTuple?)? = null,
): ColorScheme {
    val context = LocalContext.current
    return remember(
        colorTuple,
        isDarkTheme,
        amoledMode,
        contrastLevel,
        dynamicColor,
        style,
        isInvertColors,
        colorBlindType,
        dynamicColorsOverride
    ) {
        derivedStateOf {
            context.getColorScheme(
                isDarkTheme = isDarkTheme,
                amoledMode = amoledMode,
                colorTuple = colorTuple,
                style = style,
                contrastLevel = contrastLevel,
                dynamicColor = dynamicColor,
                isInvertColors = isInvertColors,
                colorBlindType = colorBlindType,
                dynamicColorsOverride = dynamicColorsOverride
            )
        }
    }.value
}

fun Context.getColorScheme(
    isDarkTheme: Boolean,
    amoledMode: Boolean,
    colorTuple: ColorTuple,
    style: PaletteStyle,
    contrastLevel: Double,
    dynamicColor: Boolean,
    isInvertColors: Boolean,
    colorBlindType: ColorBlindType? = null,
    dynamicColorsOverride: ((isDarkTheme: Boolean) -> ColorTuple?)? = null,
): ColorScheme {
    val overridden = if (dynamicColor) dynamicColorsOverride?.invoke(isDarkTheme) else null
    val colorTuple = overridden ?: colorTuple

    val colorScheme =
        if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && overridden == null) {
            if (isDarkTheme) {
                dynamicDarkColorScheme(this)
            } else {
                dynamicLightColorScheme(this)
            }
        } else {
            val hct = Hct.fromInt(colorTuple.primary.toArgb())
            val hue = hct.hue
            val chroma = hct.chroma

            val a1 = colorTuple.primary.let { TonalPalette.fromInt(it.toArgb()) }

            val a2 = colorTuple.secondary?.toArgb().let {
                if (it != null) {
                    TonalPalette.fromInt(it)
                } else {
                    TonalPalette.fromHueAndChroma(hue, chroma / 3.0)
                }
            }

            val a3 = colorTuple.tertiary?.toArgb().let {
                if (it != null) {
                    TonalPalette.fromInt(it)
                } else {
                    TonalPalette.fromHueAndChroma(hue + 60.0, chroma / 2.0)
                }
            }

            val n1 = colorTuple.surface?.toArgb().let {
                if (it != null) {
                    TonalPalette.fromInt(it)
                } else {
                    TonalPalette.fromHueAndChroma(hue, (chroma / 12.0).coerceAtMost(4.0))
                }
            }

            val n2 = TonalPalette.fromInt(n1.tone(90))

            val scheme = when (style) {
                PaletteStyle.TonalSpot -> DynamicScheme(
                    hct, Variant.TONAL_SPOT, isDarkTheme, contrastLevel, a1, a2, a3, n1, n2
                )

                PaletteStyle.Neutral -> SchemeNeutral(hct, isDarkTheme, contrastLevel)
                PaletteStyle.Vibrant -> SchemeVibrant(hct, isDarkTheme, contrastLevel)
                PaletteStyle.Expressive -> SchemeExpressive(hct, isDarkTheme, contrastLevel)
                PaletteStyle.Rainbow -> SchemeRainbow(hct, isDarkTheme, contrastLevel)
                PaletteStyle.FruitSalad -> SchemeFruitSalad(hct, isDarkTheme, contrastLevel)
                PaletteStyle.Monochrome -> SchemeMonochrome(hct, isDarkTheme, contrastLevel)
                PaletteStyle.Fidelity -> SchemeFidelity(hct, isDarkTheme, contrastLevel)
                PaletteStyle.Content -> SchemeContent(hct, isDarkTheme, contrastLevel)
            }

            scheme.toColorScheme()
        }


    return colorScheme
        .toAmoled(amoledMode && isDarkTheme)
        .toColorBlind(colorBlindType)
        .invertColors(isInvertColors && !dynamicColor)
        .run {
            copy(
                outlineVariant = onSecondaryContainer
                    .copy(alpha = 0.2f)
                    .compositeOver(surfaceColorAtElevation(6.dp))
            )
        }
}

private fun ColorScheme.invertColors(
    enabled: Boolean
): ColorScheme {

    fun Color.invertColor(): Color = if (enabled) {
        Color(this.toArgb() xor 0x00ffffff)
    } else this

    return this.copy(
        primary = primary.invertColor(),
        onPrimary = onPrimary.invertColor(),
        primaryContainer = primaryContainer.invertColor(),
        onPrimaryContainer = onPrimaryContainer.invertColor(),
        inversePrimary = inversePrimary.invertColor(),
        secondary = secondary.invertColor(),
        onSecondary = onSecondary.invertColor(),
        secondaryContainer = secondaryContainer.invertColor(),
        onSecondaryContainer = onSecondaryContainer.invertColor(),
        tertiary = tertiary.invertColor(),
        onTertiary = onTertiary.invertColor(),
        tertiaryContainer = tertiaryContainer.invertColor(),
        onTertiaryContainer = onTertiaryContainer.invertColor(),
        background = background.invertColor(),
        onBackground = onBackground.invertColor(),
        surface = surface.invertColor(),
        onSurface = onSurface.invertColor(),
        surfaceVariant = surfaceVariant.invertColor(),
        onSurfaceVariant = onSurfaceVariant.invertColor(),
        surfaceTint = surfaceTint.invertColor(),
        inverseSurface = inverseSurface.invertColor(),
        inverseOnSurface = inverseOnSurface.invertColor(),
        error = error.invertColor(),
        onError = onError.invertColor(),
        errorContainer = errorContainer.invertColor(),
        onErrorContainer = onErrorContainer.invertColor(),
        outline = outline.invertColor(),
        outlineVariant = outlineVariant.invertColor(),
        surfaceBright = surfaceBright.invertColor(),
        surfaceDim = surfaceDim.invertColor(),
        surfaceContainer = surfaceContainer.invertColor(),
        surfaceContainerHigh = surfaceContainerHigh.invertColor(),
        surfaceContainerHighest = surfaceContainerHighest.invertColor(),
        surfaceContainerLow = surfaceContainerLow.invertColor(),
        surfaceContainerLowest = surfaceContainerLowest.invertColor(),
        primaryFixed = primaryFixed.invertColor(),
        primaryFixedDim = primaryFixedDim.invertColor(),
        onPrimaryFixed = onPrimaryFixed.invertColor(),
        onPrimaryFixedVariant = onPrimaryFixedVariant.invertColor(),
        secondaryFixed = secondaryFixed.invertColor(),
        secondaryFixedDim = secondaryFixedDim.invertColor(),
        onSecondaryFixed = onSecondaryFixed.invertColor(),
        onSecondaryFixedVariant = onSecondaryFixedVariant.invertColor(),
        tertiaryFixed = tertiaryFixed.invertColor(),
        tertiaryFixedDim = tertiaryFixedDim.invertColor(),
        onTertiaryFixed = onTertiaryFixed.invertColor(),
        onTertiaryFixedVariant = onTertiaryFixedVariant.invertColor(),
    )
}

private fun DynamicScheme.toColorScheme(): ColorScheme {
    val colors = MaterialDynamicColors()
    val scheme = this
    return ColorScheme(
        primary = Color(colors.primary().getArgb(scheme)),
        onPrimary = Color(colors.onPrimary().getArgb(scheme)),
        primaryContainer = Color(colors.primaryContainer().getArgb(scheme)),
        onPrimaryContainer = Color(colors.onPrimaryContainer().getArgb(scheme)),
        inversePrimary = Color(colors.inversePrimary().getArgb(scheme)),
        secondary = Color(colors.secondary().getArgb(scheme)),
        onSecondary = Color(colors.onSecondary().getArgb(scheme)),
        secondaryContainer = Color(colors.secondaryContainer().getArgb(scheme)),
        onSecondaryContainer = Color(colors.onSecondaryContainer().getArgb(scheme)),
        tertiary = Color(colors.tertiary().getArgb(scheme)),
        onTertiary = Color(colors.onTertiary().getArgb(scheme)),
        tertiaryContainer = Color(colors.tertiaryContainer().getArgb(scheme)),
        onTertiaryContainer = Color(colors.onTertiaryContainer().getArgb(scheme)),
        background = Color(colors.background().getArgb(scheme)),
        onBackground = Color(colors.onBackground().getArgb(scheme)),
        surface = Color(colors.surface().getArgb(scheme)),
        onSurface = Color(colors.onSurface().getArgb(scheme)),
        surfaceVariant = Color(colors.surfaceVariant().getArgb(scheme)),
        onSurfaceVariant = Color(colors.onSurfaceVariant().getArgb(scheme)),
        surfaceTint = Color(colors.surfaceTint().getArgb(scheme)),
        inverseSurface = Color(colors.inverseSurface().getArgb(scheme)),
        inverseOnSurface = Color(colors.inverseOnSurface().getArgb(scheme)),
        error = Color(colors.error().getArgb(scheme)),
        onError = Color(colors.onError().getArgb(scheme)),
        errorContainer = Color(colors.errorContainer().getArgb(scheme)),
        onErrorContainer = Color(colors.onErrorContainer().getArgb(scheme)),
        outline = Color(colors.outline().getArgb(scheme)),
        outlineVariant = Color(colors.outlineVariant().getArgb(scheme)),
        scrim = Color(colors.scrim().getArgb(scheme)),
        surfaceBright = Color(colors.surfaceBright().getArgb(scheme)),
        surfaceDim = Color(colors.surfaceDim().getArgb(scheme)),
        surfaceContainer = Color(colors.surfaceContainer().getArgb(scheme)),
        surfaceContainerHigh = Color(colors.surfaceContainerHigh().getArgb(scheme)),
        surfaceContainerHighest = Color(colors.surfaceContainerHighest().getArgb(scheme)),
        surfaceContainerLow = Color(colors.surfaceContainerLow().getArgb(scheme)),
        surfaceContainerLowest = Color(colors.surfaceContainerLowest().getArgb(scheme)),
        primaryFixed = Color(colors.primaryFixed().getArgb(scheme)),
        primaryFixedDim = Color(colors.primaryFixedDim().getArgb(scheme)),
        onPrimaryFixed = Color(colors.onPrimaryFixed().getArgb(scheme)),
        onPrimaryFixedVariant = Color(colors.onPrimaryFixedVariant().getArgb(scheme)),
        secondaryFixed = Color(colors.secondaryFixed().getArgb(scheme)),
        secondaryFixedDim = Color(colors.secondaryFixedDim().getArgb(scheme)),
        onSecondaryFixed = Color(colors.onSecondaryFixed().getArgb(scheme)),
        onSecondaryFixedVariant = Color(colors.onSecondaryFixedVariant().getArgb(scheme)),
        tertiaryFixed = Color(colors.tertiaryFixed().getArgb(scheme)),
        tertiaryFixedDim = Color(colors.tertiaryFixedDim().getArgb(scheme)),
        onTertiaryFixed = Color(colors.onTertiaryFixed().getArgb(scheme)),
        onTertiaryFixedVariant = Color(colors.onTertiaryFixedVariant().getArgb(scheme)),
    )
}


private fun ColorScheme.toAmoled(amoledMode: Boolean): ColorScheme {
    fun Color.darken(fraction: Float = 0.5f): Color =
        Color(toArgb().blend(Color.Black.toArgb(), fraction))

    return if (amoledMode) {
        copy(
            primary = primary.darken(0.3f),
            onPrimary = onPrimary.darken(0.1f),
            primaryContainer = primaryContainer.darken(0.3f),
            onPrimaryContainer = onPrimaryContainer.darken(0.1f),
            inversePrimary = inversePrimary.darken(0.3f),
            secondary = secondary.darken(0.3f),
            onSecondary = onSecondary.darken(0.1f),
            secondaryContainer = secondaryContainer.darken(0.3f),
            onSecondaryContainer = onSecondaryContainer.darken(0.1f),
            tertiary = tertiary.darken(0.3f),
            onTertiary = onTertiary.darken(0.1f),
            tertiaryContainer = tertiaryContainer.darken(0.3f),
            onTertiaryContainer = onTertiaryContainer.darken(0.1f),
            background = Color.Black,
            onBackground = onBackground.darken(0.1f),
            surface = Color.Black,
            onSurface = onSurface.darken(0.1f),
            surfaceVariant = surfaceVariant.darken(0.1f),
            onSurfaceVariant = onSurfaceVariant.darken(0.1f),
            surfaceTint = surfaceTint,
            inverseSurface = inverseSurface.darken(),
            inverseOnSurface = inverseOnSurface.darken(0.1f),
            error = error.darken(0.3f),
            onError = onError.darken(0.1f),
            errorContainer = errorContainer.darken(0.3f),
            onErrorContainer = onErrorContainer.darken(0.1f),
            outline = outline.darken(0.2f),
            outlineVariant = outlineVariant.darken(0.2f),
            scrim = scrim.darken(),
            surfaceBright = surfaceBright.darken(),
            surfaceDim = surfaceDim.darken(),
            surfaceContainer = surfaceContainer.darken(),
            surfaceContainerHigh = surfaceContainerHigh.darken(),
            surfaceContainerHighest = surfaceContainerHighest.darken(),
            surfaceContainerLow = surfaceContainerLow.darken(),
            surfaceContainerLowest = surfaceContainerLowest.darken(),
            primaryFixed = primaryFixed.darken(0.3f),
            primaryFixedDim = primaryFixedDim.darken(0.3f),
            onPrimaryFixed = onPrimaryFixed.darken(0.1f),
            onPrimaryFixedVariant = onPrimaryFixedVariant.darken(0.1f),
            secondaryFixed = secondaryFixed.darken(0.3f),
            secondaryFixedDim = secondaryFixedDim.darken(0.3f),
            onSecondaryFixed = onSecondaryFixed.darken(0.1f),
            onSecondaryFixedVariant = onSecondaryFixedVariant.darken(0.1f),
            tertiaryFixed = tertiaryFixed.darken(0.3f),
            tertiaryFixedDim = tertiaryFixedDim.darken(0.3f),
            onTertiaryFixed = onTertiaryFixed.darken(0.1f),
            onTertiaryFixedVariant = onTertiaryFixedVariant.darken(0.1f),
        )
    } else this
}

val LocalDynamicThemeState: ProvidableCompositionLocal<DynamicThemeState> =
    compositionLocalOf { error("DynamicThemeState not present") }