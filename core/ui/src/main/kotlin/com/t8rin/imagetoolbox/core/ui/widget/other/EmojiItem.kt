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

package com.t8rin.imagetoolbox.core.ui.widget.other

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.lottiefiles.dotlottie.core.compose.runtime.DotLottieController
import com.lottiefiles.dotlottie.core.compose.runtime.DotLottiePlayerState
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource
import com.t8rin.imagetoolbox.core.domain.utils.throttleLatest
import com.t8rin.imagetoolbox.core.resources.shapes.CloverShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shimmer
import com.t8rin.imagetoolbox.core.utils.appContext
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EmojiItem(
    emoji: String?,
    animatedEmoji: String? = null,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = LocalTextStyle.current.fontSize,
    fontScale: Float,
    filterQuality: FilterQuality = FilterQuality.High,
    crossfade: Boolean = true,
    animateEmojiChange: Boolean = true,
    containerColor: Color = Color.Unspecified,
    shape: Shape? = null,
    onNoEmoji: @Composable (size: Dp) -> Unit = {}
) {
    key(animatedEmoji) {
        val dens = LocalDensity.current
        val density by remember(dens, fontScale) {
            derivedStateOf {
                Density(
                    density = dens.density,
                    fontScale = fontScale
                )
            }
        }
        val size by remember(fontSize, density) {
            derivedStateOf {
                with(density) {
                    fontSize.toDp()
                }
            }
        }

        val content: @Composable (emoji: String?) -> Unit = { currentEmoji ->
            currentEmoji?.let {
                var shimmering by remember(currentEmoji) {
                    mutableStateOf(true)
                }
                val emojiModifier = remember(size, shimmering) {
                    Modifier
                        .size(size + 4.dp)
                        .clip(shape ?: CloverShape)
                        .then(
                            if (containerColor.isSpecified) {
                                Modifier.background(containerColor)
                            } else {
                                Modifier
                            }
                        )
                        .shimmer(shimmering)
                        .padding(2.dp)
                }

                Box {
                    if (animatedEmoji != null) {
                        val controller = remember { DotLottieController() }

                        LaunchedEffect(controller) {
                            controller.currentState.throttleLatest(100).collectLatest {
                                shimmering = it != DotLottiePlayerState.PLAYING
                            }
                        }

                        DotLottieAnimation(
                            source = remember(animatedEmoji) {
                                DotLottieSource.Asset(animatedEmoji.removePrefix("file:///android_asset/"))
                            },
                            loop = true,
                            autoplay = true,
                            controller = controller,
                            modifier = emojiModifier
                        )
                    } else {
                        val requestSize = remember(size, density) {
                            with(density) {
                                (size + 4.dp).roundToPx().coerceAtLeast(1)
                            }
                        }
                        val painter = rememberAsyncImagePainter(
                            model = remember(currentEmoji, requestSize, crossfade) {
                                ImageRequest.Builder(appContext)
                                    .data(currentEmoji)
                                    .memoryCacheKey(currentEmoji)
                                    .diskCacheKey(currentEmoji)
                                    .size(requestSize)
                                    .crossfade(crossfade)
                                    .listener(
                                        onStart = {
                                            shimmering = true
                                        },
                                        onSuccess = { _, _ ->
                                            shimmering = false
                                        }
                                    )
                                    .build()
                            },
                            imageLoader = appContext.imageLoader,
                            filterQuality = filterQuality
                        )

                        Icon(
                            painter = painter,
                            contentDescription = currentEmoji,
                            modifier = remember(size) {
                                Modifier
                                    .size(size + 4.dp)
                                    .offset(1.dp, 1.dp)
                                    .padding(2.dp)
                            },
                            tint = Color(0, 0, 0, 40)
                        )
                        Icon(
                            painter = painter,
                            contentDescription = currentEmoji,
                            modifier = emojiModifier,
                            tint = Color.Unspecified
                        )
                    }
                }
            } ?: onNoEmoji(size)
        }

        if (animateEmojiChange) {
            AnimatedContent(
                targetState = emoji,
                modifier = modifier,
                transitionSpec = {
                    fadeIn() + scaleIn(initialScale = 0.85f) togetherWith fadeOut() + scaleOut(
                        targetScale = 0.85f
                    )
                }
            ) { currentEmoji ->
                content(currentEmoji)
            }
        } else {
            Box(modifier) {
                content(emoji)
            }
        }
    }
}