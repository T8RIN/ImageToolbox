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
package ru.tech.imageresizershrinker.media_picker.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import dagger.hilt.android.AndroidEntryPoint
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.settings.presentation.toUiState
import ru.tech.imageresizershrinker.core.ui.icons.emoji.Emoji
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeDefaults
import ru.tech.imageresizershrinker.core.ui.theme.ImageToolboxTheme
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.rememberConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionUtils.hasPermissionAllowed
import ru.tech.imageresizershrinker.core.ui.widget.haptics.customHapticFeedback
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalImageLoader
import ru.tech.imageresizershrinker.media_picker.domain.AllowedMedia
import ru.tech.imageresizershrinker.media_picker.presentation.components.PickerScreen
import ru.tech.imageresizershrinker.media_picker.presentation.viewModel.PickerViewModel

@AndroidEntryPoint
class PickerActivity : ComponentActivity() {

    private val viewModel by viewModels<PickerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val allowMultiple = intent.getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)

        val title = if (allowMultiple) {
            getString(R.string.pick_images)
        } else {
            getString(R.string.pick_image_alt)
        }
        setContent {
            val settingsState = viewModel.settingsState.toUiState(
                allEmojis = Emoji.allIcons(),
                allIconShapes = IconShapeDefaults.shapes,
                getEmojiColorTuple = viewModel::getColorTupleFromEmoji
            )

            val isSecureMode = settingsState.isSecureMode
            LaunchedEffect(isSecureMode) {
                if (isSecureMode) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    window.clearFlags(
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                }
            }

            CompositionLocalProvider(
                LocalSettingsState provides settingsState,
                LocalConfettiHostState provides rememberConfettiHostState(),
                LocalImageLoader provides viewModel.imageLoader,
                LocalHapticFeedback provides customHapticFeedback(settingsState.hapticsStrength)
            ) {
                ImageToolboxTheme {
                    PickerRootScreen(title, intent.type.allowedMedia, allowMultiple)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PickerRootScreen(
        title: String,
        allowedMedia: AllowedMedia,
        allowMultiple: Boolean
    ) {
        val context = LocalContext.current as Activity
        LaunchedEffect(Unit) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permission = Manifest.permission.READ_MEDIA_IMAGES
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
                    title = { Text(text = title) },
                    navigationIcon = {
                        IconButton(onClick = ::finish) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = getString(R.string.close)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(top = it.calculateTopPadding()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PickerScreen(
                    allowedMedia = allowedMedia,
                    allowSelection = allowMultiple,
                    viewModel = viewModel,
                    sendMediaAsResult = ::sendMediaAsResult
                )
                LaunchedEffect(Unit) {
                    viewModel.init(allowedMedia = allowedMedia)
                }
            }
        }
    }

    private fun sendMediaAsResult(selectedMedia: List<Uri>) {
        val newIntent = Intent(
            if (selectedMedia.size == 1) Intent.ACTION_SEND
            else Intent.ACTION_SEND_MULTIPLE
        ).apply {
            if (selectedMedia.size == 1) data = selectedMedia.first()
            else putParcelableArrayListExtra(
                Intent.EXTRA_STREAM,
                ArrayList(selectedMedia)
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        setResult(RESULT_OK, newIntent)

        finish()
    }

    private val String?.pickImage: Boolean get() = this?.startsWith("image") ?: false
    private val String?.pickVideo: Boolean get() = this?.startsWith("video") ?: false
    private val String?.allowedMedia: AllowedMedia
        get() = if (pickImage) AllowedMedia.Photos(this?.takeLastWhile { it != '/' })
        else if (pickVideo) AllowedMedia.Videos
        else AllowedMedia.Both
}