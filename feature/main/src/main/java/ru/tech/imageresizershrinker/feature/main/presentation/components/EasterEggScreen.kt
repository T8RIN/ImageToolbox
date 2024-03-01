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

@file:Suppress("KotlinConstantConditions")

package ru.tech.imageresizershrinker.feature.main.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.BuildConfig
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.emoji.Emoji
import ru.tech.imageresizershrinker.core.ui.shapes.MaterialStarShape
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.other.EmojiItem
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun EasterEggScreen(
    onGoBack: () -> Unit
) {
    val confettiController = LocalConfettiController.current
    val themeState = LocalDynamicThemeState.current
    val allEmojis = Emoji.allIcons()
    val emojiData = remember {
        mutableStateListOf<String>().apply {
            addAll(
                List(13) {
                    allEmojis.random().toString()
                }
            )
        }
    }
    val counter: MutableState<Int> = remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(700)
            if (counter.value > 12) counter.value = 0
            emojiData[counter.value] = allEmojis.random().toString()
            emojiData[12 - counter.value] = allEmojis.random().toString()
            counter.value++
        }
    }

    val painter = painterResource(R.drawable.ic_launcher_monochrome)

    BoxWithConstraints {
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val ballSize = 120.dp
        val ballSizePx = with(LocalDensity.current) { ballSize.toPx().roundToInt() }
        var speed by remember { mutableFloatStateOf(1f) }

        var x by rememberSaveable { mutableFloatStateOf((width - ballSizePx) * 1f) }
        var y by rememberSaveable { mutableFloatStateOf((height - ballSizePx) * 1f) }

        val animatedX = animateFloatAsState(x)
        val animatedY = animateFloatAsState(y)

        var xSpeed by rememberSaveable { mutableFloatStateOf(10f) }
        var ySpeed by rememberSaveable { mutableFloatStateOf(10f) }

        val scale = remember {
            Animatable(1f)
        }
        val rotation = remember {
            Animatable(0f)
        }
        var bounces by remember {
            mutableIntStateOf(0)
        }
        LaunchedEffect(bounces) {
            themeState.updateColorTuple(ColorTuple(Color(Random.nextInt())))
            launch {
                scale.animateTo(
                    (Random.nextFloat() * 2).coerceIn(0.5f..1.3f)
                )
            }
            launch {
                rotation.animateTo(Random.nextInt(0..360).toFloat())
            }
            speed = Random.nextFloat() * 10f
        }

        LaunchedEffect(speed) {
            while (isActive) {
                x += xSpeed * speed
                y += ySpeed * speed

                val rightBounce = x > width - ballSizePx
                val leftBounce = x < 0
                val bottomBounce = y > height - ballSizePx
                val topBounce = y < 0

                if (rightBounce || leftBounce) {
                    xSpeed = -xSpeed
                    bounces++
                }

                if (topBounce || bottomBounce) {
                    ySpeed = -ySpeed
                    bounces++
                }

                delay(1)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Marquee {
                        Row {
                            emojiData.forEach { emoji ->
                                EmojiItem(
                                    emoji = emoji,
                                    fontScale = 1f
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    EnhancedIconButton(
                        onClick = onGoBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.exit)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )

            val dimens = width * height
            FlowRow(
                modifier = Modifier
                    .weight(1f)
            ) {
                repeat(dimens / (64 * 64)) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .graphicsLayer {
                                scaleX = scale.value
                                scaleY = scale.value
                                rotationX = rotation.value
                                rotationY = rotation.value
                                rotationZ = rotation.value
                                clip = false
                            }
                    ) {
                        Icon(
                            painter = painter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(64.dp)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier.offset {
                IntOffset(
                    animatedX.value.roundToInt(),
                    animatedY.value.roundToInt()
                )
            },
            contentAlignment = Alignment.Center
        ) {
            val scope = rememberCoroutineScope()
            Column(
                modifier = Modifier
                    .clip(MaterialStarShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .clickable {
                        scope.launch {
                            confettiController.showEmpty()
                        }
                    }
                    .size(ballSize),
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier
                        .size(72.dp)
                        .weight(1f, false)
                )
                Column(
                    modifier = Modifier.offset(
                        y = (-16).dp
                    )
                ) {
                    Text(
                        text = stringResource(R.string.version),
                        style = LocalTextStyle.current.copy(
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.9f)
                        ),
                        maxLines = 1
                    )
                    AutoSizeText(
                        text = "${BuildConfig.VERSION_NAME}${if (BuildConfig.FLAVOR == "foss") "-foss" else ""} (${BuildConfig.VERSION_CODE})",
                        style = LocalTextStyle.current.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 14.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.5f)
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }

    BackHandler(onBack = onGoBack)
}