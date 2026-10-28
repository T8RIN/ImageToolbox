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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.t8rin.imagetoolbox.core.domain.utils.tryAll
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.BrokenImageAlt
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.appSettingsIntent
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.isExternalStorageManagerUnavailable
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.manageAllFilesIntent
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.manageAppAllFilesIntent
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.requestPermissions
import com.t8rin.imagetoolbox.core.ui.utils.permission.PermissionStatus
import com.t8rin.imagetoolbox.core.ui.utils.permission.PermissionUtils.checkPermissions
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalComponentActivity
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberCurrentLifecycleEvent
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategoryResult
import com.t8rin.imagetoolbox.feature.media_picker.presentation.screenLogic.MediaPickerComponent

@Composable
fun MediaPickerRootContentEmbeddable(
    component: MediaPickerComponent,
    onPicked: (List<Uri>) -> Unit,
    modifier: Modifier = Modifier,
    allowedMedia: AllowedMedia = AllowedMedia.Photos(null),
    allowMultiple: Boolean = true,
    onBack: (() -> Unit)? = null,
    isGalleryMode: Boolean = false
) {
    val context = LocalComponentActivity.current
    val mediaPermissions = remember {
        buildList {
            add(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            }
        }
    }

    var isPermissionAllowed by remember {
        mutableStateOf(true)
    }
    var isManagePermissionAllowed by remember {
        mutableStateOf(true)
    }
    var invalidator by remember {
        mutableIntStateOf(0)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        invalidator++
    }

    val requestManagePermission = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            tryAll(
                { launcher.launch(context.manageAppAllFilesIntent()) },
                { launcher.launch(manageAllFilesIntent()) },
                { launcher.launch(context.appSettingsIntent()) }
            )
        }
    }

    val lifecycleEvent = rememberCurrentLifecycleEvent()
    LaunchedEffect(lifecycleEvent, invalidator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.READ_MEDIA_IMAGES
            val status = context.checkPermissions(listOf(permission)).finalStatus
            val hasSelectedPhotos = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                    context.checkPermissions(
                        listOf(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                    ).finalStatus == PermissionStatus.ALLOWED
            isManagePermissionAllowed =
                Environment.isExternalStorageManager() || context.isExternalStorageManagerUnavailable()
            isPermissionAllowed = status == PermissionStatus.ALLOWED || hasSelectedPhotos
            when {
                isPermissionAllowed -> {
                    component.init(allowedMedia)
                }

                status == PermissionStatus.NOT_GIVEN -> {
                    ActivityCompat.requestPermissions(
                        context,
                        mediaPermissions.toTypedArray(),
                        0
                    )
                }
            }
        }
    }

    val albumsState by component.albumsState.collectAsStateWithLifecycle()
    val mediaState by component.mediaState.collectAsStateWithLifecycle()
    val semanticSearchState by component.semanticSearchState.collectAsStateWithLifecycle()
    val categories by component.semanticSearchCategories.collectAsStateWithLifecycle()
    val categoriesLoading by component.isSemanticSearchCategoriesLoading.collectAsStateWithLifecycle()
    val selectedCategory by component.selectedSemanticCategory.collectAsStateWithLifecycle()

    val currentCategories = remember(categories, selectedCategory) {
        categories.filter {
            it.category !is SemanticSearchCategory.Person &&
                    (it.media.isNotEmpty() || it.category.id == selectedCategory)
        }.sortedBy { it.category !is SemanticSearchCategory.Custom }
    }
    var previousCategories by remember { mutableStateOf(currentCategories) }
    SideEffect {
        if (!categoriesLoading) previousCategories = currentCategories
    }
    val visibleCategories = if (categoriesLoading) {
        previousCategories.ifEmpty {
            val customCategories = component.settingsState.semanticSearchSettings.customCategories
            remember(customCategories) {
                customCategories.map {
                    SemanticSearchCategoryResult(
                        category = SemanticSearchCategory.Custom(it.id, it.name, it.description),
                        media = emptyList()
                    )
                } + SemanticSearchCategory.BuiltIn.entries.map {
                    SemanticSearchCategoryResult(category = it, media = emptyList())
                }
            }
        }
    } else currentCategories

    val showCategories = allowedMedia != AllowedMedia.Videos &&
            component.settingsState.isSemanticSearchEnabled && semanticSearchState.modelsReady &&
            (visibleCategories.isNotEmpty() || component.settingsState.semanticSearchSettings.groupPeople)

    val content: @Composable (PaddingValues) -> Unit = {
        AnimatedContent(
            targetState = isPermissionAllowed,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
        ) { havePermissions ->
            if (havePermissions) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MediaPickerHavePermissions(
                        allowedMedia = allowedMedia,
                        allowMultiple = allowMultiple,
                        component = component,
                        isGalleryMode = isGalleryMode,
                        isManagePermissionAllowed = isManagePermissionAllowed,
                        onRequestManagePermission = requestManagePermission,
                        onPicked = onPicked,
                        albumsState = albumsState,
                        mediaState = mediaState,
                        showCategories = showCategories,
                        visibleCategories = visibleCategories,
                        selectedCategory = selectedCategory,
                        categoriesLoading = categoriesLoading
                    )
                    LaunchedEffect(Unit) {
                        component.init(allowedMedia = allowedMedia)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BrokenImageAlt,
                        contentDescription = null,
                        modifier = Modifier.size(108.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.no_permissions),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    EnhancedButton(
                        onClick = {
                            context.requestPermissions(mediaPermissions)
                        }
                    ) {
                        Text(stringResource(id = R.string.request))
                    }
                }
            }
        }
    }

    Box(modifier = modifier) {
        if (onBack == null) {
            content(PaddingValues())
        } else {
            Scaffold(
                topBar = {
                    EnhancedTopAppBar(
                        title = {
                            Text(
                                text = when {
                                    isGalleryMode -> stringResource(R.string.gallery_picker)
                                    allowMultiple -> stringResource(R.string.pick_multiple_media)
                                    else -> stringResource(R.string.pick_single_media)
                                },
                                modifier = Modifier.marquee()
                            )
                        },
                        navigationIcon = {
                            EnhancedIconButton(
                                onClick = onBack,
                                containerColor = Color.Transparent
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowBack,
                                    contentDescription = stringResource(R.string.close)
                                )
                            }
                        },
                        actions = {
                            TopAppBarEmoji()
                        },
                        drawHorizontalStroke = !showCategories && albumsState.albums.size <= 1 && !(mediaState.isLoading && mediaState.media.isNotEmpty())
                    )
                },
                content = content
            )
        }
    }
}