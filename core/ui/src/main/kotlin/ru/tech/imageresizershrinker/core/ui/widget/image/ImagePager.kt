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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.ImageEdit
import ru.tech.imageresizershrinker.core.ui.theme.White
import ru.tech.imageresizershrinker.core.ui.utils.animation.PageCloseTransition
import ru.tech.imageresizershrinker.core.ui.utils.animation.PageOpenTransition
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePager(
    visible: Boolean,
    selectedUri: Uri?,
    uris: List<Uri>?,
    onNavigate: (Screen) -> Unit,
    onUriSelected: (Uri?) -> Unit,
    onShare: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = visible,
        modifier = Modifier.fillMaxSize(),
        enter = PageOpenTransition,
        exit = PageCloseTransition
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
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
                .pointerInput(Unit) {
                    detectTapGestures { }
                }
        ) {
            HorizontalPager(
                state = state,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 5
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Picture(
                        showTransparencyChecker = false,
                        model = uris?.getOrNull(page),
                        modifier = Modifier
                            .fillMaxSize()
                            .clipToBounds()
                            .zoomable(rememberZoomState(8f))
                            .systemBarsPadding()
                            .displayCutoutPadding(),
                        contentScale = ContentScale.Fit,
                        shape = RectangleShape
                    )
                }
            }
            EnhancedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                type = EnhancedTopAppBarType.Center,
                modifier = Modifier,
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
                                    contentDescription = stringResource(R.string.share),
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
                                    contentDescription = stringResource(R.string.edit),
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
                                contentDescription = stringResource(R.string.exit),
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
                    onNavigate(screen)
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