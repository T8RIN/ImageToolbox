package ru.tech.imageresizershrinker.main_screen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.dp
import com.cookhelper.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.*
import nl.dionsegijn.konfetti.core.emitter.Emitter
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.Screen
import ru.tech.imageresizershrinker.resize_screen.components.blend
import ru.tech.imageresizershrinker.utils.LocalWindowSizeClass
import java.lang.Integer.max
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun MainScreen(navController: NavController<Screen>) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val isGrid = LocalWindowSizeClass.current.widthSizeClass != WindowWidthSizeClass.Compact
    val colorScheme = MaterialTheme.colorScheme
    val themeState = LocalDynamicThemeState.current

    var showConfetti by remember { mutableStateOf(false) }

    val colors = remember(colorScheme) {
        colorScheme.run {
            listOf(
                primary,
                secondary,
                tertiary,
                primaryContainer,
                secondaryContainer,
                tertiaryContainer,
                Color.Red.blend(primary, 0.4f),
                Color.Green.blend(primary, 0.4f),
                Color.Yellow.blend(primary, 0.4f),
                Color.Magenta.blend(primary, 0.4f),
                Color.Cyan.blend(primary, 0.4f),
                Color.Red.blend(Color.Yellow, 1f).blend(primary),
            )
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        LargeTopAppBar(
            title = {
                var scaleState by remember { mutableStateOf(1f) }
                val scale by animateFloatAsState(scaleState)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.app_name))
                    Spacer(Modifier.width(12.dp))
                    Box(
                        Modifier
                            .scale(scale)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        scaleState = 1.3f
                                        delay(200)
                                        tryAwaitRelease()
                                        showConfetti = true
                                        themeState.updateColor(colors.random())
                                        scaleState = 0.8f
                                        delay(200)
                                        scaleState = 1f
                                    }
                                )
                            }
                    ) {
                        Text(
                            text = stringResource(R.string.star_symbol),
                            style = LocalTextStyle.current.copy(
                                shadow = Shadow(
                                    color = MaterialTheme.colorScheme.scrim,
                                    offset = Offset(0f, 0f),
                                    blurRadius = 2f
                                )
                            )
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    3.dp
                )
            ),
            modifier = Modifier.shadow(6.dp),
            scrollBehavior = scrollBehavior,
        )

        val footer: @Composable ColumnScope.() -> Unit = {
            Spacer(modifier = Modifier.weight(1f))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.version) + " ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Column(
            Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isGrid) {
                Spacer(modifier = Modifier.height(16.dp))
                SingleResizePreference(onClick = { navController.navigate(Screen.SingleResize) })
                Spacer(modifier = Modifier.height(16.dp))
                BatchResizePreference(onClick = { navController.navigate(Screen.BatchResize) })
                Spacer(modifier = Modifier.height(16.dp))
                CropPreference(onClick = { navController.navigate(Screen.Crop) })
                Spacer(modifier = Modifier.height(16.dp))
                PickColorPreference(onClick = { navController.navigate(Screen.PickColor) })
                Spacer(modifier = Modifier.height(32.dp))
                SourceCodePreference()
                Spacer(modifier = Modifier.height(16.dp))
                footer()
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    var heightOne by remember { mutableStateOf(0) }
                    var heightTwo by remember { mutableStateOf(0) }
                    SingleResizePreference(
                        onClick = { navController.navigate(Screen.SingleResize) },
                        modifier = Modifier
                            .then(
                                if (heightOne != 0 && heightTwo != 0) {
                                    Modifier.height(
                                        with(LocalDensity.current) {
                                            max(heightOne, heightTwo).toDp()
                                        }
                                    )
                                } else Modifier
                            )
                            .widthIn(max = 350.dp)
                            .fillMaxWidth()
                            .onSizeChanged {
                                heightOne = it.height
                            }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    BatchResizePreference(
                        onClick = { navController.navigate(Screen.BatchResize) },
                        modifier = Modifier
                            .then(
                                if (heightOne != 0 && heightTwo != 0) {
                                    Modifier.height(
                                        with(LocalDensity.current) {
                                            max(heightOne, heightTwo).toDp()
                                        }
                                    )
                                } else Modifier
                            )
                            .widthIn(max = 350.dp)
                            .fillMaxWidth()
                            .onSizeChanged {
                                heightTwo = it.height
                            }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    var heightOne by remember { mutableStateOf(0) }
                    var heightTwo by remember { mutableStateOf(0) }
                    PickColorPreference(
                        onClick = { navController.navigate(Screen.PickColor) },
                        modifier = Modifier
                            .then(
                                if (heightOne != 0 && heightTwo != 0) {
                                    Modifier.height(
                                        with(LocalDensity.current) {
                                            max(heightOne, heightTwo).toDp()
                                        }
                                    )
                                } else Modifier
                            )
                            .widthIn(max = 350.dp)
                            .fillMaxWidth()
                            .onSizeChanged {
                                heightOne = it.height
                            }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    CropPreference(
                        onClick = { navController.navigate(Screen.Crop) },
                        modifier = Modifier
                            .then(
                                if (heightOne != 0 && heightTwo != 0) {
                                    Modifier.height(
                                        with(LocalDensity.current) {
                                            max(heightOne, heightTwo).toDp()
                                        }
                                    )
                                } else Modifier
                            )
                            .widthIn(max = 350.dp)
                            .fillMaxWidth()
                            .onSizeChanged {
                                heightTwo = it.height
                            }
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                SourceCodePreference(
                    modifier = Modifier
                        .widthIn(max = 350.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                footer()
            }
        }
    }

    if (showConfetti) {
        val primary = MaterialTheme.colorScheme.primary
        KonfettiView(
            modifier = Modifier.fillMaxSize(),
            parties = remember { particles(primary) },
            updateListener = object : OnParticleSystemUpdateListener {
                override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                    if (activeSystems == 0) showConfetti = false
                }
            }
        )
    }
}

private fun particles(primary: Color) = listOf(
    Party(
        speed = 0f,
        maxSpeed = 15f,
        damping = 0.9f,
        angle = Angle.BOTTOM,
        spread = Spread.ROUND,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def).map {
            it.blend(
                primary
            )
        },
        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
        position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0))
    ),
    Party(
        speed = 10f,
        maxSpeed = 30f,
        damping = 0.9f,
        angle = Angle.RIGHT - 45,
        spread = Spread.SMALL,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def).map {
            it.blend(
                primary
            )
        },
        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(30),
        position = Position.Relative(0.0, 1.0)
    ),
    Party(
        speed = 10f,
        maxSpeed = 30f,
        damping = 0.9f,
        angle = Angle.RIGHT - 135,
        spread = Spread.SMALL,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def).map {
            it.blend(
                primary
            )
        },
        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(30),
        position = Position.Relative(1.0, 1.0)
    )
)