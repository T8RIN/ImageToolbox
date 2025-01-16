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

package ru.tech.imageresizershrinker.core.crash

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.exifinterface.media.ExifInterface
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.extractPrimaryColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.crash.components.CrashHandler
import ru.tech.imageresizershrinker.core.domain.ISSUE_TRACKER
import ru.tech.imageresizershrinker.core.domain.TELEGRAM_GROUP_LINK
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.resources.BuildConfig
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.emoji.Emoji
import ru.tech.imageresizershrinker.core.resources.icons.Github
import ru.tech.imageresizershrinker.core.resources.icons.Robot
import ru.tech.imageresizershrinker.core.resources.icons.Telegram
import ru.tech.imageresizershrinker.core.settings.presentation.model.toUiState
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeDefaults
import ru.tech.imageresizershrinker.core.ui.theme.Black
import ru.tech.imageresizershrinker.core.ui.theme.Blue
import ru.tech.imageresizershrinker.core.ui.theme.ImageToolboxThemeSurface
import ru.tech.imageresizershrinker.core.ui.theme.White
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.helper.AppActivityClass
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.copyToClipboard
import ru.tech.imageresizershrinker.core.ui.utils.provider.ImageToolboxCompositionLocals
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalScreenSize
import ru.tech.imageresizershrinker.core.ui.utils.provider.setContentWithWindowSizeClass
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.other.ExpandableItem
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastHost
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import javax.inject.Inject

@AndroidEntryPoint
class CrashActivity : CrashHandler() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crashReason = getCrashReason()
        val exName = crashReason.split("\n\n")[0].trim()
        val ex = crashReason.split("\n\n").drop(1).joinToString("\n\n")

        val title = "[Bug] App Crash: $exName"
        val deviceInfo =
            "Device: ${Build.MODEL} (${Build.BRAND} - ${Build.DEVICE}), SDK: ${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE}), App: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})\n\n"
        val body = "$deviceInfo$ex"

        setContentWithWindowSizeClass {
            ImageToolboxCompositionLocals(
                settingsState = getSettingsState().toUiState(
                    allEmojis = Emoji.allIcons(),
                    allIconShapes = IconShapeDefaults.shapes,
                    onGetEmojiColorTuple = ::getColorTupleFromEmoji
                )
            ) {
                val toastHostState = LocalToastHostState.current
                val scope = rememberCoroutineScope()

                val createClip: (String) -> Unit = {
                    copyToClipboard(
                        label = getString(R.string.exception),
                        value = it
                    )
                    scope.launch {
                        toastHostState.showToast(
                            icon = Icons.Rounded.ContentCopy,
                            message = getString(R.string.copied),
                        )
                    }
                }

                val linkHandler = LocalUriHandler.current

                ImageToolboxThemeSurface {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .verticalScroll(rememberScrollState())
                            .displayCutoutPadding(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Icon(
                            imageVector = Icons.Rounded.Robot,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .statusBarsPadding()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.something_went_wrong_emphasis),
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        val screenWidth = LocalScreenSize.current.width - 32.dp
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            EnhancedButton(
                                onClick = {
                                    linkHandler.openUri(TELEGRAM_GROUP_LINK)
                                    createClip(title + "\n\n" + body)
                                },
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .weight(1f)
                                    .width(screenWidth / 2f)
                                    .height(50.dp),
                                containerColor = Blue,
                                contentColor = White,
                                borderColor = MaterialTheme.colorScheme.outlineVariant(
                                    onTopOf = Blue
                                ),
                                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Telegram,
                                        contentDescription = stringResource(R.string.telegram)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AutoSizeText(
                                        text = stringResource(id = R.string.contact_me),
                                        maxLines = 1
                                    )
                                }
                            }
                            EnhancedButton(
                                onClick = {
                                    linkHandler.openUri("$ISSUE_TRACKER/new?title=$title&body=$body")
                                    createClip(title + "\n\n" + body)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .width(screenWidth / 2f)
                                    .height(50.dp),
                                containerColor = Black,
                                contentColor = White,
                                borderColor = MaterialTheme.colorScheme.outlineVariant(
                                    onTopOf = Black
                                ),
                                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Github,
                                        contentDescription = stringResource(R.string.github)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AutoSizeText(
                                        text = stringResource(id = R.string.create_issue),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        val interactionSource = remember {
                            MutableInteractionSource()
                        }
                        val pressed by interactionSource.collectIsPressedAsState()

                        val cornerSize by animateDpAsState(
                            if (pressed) 8.dp
                            else 24.dp
                        )
                        ExpandableItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .navigationBarsPadding(),
                            shape = RoundedCornerShape(cornerSize),
                            interactionSource = interactionSource,
                            visibleContent = {
                                Icon(
                                    imageVector = Icons.Rounded.BugReport,
                                    contentDescription = null,
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        top = 16.dp,
                                        bottom = 16.dp
                                    )
                                )
                                AutoSizeText(
                                    text = exName,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .weight(1f)
                                )
                            },
                            expandableContent = {
                                AnimatedVisibility(visible = it) {
                                    SelectionContainer {
                                        Text(
                                            text = ex,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                    Row(
                        Modifier
                            .padding(8.dp)
                            .navigationBarsPadding()
                            .displayCutoutPadding()
                            .align(Alignment.BottomCenter)
                    ) {
                        EnhancedFloatingActionButton(
                            modifier = Modifier
                                .weight(1f, false),
                            onClick = {
                                startActivity(
                                    Intent(
                                        this@CrashActivity,
                                        AppActivityClass
                                    )
                                )
                            },
                            content = {
                                Spacer(Modifier.width(16.dp))
                                Icon(
                                    imageVector = Icons.Rounded.RestartAlt,
                                    contentDescription = stringResource(R.string.restart_app)
                                )
                                Spacer(Modifier.width(16.dp))
                                AutoSizeText(
                                    text = stringResource(R.string.restart_app),
                                    maxLines = 1
                                )
                                Spacer(Modifier.width(16.dp))
                            }
                        )
                        Spacer(Modifier.width(8.dp))
                        EnhancedFloatingActionButton(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            onClick = {
                                createClip(title + "\n\n" + body)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ContentCopy,
                                contentDescription = stringResource(R.string.copy)
                            )
                        }
                    }

                    ToastHost()
                }
            }
        }
    }

    @Inject
    lateinit var imageGetter: ImageGetter<Bitmap, ExifInterface>

    private suspend fun getColorTupleFromEmoji(
        emojiUri: String
    ): ColorTuple? = imageGetter
        .getImage(data = emojiUri)
        ?.extractPrimaryColor()
        ?.let { ColorTuple(it) }

}
