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
package ru.tech.imageresizershrinker.feature.media_picker.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.arkivanov.decompose.retainedComponent
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.crash.components.M3Activity
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.emoji.Emoji
import ru.tech.imageresizershrinker.core.settings.presentation.model.toUiState
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeDefaults
import ru.tech.imageresizershrinker.core.ui.theme.ImageToolboxTheme
import ru.tech.imageresizershrinker.core.ui.utils.confetti.ConfettiHost
import ru.tech.imageresizershrinker.core.ui.utils.helper.ColorSchemeName
import ru.tech.imageresizershrinker.core.ui.utils.helper.toClipData
import ru.tech.imageresizershrinker.core.ui.utils.provider.ImageToolboxCompositionLocals
import ru.tech.imageresizershrinker.core.ui.utils.provider.setContentWithWindowSizeClass
import ru.tech.imageresizershrinker.core.ui.widget.other.SecureModeHandler
import ru.tech.imageresizershrinker.feature.media_picker.domain.model.AllowedMedia
import ru.tech.imageresizershrinker.feature.media_picker.presentation.components.MediaPickerRootContent
import ru.tech.imageresizershrinker.feature.media_picker.presentation.screenLogic.MediaPickerComponent
import javax.inject.Inject

@AndroidEntryPoint
class MediaPickerActivity : M3Activity() {

    @Inject
    lateinit var componentFactory: MediaPickerComponent.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val component = retainedComponent(factory = componentFactory::invoke)

        val allowMultiple = intent.getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)

        val title = if (allowMultiple) {
            getString(R.string.pick_multiple_media)
        } else {
            getString(R.string.pick_single_media)
        }
        setContentWithWindowSizeClass {
            ImageToolboxCompositionLocals(
                settingsState = component.settingsState.toUiState(
                    allEmojis = Emoji.allIcons(),
                    allIconShapes = IconShapeDefaults.shapes,
                    getEmojiColorTuple = component::getColorTupleFromEmoji
                )
            ) {
                SecureModeHandler()

                ImageToolboxTheme {
                    val dynamicTheme = LocalDynamicThemeState.current
                    MediaPickerRootContent(
                        component = component,
                        title = title,
                        allowedMedia = intent.type.allowedMedia,
                        allowMultiple = allowMultiple
                    )
                    ConfettiHost()

                    val scope = rememberCoroutineScope()
                    SideEffect {
                        intent.getIntExtra(ColorSchemeName, Color.Transparent.toArgb()).takeIf {
                            it != Color.Transparent.toArgb()
                        }?.let {
                            scope.launch {
                                while (dynamicTheme.colorTuple.value.primary != Color(it)) {
                                    dynamicTheme.updateColorTuple(ColorTuple(Color(it)))
                                    delay(500L)
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    internal fun sendMediaAsResult(selectedMedia: List<Uri>) {
        val newIntent = Intent(
            if (selectedMedia.size == 1) Intent.ACTION_SEND
            else Intent.ACTION_SEND_MULTIPLE
        ).apply {
            if (selectedMedia.size == 1) {
                data = selectedMedia.first()
                clipData = selectedMedia.toClipData()
                putExtra(
                    Intent.EXTRA_STREAM,
                    selectedMedia.first()
                )
            } else {
                clipData = selectedMedia.toClipData()
                putParcelableArrayListExtra(
                    Intent.EXTRA_STREAM,
                    ArrayList(selectedMedia)
                )
            }
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        setResult(RESULT_OK, newIntent)

        finish()
    }

    private val String?.pickImage: Boolean get() = this?.startsWith("image") == true
    private val String?.pickVideo: Boolean get() = this?.startsWith("video") == true
    private val String?.allowedMedia: AllowedMedia
        get() = if (pickImage) AllowedMedia.Photos(this?.takeLastWhile { it != '/' })
        else if (pickVideo) AllowedMedia.Videos
        else AllowedMedia.Both
}