package ru.tech.imageresizershrinker.theme

import android.Manifest
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.cookhelper.dynamic.theme.ColorTuple
import com.cookhelper.dynamic.theme.DynamicTheme
import com.cookhelper.dynamic.theme.extractPrimaryColor
import com.cookhelper.dynamic.theme.rememberDynamicThemeState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.tech.imageresizershrinker.main_screen.components.*

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

@Composable
fun ImageResizerTheme(
    dynamicColor: Boolean = LocalDynamicColors.current,
    amoledMode: Boolean = LocalAmoledMode.current,
    content: @Composable () -> Unit
) {
    val darkTheme = LocalNightMode.current.isNightMode()

    val colorTuple = getAppColorTuple(dynamicColor)

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !darkTheme

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons,
            isNavigationBarContrastEnforced = false
        )
    }

    val state = rememberDynamicThemeState(colorTuple)

    LaunchedEffect(colorTuple) {
        state.updateColorTuple(colorTuple)
    }

    DynamicTheme(
        typography = Typography,
        state = state,
        amoledMode = amoledMode,
        isDarkTheme = darkTheme,
        content = content
    )
}

@Composable
fun getAppColorTuple(
    dynamicColor: Boolean = LocalDynamicColors.current,
    darkTheme: Boolean = LocalNightMode.current.isNightMode()
): ColorTuple {
    val context = LocalContext.current
    val appPrimary = LocalAppPrimaryColor.current
    return remember(
        LocalLifecycleOwner.current.lifecycle.observeAsState().value,
        dynamicColor,
        darkTheme,
        appPrimary
    ) {
        derivedStateOf {
            var colorTuple: ColorTuple
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
                        colorTuple = ColorTuple(
                            primary = primary,
                            secondary = secondary,
                            tertiary = tertiary
                        )
                    }
                }
                dynamicColor && wallColors != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
                    colorTuple = ColorTuple(
                        primary = Color(wallColors.primaryColor.toArgb()),
                        secondary = wallColors.secondaryColor?.toArgb()?.let { Color(it) },
                        tertiary = wallColors.tertiaryColor?.toArgb()?.let { Color(it) }
                    )
                }
                dynamicColor && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    colorTuple = ColorTuple(
                        primary = (wallpaperManager.drawable as BitmapDrawable).bitmap.extractPrimaryColor(),
                        secondary = null,
                        tertiary = null
                    )
                }
                else -> {
                    colorTuple = ColorTuple(primary = appPrimary, secondary = null, tertiary = null)
                }
            }
            colorTuple
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