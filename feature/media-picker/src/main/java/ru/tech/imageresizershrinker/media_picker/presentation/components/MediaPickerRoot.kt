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

package ru.tech.imageresizershrinker.media_picker.presentation.components

import android.Manifest
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.twotone.BrokenImage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.t8rin.dynamic.theme.observeAsState
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionUtils.hasPermissionAllowed
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.media_picker.domain.model.AllowedMedia
import ru.tech.imageresizershrinker.media_picker.presentation.MediaPickerActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MediaPickerRoot(
    title: String,
    allowedMedia: AllowedMedia,
    allowMultiple: Boolean,
) {
    val context = LocalContext.current as MediaPickerActivity
    var permissionAllowed by remember {
        mutableStateOf(true)
    }
    var invalidator by remember {
        mutableIntStateOf(0)
    }
    LaunchedEffect(
        LocalLifecycleOwner.current.lifecycle.observeAsState().value,
        invalidator
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.READ_MEDIA_IMAGES
            permissionAllowed = context.hasPermissionAllowed(permission)
            if (!context.hasPermissionAllowed(permission)) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(permission),
                    0
                )
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Marquee {
                        Text(text = title)
                    }
                },
                navigationIcon = {
                    EnhancedIconButton(
                        onClick = context::finish,
                        containerColor = Color.Transparent
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = context.getString(R.string.close)
                        )
                    }
                },
                actions = {
                    TopAppBarEmoji()
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) {
        AnimatedContent(
            targetState = permissionAllowed,
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
                        viewModel = context.viewModel,
                        sendMediaAsResult = context::sendMediaAsResult
                    )
                    LaunchedEffect(Unit) {
                        context.viewModel.init(allowedMedia = allowedMedia)
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
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    EnhancedButton(
                        onClick = {
                            ActivityCompat.requestPermissions(
                                context,
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