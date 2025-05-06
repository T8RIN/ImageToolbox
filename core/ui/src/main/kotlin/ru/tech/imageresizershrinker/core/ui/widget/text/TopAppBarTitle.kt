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

package ru.tech.imageresizershrinker.core.ui.widget.text

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.Green
import ru.tech.imageresizershrinker.core.ui.theme.blend
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.rememberHumanFileSize

@Composable
fun <T : Any> TopAppBarTitle(
    title: String,
    input: T?,
    isLoading: Boolean,
    size: Long?,
    originalSize: Long? = null,
    updateOnSizeChange: Boolean = true
) {
    if (updateOnSizeChange) {
        AnimatedContent(
            targetState = Triple(
                input,
                isLoading,
                size
            ),
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.marquee()
        ) { (inp, loading, size) ->
            if (loading) {
                Text(
                    stringResource(R.string.loading)
                )
            } else if (inp == null || size == null) {
                AnimatedContent(targetState = title) {
                    Text(it)
                }
            } else {
                AnimatedContent(originalSize) { originalSize ->
                    val readableOriginal = if ((originalSize ?: 0) > 0) {
                        rememberHumanFileSize(originalSize ?: 0)
                    } else {
                        "? B"
                    }
                    val readableCompressed = if (size > 0) {
                        rememberHumanFileSize(size)
                    } else {
                        "(...)"
                    }
                    val isSizesEqual =
                        size == originalSize || readableCompressed == readableOriginal
                    val color = takeColorFromScheme {
                        when {
                            isSizesEqual || originalSize == null -> onBackground
                            size > originalSize -> error.blend(errorContainer)
                            size <= 0 -> tertiary
                            else -> Green
                        }
                    }

                    val textStyle = LocalTextStyle.current
                    val sizeString = stringResource(R.string.size, readableCompressed)

                    val text by remember(
                        originalSize,
                        isSizesEqual,
                        sizeString,
                        readableOriginal,
                        readableCompressed,
                        textStyle
                    ) {
                        derivedStateOf {
                            buildAnnotatedString {
                                append(
                                    if (originalSize == null || isSizesEqual) sizeString else ""
                                )
                                originalSize?.takeIf { !isSizesEqual }?.let {
                                    append(readableOriginal)
                                    append(" -> ")
                                    withStyle(textStyle.toSpanStyle().copy(color)) {
                                        append(readableCompressed)
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = text
                    )
                }
            }
        }
    } else {
        AnimatedContent(
            targetState = input to isLoading,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.marquee()
        ) { (inp, loading) ->
            if (loading) {
                Text(
                    stringResource(R.string.loading)
                )
            } else if (inp == null || size == null || size <= 0) {
                AnimatedContent(targetState = title) {
                    Text(it)
                }
            } else {
                val readableOriginal = rememberHumanFileSize(originalSize ?: 0)
                val readableCompressed = rememberHumanFileSize(size)
                val isSizesEqual =
                    size == originalSize || readableCompressed == readableOriginal
                val color = takeColorFromScheme {
                    when {
                        isSizesEqual || originalSize == null -> onBackground
                        size > originalSize -> error.blend(errorContainer)
                        else -> Green
                    }
                }

                val textStyle = LocalTextStyle.current
                val sizeString = stringResource(R.string.size, readableCompressed)

                val text by remember(
                    originalSize,
                    isSizesEqual,
                    sizeString,
                    readableOriginal,
                    readableCompressed,
                    textStyle
                ) {
                    derivedStateOf {
                        buildAnnotatedString {
                            append(
                                if (originalSize == null || isSizesEqual) sizeString else ""
                            )
                            originalSize?.takeIf { !isSizesEqual }?.let {
                                append(readableOriginal)
                                append(" -> ")
                                withStyle(textStyle.toSpanStyle().copy(color)) {
                                    append(readableCompressed)
                                }
                            }
                        }
                    }
                }

                Text(
                    text = text
                )
            }
        }
    }
}