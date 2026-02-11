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

@file:Suppress("KotlinConstantConditions", "COMPOSE_APPLIER_CALL_MISMATCH")

package com.t8rin.imagetoolbox.feature.easter_egg.presentation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.imagetoolbox.core.resources.BuildConfig
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.emoji.Emoji
import com.t8rin.imagetoolbox.core.resources.shapes.MaterialStarShape
import com.t8rin.imagetoolbox.core.ui.utils.confetti.LocalConfettiHostState
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppVersion
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.EmojiItem
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.easter_egg.presentation.screenLogic.EasterEggComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun EasterEggContent(
    component: EasterEggComponent
) {
    val confettiHostState = LocalConfettiHostState.current
    val themeState = LocalDynamicThemeState.current
    val allEmojis = Emoji.allIcons()
    val emojiData = remember {
        mutableStateListOf<String>().apply {
            addAll(
                List(11) {
                    allEmojis.random().toString()
                }
            )
        }
    }
    val counter: MutableState<Int> = remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(700)
            if (counter.value > 10) counter.value = 0
            emojiData[counter.value] = allEmojis.random().toString()
            emojiData[10 - counter.value] = allEmojis.random().toString()
            counter.value++
        }
    }

    val painter = painterResource(R.drawable.ic_launcher_foreground)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        topBar = {
            EnhancedTopAppBar(
                title = {
                    Row(modifier = Modifier.marquee()) {
                        emojiData.forEach { emoji ->
                            EmojiItem(
                                emoji = emoji,
                                fontScale = 1f
                            )
                        }
                    }
                },
                navigationIcon = {
                    EnhancedIconButton(
                        onClick = component.onGoBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.exit)
                        )
                    }
                },
                type = EnhancedTopAppBarType.Center
            )
        },
        contentWindowInsets = WindowInsets()
    ) { contentPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            val density = LocalDensity.current
            val width = constraints.maxWidth
            val height = constraints.maxHeight
            val ballSize = remember(maxWidth, maxHeight) {
                (min(maxWidth, maxHeight) * 0.3f).coerceAtMost(180.dp)
            }
            val ballSizePx = remember(density, ballSize) {
                with(density) { ballSize.toPx().roundToInt() }
            }
            var speed by remember { mutableFloatStateOf(0.2f) }

            var x by remember { mutableFloatStateOf((width - ballSizePx) * 1f) }
            var y by remember { mutableFloatStateOf((height - ballSizePx) * 1f) }

            val animatedX = animateFloatAsState(x)
            val animatedY = animateFloatAsState(y)

            var xSpeed by rememberSaveable { mutableFloatStateOf(10f) }
            var ySpeed by rememberSaveable { mutableFloatStateOf(10f) }
            val rotation = rememberInfiniteTransition().animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2500),
                    repeatMode = RepeatMode.Reverse
                )
            )

            var bounces by remember {
                mutableIntStateOf(0)
            }

            LaunchedEffect(bounces) {
                if (bounces % 10 == 0) {
                    themeState.updateColorTuple(ColorTuple(Color(Random.nextInt())))
                }
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

                    delay(10)
                }
            }

            val icons = remember {
                Screen.entries.mapNotNull { it.icon }.shuffled()
            }
            val tint = MaterialTheme.colorScheme.secondary.copy(0.8f)
            val minIconSize = remember(density) {
                with(density) { 22.dp.roundToPx() }
            }


            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                repeat(height / minIconSize) {
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        repeat(width / minIconSize) {
                            val icon = remember(it) { icons.random() }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(1.dp),
                                tint = tint
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = animatedX.value
                        translationY = animatedY.value
                        rotationZ = rotation.value
                    },
                contentAlignment = Alignment.Center
            ) {
                val scope = rememberCoroutineScope()
                Column(
                    modifier = Modifier
                        .container(
                            shape = MaterialStarShape,
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        )
                        .hapticsClickable {
                            speed = if (speed == 0.2f) {
                                Random.nextFloat()
                            } else 0.2f
                            scope.launch {
                                confettiHostState.showConfetti()
                            }
                        }
                        .size(ballSize)
                        .graphicsLayer {
                            rotationZ = -rotation.value
                        },
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painter,
                        contentDescription = stringResource(R.string.version),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier
                            .size(ballSize * 0.6f)
                            .weight(1f, false)
                    )
                    Column(
                        modifier = Modifier.offset(
                            y = -ballSize * 0.15f
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
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
                            text = "$AppVersion\n(${BuildConfig.VERSION_CODE})",
                            style = LocalTextStyle.current.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 14.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.5f)
                            ),
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}