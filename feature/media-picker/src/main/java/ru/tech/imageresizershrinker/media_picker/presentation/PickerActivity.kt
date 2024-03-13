/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.tech.imageresizershrinker.media_picker.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.settings.presentation.toUiState
import ru.tech.imageresizershrinker.core.ui.icons.emoji.Emoji
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeDefaults
import ru.tech.imageresizershrinker.core.ui.theme.ImageToolboxTheme
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.rememberConfettiHostState
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
        WindowCompat.setDecorFitsSystemWindows(window, false)

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
                    PickerRootScreen(title, AllowedMedia.PHOTOS, allowMultiple)
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
                    }
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
        val newIntent = Intent().apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            data = selectedMedia[0]
        }
        if (selectedMedia.size == 1)
            setResult(RESULT_OK, newIntent)
        else {
            newIntent.putParcelableArrayListExtra(
                Intent.EXTRA_STREAM,
                ArrayList(selectedMedia)
            )
            setResult(RESULT_OK, newIntent)
        }
        finish()
    }
}