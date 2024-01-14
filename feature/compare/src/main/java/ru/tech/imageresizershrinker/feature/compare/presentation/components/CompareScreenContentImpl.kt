package ru.tech.imageresizershrinker.feature.compare.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.beforeafter.BeforeAfterImage
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker

@Composable
internal fun CompareScreenContentImpl(
    compareType: CompareType,
    bitmapPair: Pair<Bitmap?, Bitmap?>,
    compareProgress: Float,
    onCompareProgressChange: (Float) -> Unit,
    isPortrait: Boolean
) {
    val modifier = Modifier
        .padding(16.dp)
        .container(RoundedCornerShape(16.dp))
        .padding(4.dp)
        .clip(RoundedCornerShape(12.dp))
        .transparencyChecker()

    AnimatedContent(targetState = compareType) { type ->
        when (type) {
            CompareType.Slide -> {
                AnimatedContent(targetState = bitmapPair) { data ->
                    data.let { (b, a) ->
                        val before = remember(data) { b?.asImageBitmap() }
                        val after = remember(data) { a?.asImageBitmap() }

                        if (before != null && after != null) {
                            BeforeAfterImage(
                                enableZoom = false,
                                modifier = modifier,
                                progress = animateFloatAsState(targetValue = compareProgress).value,
                                onProgressChange = onCompareProgressChange,
                                beforeImage = before,
                                afterImage = after,
                                beforeLabel = { },
                                afterLabel = { }
                            )
                        }
                    }
                }
            }

            CompareType.SideBySide -> {
                val first = bitmapPair.first
                val second = bitmapPair.second

                val zoomState = rememberZoomState(30f)
                val zoomModifier = Modifier.zoomable(
                    zoomState = zoomState
                )


                if (isPortrait) {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (first != null) {
                            Image(
                                bitmap = first.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .then(zoomModifier)
                            )
                            HorizontalDivider()
                        }
                        if (second != null) {
                            Image(
                                bitmap = second.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .then(zoomModifier)
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (first != null) {
                            Image(
                                bitmap = first.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .then(zoomModifier)
                            )
                            VerticalDivider()
                        }
                        if (second != null) {
                            Image(
                                bitmap = second.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .then(zoomModifier)
                            )
                        }
                    }
                }
            }

            CompareType.Tap -> {
                var showSecondImage by rememberSaveable {
                    mutableStateOf(false)
                }
                Box(
                    modifier = modifier
                        .pointerInput(Unit) {
                            detectTapGestures {
                                showSecondImage = !showSecondImage
                            }
                        }
                ) {
                    val first = bitmapPair.first
                    val second = bitmapPair.second
                    if (!showSecondImage && first != null) {
                        Image(
                            bitmap = first.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Inside
                        )
                    }
                    if (showSecondImage && second != null) {
                        Image(
                            bitmap = second.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Inside
                        )
                    }
                }
            }

            CompareType.Transparency -> {
                Box(
                    modifier = modifier
                ) {
                    val first = bitmapPair.first
                    val second = bitmapPair.second
                    if (first != null) {
                        Image(
                            bitmap = first.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Inside
                        )
                    }
                    if (second != null) {
                        Image(
                            bitmap = second.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Inside,
                            modifier = Modifier.alpha(compareProgress / 100f)
                        )
                    }
                }
            }
        }
    }
}