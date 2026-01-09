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

package com.t8rin.imagetoolbox.feature.wallpapers_export.presentation.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowCircleRight
import androidx.compose.material.icons.rounded.ImageSearch
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.tryAll
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.FolderOpened
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.appSettingsIntent
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.manageAllFilesIntent
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.manageAppAllFilesIntent
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.requestPermissions
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalComponentActivity
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.feature.wallpapers_export.domain.model.Permission
import com.t8rin.imagetoolbox.feature.wallpapers_export.domain.model.WallpapersResult
import com.t8rin.imagetoolbox.feature.wallpapers_export.presentation.screenLogic.WallpapersExportComponent

@Composable
fun WallpapersControls(component: WallpapersExportComponent) {
    val isPortrait by isPortraitOrientationAsState()

    AnimatedContent(
        targetState = component.wallpapersState,
        contentKey = { it::class.simpleName },
        modifier = Modifier.fillMaxWidth()
    ) { state ->
        when (state) {
            is WallpapersResult.Success -> {
                Column {
                    if (isPortrait) {
                        Spacer(Modifier.height(16.dp))
                    }
                    QualitySelector(
                        imageFormat = component.imageFormat,
                        quality = component.quality,
                        onQualityChange = component::setQuality
                    )
                    if (component.imageFormat.canChangeCompressionValue) {
                        Spacer(Modifier.height(8.dp))
                    }
                    ImageFormatSelector(
                        value = component.imageFormat,
                        onValueChange = component::setImageFormat
                    )
                }
            }

            is WallpapersResult.Failed.NoPermissions -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isPortrait) {
                        Spacer(Modifier.height(12.dp))
                    }

                    state.missingPermissions.forEachIndexed { index, permission ->
                        PermissionItem(
                            permission = permission,
                            shape = ShapeDefaults.byIndex(
                                index = index,
                                size = state.missingPermissions.size
                            )
                        )
                    }
                }
            }

            is WallpapersResult.Loading -> {
                val screenHeight = LocalScreenSize.current.height
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = screenHeight * 0.3f),
                    contentAlignment = Alignment.Center
                ) {
                    EnhancedLoadingIndicator()
                }
            }
        }
    }
}

@Composable
private fun PermissionItem(
    permission: Permission,
    shape: Shape
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )
    val context = LocalComponentActivity.current

    PreferenceItem(
        title = permission.title,
        subtitle = permission.subtitle,
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            when (permission) {
                Permission.ManageExternalStorage -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        tryAll(
                            { launcher.launch(context.manageAppAllFilesIntent()) },
                            { launcher.launch(manageAllFilesIntent()) },
                            { launcher.launch(context.appSettingsIntent()) }
                        )
                    }
                }

                Permission.ReadMediaImages -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context.requestPermissions(listOf(Manifest.permission.READ_MEDIA_IMAGES))
                    }
                }
            }
        },
        shape = shape,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        endIcon = Icons.Rounded.ArrowCircleRight,
        startIcon = permission.icon
    )
}

private val Permission.icon: ImageVector
    get() = when (this) {
        Permission.ManageExternalStorage -> Icons.Rounded.FolderOpened
        Permission.ReadMediaImages -> Icons.Rounded.ImageSearch
    }

private val Permission.subtitle: String
    @Composable
    get() = when (this) {
        Permission.ManageExternalStorage -> stringResource(R.string.allow_access_to_all_files_for_wp)
        Permission.ReadMediaImages -> stringResource(R.string.allow_read_media_images_for_wp)
    }

private val Permission.title: String
    get() = when (this) {
        Permission.ManageExternalStorage -> "MANAGE_EXTERNAL_STORAGE"
        Permission.ReadMediaImages -> "READ_MEDIA_IMAGES"
    }