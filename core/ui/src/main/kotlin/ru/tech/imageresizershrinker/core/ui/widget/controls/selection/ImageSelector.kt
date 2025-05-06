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

package ru.tech.imageresizershrinker.core.ui.widget.controls.selection

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.MiniEdit
import ru.tech.imageresizershrinker.core.resources.shapes.CloverShape
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.Picker
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberFilePicker
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.rememberFilename
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItemOverload

@Composable
fun ImageSelector(
    value: Any?,
    onValueChange: (Uri) -> Unit,
    title: String = stringResource(id = R.string.image),
    subtitle: String?,
    modifier: Modifier = Modifier,
    autoShadowElevation: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    shape: Shape = RoundedCornerShape(20.dp),
    contentScale: ContentScale = ContentScale.Crop
) {
    val imagePicker = rememberImagePicker(onSuccess = onValueChange)

    var showOneTimeImagePickingDialog by rememberSaveable {
        mutableStateOf(false)
    }

    PreferenceItemOverload(
        title = title,
        subtitle = subtitle,
        onClick = imagePicker::pickImage,
        onLongClick = {
            showOneTimeImagePickingDialog = true
        },
        autoShadowElevation = autoShadowElevation,
        startIcon = {
            Picture(
                contentScale = contentScale,
                model = value,
                shape = CloverShape,
                modifier = Modifier.size(48.dp),
                error = {
                    Icon(
                        imageVector = Icons.Outlined.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CloverShape)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer
                                    .copy(0.5f)
                                    .compositeOver(color)
                            )
                            .padding(8.dp)
                    )
                }
            )
        },
        endIcon = {
            Icon(
                imageVector = Icons.Rounded.MiniEdit,
                contentDescription = stringResource(R.string.edit)
            )
        },
        modifier = modifier,
        shape = shape,
        color = color,
        drawStartIconContainer = false
    )

    OneTimeImagePickingDialog(
        onDismiss = { showOneTimeImagePickingDialog = false },
        picker = Picker.Single,
        imagePicker = imagePicker,
        visible = showOneTimeImagePickingDialog
    )
}

@Composable
fun FileSelector(
    value: String?,
    onValueChange: (Uri) -> Unit,
    title: String = stringResource(id = R.string.pick_file),
    subtitle: String?,
    modifier: Modifier = Modifier,
    autoShadowElevation: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    shape: Shape = RoundedCornerShape(20.dp)
) {
    val pickFileLauncher = rememberFilePicker(onSuccess = onValueChange)

    PreferenceItemOverload(
        title = title,
        subtitle = if (subtitle == null && value != null) {
            rememberFilename(value.toUri())
        } else subtitle,
        onClick = pickFileLauncher::pickFile,
        autoShadowElevation = autoShadowElevation,
        startIcon = {
            Picture(
                contentScale = ContentScale.Crop,
                model = value,
                shape = CloverShape,
                modifier = Modifier.size(48.dp),
                error = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.InsertDriveFile,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CloverShape)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer
                                    .copy(0.5f)
                                    .compositeOver(color)
                            )
                            .padding(8.dp)
                    )
                }
            )
        },
        endIcon = {
            Icon(
                imageVector = Icons.Rounded.MiniEdit,
                contentDescription = stringResource(R.string.edit)
            )
        },
        modifier = modifier,
        shape = shape,
        color = color,
        drawStartIconContainer = false
    )
}