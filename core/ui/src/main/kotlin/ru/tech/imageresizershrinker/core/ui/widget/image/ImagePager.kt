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

package ru.tech.imageresizershrinker.core.ui.widget.image

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.ui.icons.material.ImageEdit
import ru.tech.imageresizershrinker.core.ui.theme.White
import ru.tech.imageresizershrinker.core.ui.utils.navigation.LocalNavController
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ImagePager(
    visible: Boolean,
    selectedUri: Uri?,
    uris: List<Uri>?,
    onUriSelected: (Uri?) -> Unit,
    onShare: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current

    AnimatedVisibility(
        visible = visible,
        modifier = Modifier.fillMaxSize(),
        enter = slideInHorizontally(
            animationSpec()
        ) { -it / 3 } + fadeIn(
            animationSpec(500)
        ),
        exit = slideOutHorizontally(
            animationSpec()
        ) { -it / 3 } + fadeOut(
            animationSpec(500)
        )
    ) {
        val wantToEdit = rememberSaveable { mutableStateOf(false) }
        val state = rememberPagerState(
            initialPage = selectedUri?.let {
                uris?.indexOf(it)
            } ?: 0,
            pageCount = {
                uris?.size ?: 0
            }
        )
        LaunchedEffect(state.currentPage) {
            onUriSelected(
                uris?.getOrNull(state.currentPage)
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
                .pointerInput(Unit) {
                    detectTapGestures { }
                }
        ) {
            HorizontalPager(
                state = state,
                modifier = Modifier.fillMaxSize(),
                outOfBoundsPageCount = 5
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Picture(
                        showTransparencyChecker = false,
                        model = uris?.getOrNull(page),
                        modifier = Modifier
                            .fillMaxSize()
                            .zoomable(rememberZoomState(8f))
                            .systemBarsPadding()
                            .displayCutoutPadding(),
                        contentScale = ContentScale.Fit,
                        shape = RectangleShape
                    )
                }
            }
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    uris?.size?.takeIf { it > 1 }?.let {
                        Text(
                            text = "${state.currentPage + 1}/$it",
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                                    CircleShape
                                )
                                .padding(vertical = 4.dp, horizontal = 12.dp),
                            color = White
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = !uris.isNullOrEmpty(),
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                                    CircleShape
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        selectedUri?.let { onShare(it) }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = null,
                                    tint = White
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        wantToEdit.value = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ImageEdit,
                                    contentDescription = null,
                                    tint = White
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    AnimatedVisibility(!uris.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .size(48.dp)
                                .background(
                                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                                    CircleShape
                                )
                                .padding(4.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onDismiss()
                                    onUriSelected(null)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null,
                                tint = White
                            )
                        }
                    }
                },
            )
        }

        ProcessImagesPreferenceSheet(
            uris = listOfNotNull(selectedUri),
            visible = wantToEdit,
            navigate = { screen ->
                scope.launch {
                    wantToEdit.value = false
                    delay(200)
                    navController.navigate(screen)
                }
            }
        )

        if (visible) {
            BackHandler {
                onDismiss()
                onUriSelected(null)
            }
        }
    }
}

private fun <T> animationSpec(
    duration: Int = 500,
    delay: Int = 0
) = tween<T>(
    durationMillis = duration,
    delayMillis = delay,
    easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
)