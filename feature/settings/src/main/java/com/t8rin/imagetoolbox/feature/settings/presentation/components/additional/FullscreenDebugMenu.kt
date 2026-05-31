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

package com.t8rin.imagetoolbox.feature.settings.presentation.components.additional

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.ui.utils.helper.PredictiveBackObserver
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.toShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.withLayoutCorners
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.modalsheet.FullscreenPopup
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun FullscreenDebugMenu(
    showMenuState: MutableState<Boolean>,
    content: @Composable ColumnScope.() -> Unit
) {
    var showMenu by showMenuState
    val onDismiss = { showMenu = false }

    val dialogVisible by produceState(showMenu) {
        snapshotFlow { showMenu }
            .collectLatest {
                if (!it) delay(600)
                value = it
            }
    }
    val visible by produceState(showMenu) {
        snapshotFlow { showMenu }
            .collectLatest {
                if (it) delay(100)
                value = it
            }
    }

    var predictiveBackProgress by remember {
        mutableFloatStateOf(0f)
    }

    LaunchedEffect(predictiveBackProgress, visible) {
        if (!visible && predictiveBackProgress != 0f) {
            delay(400)
            predictiveBackProgress = 0f
        }
    }

    if (dialogVisible) {
        FullscreenPopup {
            AnimatedVisibility(
                visible = visible,
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(tween(400)),
                exit = fadeOut(tween(400))
            ) {
                val animatedPredictiveBackProgress by animateFloatAsState(predictiveBackProgress)
                val scale = (1f - animatedPredictiveBackProgress).coerceAtLeast(0.75f)

                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(0.5f * scale))
                )

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .withLayoutCorners { corners ->
                            graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.shape = corners.toShape(animatedPredictiveBackProgress)
                                clip = true
                            }
                        }
                ) {
                    Scaffold(
                        topBar = {
                            EnhancedTopAppBar(
                                title = {
                                    Text(stringResource(R.string.debug_menu))
                                },
                                navigationIcon = {
                                    EnhancedIconButton(
                                        onClick = onDismiss
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.ArrowBack,
                                            contentDescription = stringResource(R.string.close)
                                        )
                                    }
                                },
                                actions = {
                                    TopAppBarEmoji()
                                }
                            )
                        },
                        contentWindowInsets = WindowInsets()
                    ) { contentPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .enhancedVerticalScroll(rememberScrollState())
                                .padding(contentPadding)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            content = content
                        )
                    }
                }
                if (visible) {
                    PredictiveBackObserver(
                        onProgress = {
                            predictiveBackProgress = it / 6f
                        },
                        onClean = { isCompleted ->
                            if (isCompleted) {
                                onDismiss()
                                delay(400)
                            }
                            predictiveBackProgress = 0f
                        }
                    )
                }
            }
        }
    }
}