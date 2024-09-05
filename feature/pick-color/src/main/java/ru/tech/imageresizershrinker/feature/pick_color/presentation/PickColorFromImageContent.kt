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

package ru.tech.imageresizershrinker.feature.pick_color.presentation

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.ImageColorDetector
import com.smarttoolfactory.colordetector.parser.rememberColorParser
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.copyToClipboard
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.helper.toHex
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.PanModeButton
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.modifier.navBarsPaddingOnlyIfTheyAtTheBottom
import ru.tech.imageresizershrinker.core.ui.widget.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.feature.pick_color.presentation.viewModel.PickColorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickColorFromImageContent(
    uriState: Uri?,
    onGoBack: () -> Unit,
    viewModel: PickColorViewModel = hiltViewModel()
) {
    val settingsState = LocalSettingsState.current
    val context = LocalContext.current
    val toastHostState = LocalToastHostState.current
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val parser = rememberColorParser()

    val scope = rememberCoroutineScope()

    var panEnabled by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uriState) {
        uriState?.let {
            viewModel.setUri(it) {
                scope.launch {
                    toastHostState.showError(context, it)
                }
            }
        }
    }

    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let {
            if (allowChangeColor) {
                themeState.updateColorByImage(it)
            }
        }
    }

    LaunchedEffect(viewModel.color) {
        if (!viewModel.color.isUnspecified) {
            if (allowChangeColor) themeState.updateColor(viewModel.color)
        }
    }

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Single)
        ) { uris ->
            uris.takeIf { it.isNotEmpty() }
                ?.firstOrNull()
                ?.let {
                    viewModel.setUri(it) {
                        scope.launch {
                            toastHostState.showError(context, it)
                        }
                    }
                }
        }

    val pickImage = pickImageLauncher::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = uriState != null
    )

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()

    val isPortrait by isPortraitOrientationAsState()

    val switch = @Composable {
        PanModeButton(
            selected = panEnabled,
            onClick = { panEnabled = !panEnabled }
        )
    }

    val magnifierButton = @Composable {
        val settingsInteractor = LocalSimpleSettingsInteractor.current
        EnhancedIconButton(
            containerColor = takeColorFromScheme {
                if (settingsState.magnifierEnabled) {
                    secondary
                } else surfaceContainer
            },
            contentColor = takeColorFromScheme {
                if (settingsState.magnifierEnabled) {
                    onSecondary
                } else onSurface
            },
            enableAutoShadowAndBorder = false,
            onClick = {
                scope.launch {
                    settingsInteractor.toggleMagnifierEnabled()
                }
            },
            modifier = Modifier.statusBarsPadding()
        ) {
            Icon(
                imageVector = Icons.Outlined.ZoomIn,
                contentDescription = stringResource(R.string.magnifier)
            )
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        val color = viewModel.color
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedContent(
                modifier = Modifier.drawHorizontalStroke(),
                targetState = viewModel.bitmap == null,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { noBmp ->
                if (noBmp) {
                    EnhancedTopAppBar(
                        type = EnhancedTopAppBarType.Large,
                        scrollBehavior = scrollBehavior,
                        modifier = Modifier,
                        navigationIcon = {
                            EnhancedIconButton(
                                containerColor = Color.Transparent,
                                contentColor = LocalContentColor.current,
                                enableAutoShadowAndBorder = false,
                                onClick = {
                                    onGoBack()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = stringResource(R.string.exit)
                                )
                            }
                        },
                        title = {
                            Text(
                                text = stringResource(R.string.pick_color),
                                modifier = Modifier.marquee()
                            )
                        },
                        actions = {
                            if (viewModel.bitmap == null) {
                                TopAppBarEmoji()
                            }
                        }
                    )
                } else {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.animateContentSize(),
                    ) {
                        Column {
                            Column(Modifier.navBarsPaddingOnlyIfTheyAtTheEnd()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row {
                                        EnhancedIconButton(
                                            containerColor = Color.Transparent,
                                            contentColor = LocalContentColor.current,
                                            enableAutoShadowAndBorder = false,
                                            onClick = {
                                                onGoBack()
                                            },
                                            modifier = Modifier.statusBarsPadding()
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                                contentDescription = stringResource(R.string.exit)
                                            )
                                        }
                                        if (isPortrait) {
                                            Spacer(modifier = Modifier.weight(1f))
                                            magnifierButton()
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                    }
                                    if (!isPortrait) {
                                        ProvideTextStyle(
                                            value = LocalTextStyle.current.merge(
                                                MaterialTheme.typography.headlineSmall
                                            )
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .padding(
                                                        start = 16.dp,
                                                        end = 16.dp
                                                    )
                                                    .statusBarsPadding()
                                            ) {
                                                Text(stringResource(R.string.color))

                                                Text(
                                                    modifier = Modifier
                                                        .padding(horizontal = 8.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .clickable {
                                                            context.copyToClipboard(
                                                                context.getString(R.string.color),
                                                                color.toHex()
                                                            )
                                                            scope.launch {
                                                                toastHostState.showToast(
                                                                    icon = Icons.Rounded.ContentPaste,
                                                                    message = context.getString(R.string.color_copied)
                                                                )
                                                            }
                                                        }
                                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                                        .border(
                                                            settingsState.borderWidth,
                                                            MaterialTheme.colorScheme.outlineVariant(
                                                                onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                                            ),
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .padding(horizontal = 6.dp),
                                                    text = color.toHex(),
                                                    style = LocalTextStyle.current.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                )

                                                Text(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(2.dp),
                                                    text = remember(color) {
                                                        derivedStateOf {
                                                            parser.parseColorName(color)
                                                        }
                                                    }.value
                                                )

                                                Box(
                                                    Modifier
                                                        .padding(
                                                            vertical = 4.dp,
                                                            horizontal = 16.dp
                                                        )
                                                        .background(
                                                            color = animateColorAsState(color).value,
                                                            shape = RoundedCornerShape(12.dp)
                                                        )
                                                        .height(40.dp)
                                                        .width(72.dp)
                                                        .border(
                                                            width = settingsState.borderWidth,
                                                            color = MaterialTheme.colorScheme.outlineVariant(
                                                                onTopOf = animateColorAsState(color).value
                                                            ),
                                                            shape = RoundedCornerShape(11.dp)
                                                        )
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .clickable {
                                                            context.copyToClipboard(
                                                                context.getString(R.string.color),
                                                                color.toHex()
                                                            )
                                                            scope.launch {
                                                                toastHostState.showToast(
                                                                    icon = Icons.Rounded.ContentPaste,
                                                                    message = context.getString(R.string.color_copied)
                                                                )
                                                            }
                                                        }
                                                )
                                            }
                                        }
                                    }
                                    Spacer(
                                        Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp)
                                    )
                                }
                                if (isPortrait) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    ProvideTextStyle(
                                        value = LocalTextStyle.current.merge(
                                            MaterialTheme.typography.headlineSmall
                                        )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                        ) {
                                            Text(stringResource(R.string.color))

                                            Text(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        context.copyToClipboard(
                                                            context.getString(R.string.color),
                                                            color.toHex()
                                                        )
                                                        scope.launch {
                                                            toastHostState.showToast(
                                                                icon = Icons.Rounded.ContentPaste,
                                                                message = context.getString(R.string.color_copied)
                                                            )
                                                        }
                                                    }
                                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                                    .border(
                                                        settingsState.borderWidth,
                                                        MaterialTheme.colorScheme.outlineVariant(
                                                            onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                                        ),
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(horizontal = 6.dp),
                                                text = color.toHex(),
                                                style = LocalTextStyle.current.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            )

                                            Spacer(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(2.dp)
                                            )

                                            Box(
                                                Modifier
                                                    .padding(vertical = 4.dp)
                                                    .background(
                                                        color = animateColorAsState(color).value,
                                                        shape = RoundedCornerShape(12.dp)
                                                    )
                                                    .height(40.dp)
                                                    .width(72.dp)
                                                    .border(
                                                        width = settingsState.borderWidth,
                                                        color = MaterialTheme.colorScheme.outlineVariant(
                                                            onTopOf = animateColorAsState(color).value
                                                        ),
                                                        shape = RoundedCornerShape(11.dp)
                                                    )
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .clickable {
                                                        context.copyToClipboard(
                                                            context.getString(R.string.color),
                                                            color.toHex()
                                                        )
                                                        scope.launch {
                                                            toastHostState.showToast(
                                                                icon = Icons.Rounded.ContentPaste,
                                                                message = context.getString(R.string.color_copied)
                                                            )
                                                        }
                                                    }
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier.weight(1f)
            ) {
                viewModel.bitmap?.let {
                    if (isPortrait) {
                        AnimatedContent(
                            targetState = it
                        ) { bitmap ->
                            ImageColorDetector(
                                panEnabled = panEnabled,
                                imageBitmap = bitmap.asImageBitmap(),
                                color = viewModel.color,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .navBarsPaddingOnlyIfTheyAtTheBottom()
                                    .container(resultPadding = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .transparencyChecker(),
                                isMagnifierEnabled = settingsState.magnifierEnabled,
                                onColorChange = viewModel::updateColor
                            )
                        }
                    } else {
                        Row {
                            Box(
                                modifier = Modifier.weight(0.8f)
                            ) {
                                Box(Modifier.align(Alignment.Center)) {
                                    AnimatedContent(
                                        targetState = it
                                    ) { bitmap ->
                                        val direction = LocalLayoutDirection.current
                                        ImageColorDetector(
                                            panEnabled = panEnabled,
                                            imageBitmap = bitmap.asImageBitmap(),
                                            color = viewModel.color,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(20.dp)
                                                .navBarsPaddingOnlyIfTheyAtTheBottom()
                                                .padding(
                                                    start = WindowInsets
                                                        .displayCutout
                                                        .asPaddingValues()
                                                        .calculateStartPadding(direction)
                                                )
                                                .container(resultPadding = 8.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .transparencyChecker(),
                                            isMagnifierEnabled = settingsState.magnifierEnabled,
                                            onColorChange = viewModel::updateColor
                                        )
                                    }
                                }
                            }
                            val direction = LocalLayoutDirection.current
                            Column(
                                Modifier
                                    .container(
                                        shape = RectangleShape,
                                        resultPadding = 0.dp
                                    )
                                    .fillMaxHeight()
                                    .padding(horizontal = 20.dp)
                                    .padding(
                                        end = WindowInsets.displayCutout
                                            .asPaddingValues()
                                            .calculateEndPadding(direction)
                                    )
                                    .navigationBarsPadding(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                magnifierButton()
                                Spacer(modifier = Modifier.height(8.dp))
                                switch()
                                Spacer(modifier = Modifier.height(16.dp))
                                EnhancedFloatingActionButton(
                                    onClick = pickImage
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.AddPhotoAlternate,
                                        contentDescription = stringResource(R.string.pick_image_alt)
                                    )
                                }
                            }
                        }
                    }
                } ?: Column(Modifier.verticalScroll(scrollState)) {
                    ImageNotPickedWidget(
                        onPickImage = pickImage,
                        modifier = Modifier
                            .padding(bottom = 88.dp, top = 20.dp, start = 20.dp, end = 20.dp)
                            .navigationBarsPadding()
                    )
                }
            }
            if (viewModel.bitmap != null && isPortrait) {
                BottomAppBar(
                    modifier = Modifier
                        .drawHorizontalStroke(true),
                    actions = {
                        switch()
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .offset(x = (-10).dp),
                            text = remember(color) {
                                derivedStateOf {
                                    parser.parseColorName(color)
                                }
                            }.value,
                            textAlign = TextAlign.Center
                        )
                    },
                    floatingActionButton = {
                        EnhancedFloatingActionButton(
                            onClick = pickImage
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddPhotoAlternate,
                                contentDescription = stringResource(R.string.pick_image_alt)
                            )
                        }
                    }
                )
            }
        }

        if (viewModel.bitmap == null) {
            EnhancedFloatingActionButton(
                onClick = pickImage,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .align(settingsState.fabAlignment)
            ) {
                Spacer(Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Rounded.AddPhotoAlternate,
                    contentDescription = stringResource(R.string.pick_image_alt)
                )
                Spacer(Modifier.width(16.dp))
                Text(stringResource(R.string.pick_image_alt))
                Spacer(Modifier.width(16.dp))
            }
        }
    }

    if (viewModel.isImageLoading) LoadingDialog(canCancel = false)

    BackHandler {
        onGoBack()
    }
}