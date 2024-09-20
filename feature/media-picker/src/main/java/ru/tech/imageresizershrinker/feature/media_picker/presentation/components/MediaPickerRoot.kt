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

package ru.tech.imageresizershrinker.feature.media_picker.presentation.components

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.twotone.BrokenImage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.t8rin.dynamic.theme.observeAsState
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionUtils.hasPermissionAllowed
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.AllowedMedia
import ru.tech.imageresizershrinker.feature.media_picker.presentation.MediaPickerActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MediaPickerActivity.MediaPickerRoot(
    title: String,
    allowedMedia: AllowedMedia,
    allowMultiple: Boolean,
) {
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
            launcher.launch(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
        }
    }

    val lifecycleEvent by LocalLifecycleOwner.current.lifecycle.observeAsState()
    LaunchedEffect(
        lifecycleEvent,
        invalidator
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.READ_MEDIA_IMAGES
            isPermissionAllowed = hasPermissionAllowed(permission)
            isManagePermissionAllowed =
                Environment.isExternalStorageManager() || isInstalledFromPlayStore()
            if (!hasPermissionAllowed(permission)) {
                ActivityCompat.requestPermissions(
                    this@MediaPickerRoot,
                    arrayOf(permission),
                    0
                )
            } else {
                viewModel.init(allowedMedia)
            }
        }
    }
    Scaffold(
        topBar = {
            EnhancedTopAppBar(
                title = {
                    Text(
                        text = title,
                        modifier = Modifier.marquee()
                    )
                },
                navigationIcon = {
                    EnhancedIconButton(
                        onClick = ::finish,
                        containerColor = Color.Transparent
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.close)
                        )
                    }
                },
                actions = {
                    TopAppBarEmoji()
                },
                modifier = if (viewModel.albumsState.collectAsState().value.albums.size <= 1) {
                    Modifier.drawHorizontalStroke()
                } else Modifier
            )
        }
    ) {
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
                    MediaPickerScreen(
                        allowedMedia = allowedMedia,
                        allowSelection = allowMultiple,
                        viewModel = viewModel,
                        sendMediaAsResult = ::sendMediaAsResult,
                        isManagePermissionAllowed = isManagePermissionAllowed,
                        onRequestManagePermission = requestManagePermission
                    )
                    LaunchedEffect(Unit) {
                        viewModel.init(allowedMedia = allowedMedia)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.BrokenImage,
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
                            ActivityCompat.requestPermissions(
                                this@MediaPickerRoot,
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                                } else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                0
                            )
                            invalidator++
                        }
                    ) {
                        Text(stringResource(id = R.string.request))
                    }
                }
            }
        }
    }
}