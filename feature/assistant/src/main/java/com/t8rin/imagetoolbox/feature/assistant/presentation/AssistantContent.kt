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

package com.t8rin.imagetoolbox.feature.assistant.presentation

import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gigamole.composefadingedges.FadingEdgesGravity
import com.t8rin.imagetoolbox.core.domain.model.AssistantModel
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Add
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.Close
import com.t8rin.imagetoolbox.core.resources.icons.DeleteSweep
import com.t8rin.imagetoolbox.core.resources.icons.KeyboardArrowDown
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import com.t8rin.imagetoolbox.core.resources.icons.Square
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.DisabledAlpha
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ResetDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButtonType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.text.isKeyboardVisibleAsState
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.assistant.presentation.components.AssistantMessageContent
import com.t8rin.imagetoolbox.feature.assistant.presentation.screenLogic.AssistantComponent
import com.t8rin.neural_tools.assistant.AssistantModels
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch

@Composable
fun AssistantContent(component: AssistantComponent) {
    var input by rememberSaveable { mutableStateOf("") }
    var showModels by rememberSaveable { mutableStateOf(false) }
    var showClearConversation by rememberSaveable { mutableStateOf(false) }
    var followLatest by rememberSaveable { mutableStateOf(true) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val busy = component.isGenerating
    val landscape = LocalScreenSize.current.run { width > height }
    val keyboardVisible by isKeyboardVisibleAsState()
    val modelReady = AssistantModels.isSupported() && component.installations.any {
        it.variant == component.selectedModel && it.installed
    }
    val showSetup = component.enabled && component.messages.isEmpty() &&
            !LocalSettingsState.current.isAiAssistantSetupComplete &&
            component.installations.none { it.installed }
    val hideTopBar = !showSetup && landscape && keyboardVisible
    val window = LocalActivity.current?.window
    DisposableEffect(window) {
        val previousMode = window?.attributes?.softInputMode?.let {
            // Android keeps the current adjustment when restoring an unspecified mode.
            if (it and WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST == 0) {
                it or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
            } else it
        }
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        onDispose {
            previousMode?.let { window.setSoftInputMode(it) }
        }
    }
    val picker = rememberImagePicker(
        picker = if (component.selectedModel.supportsVision) Picker.Single else Picker.Multiple
    ) { uris ->
        if (uris.isNotEmpty()) {
            component.updateSelectedUris(component.selectedUris + uris.map { it.toString() })
        }
    }
    val send: (String) -> Unit = { question ->
        if ((question.isNotBlank() || component.selectedUris.isNotEmpty()) && component.enabled && !busy && !component.isLoading) {
            if (!modelReady) {
                showModels = true
            } else if (component.send(question)) {
                followLatest = true
                input = ""
            }
        }
    }
    LaunchedEffect(listState) {
        listState.interactionSource.interactions.collect {
            if (it is DragInteraction.Start) followLatest = false
        }
    }
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .drop(1)
            .filterNot { it }
            .collect { followLatest = !listState.canScrollForward }
    }
    val viewportHeight by remember {
        derivedStateOf { listState.layoutInfo.viewportSize.height }
    }
    LaunchedEffect(component.messages, component.error, viewportHeight, followLatest) {
        if (followLatest && !listState.isScrollInProgress && component.messages.isNotEmpty()) {
            listState.scrollToItem(component.messages.size)
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (hideTopBar) {
                    Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                } else Modifier
            ),
        contentWindowInsets = WindowInsets.safeDrawing.only(
            if (showSetup) WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
            else WindowInsetsSides.Horizontal
        ),
        topBar = {
            if (!hideTopBar) {
                AssistantTopBar(
                    component = component,
                    onClearConversation = { showClearConversation = true }
                )
            }
        },
        bottomBar = {
            if (!showSetup) {
                AssistantComposer(
                    component = component,
                    input = input,
                    onInputChange = { input = it },
                    onSend = { send(input) },
                    onPickImages = picker::pickImage,
                    onShowModels = { showModels = true },
                    compact = landscape
                )
            }
        }
    ) { padding ->
        AssistantConversation(
            component = component,
            listState = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding),
            modelReady = modelReady,
            showSetup = showSetup,
            onShowModels = { showModels = true },
            onLatest = {
                scope.launch {
                    listState.animateScrollToItem(component.messages.size)
                    followLatest = true
                }
            }
        )
    }
    AssistantModelSheet(
        component = component,
        visible = showModels,
        onDismiss = { showModels = it }
    )
    ResetDialog(
        visible = showClearConversation,
        onDismiss = { showClearConversation = false },
        onReset = {
            component.newConversation()
            followLatest = true
        },
        dismissText = stringResource(R.string.clear),
        title = stringResource(R.string.assistant_clear_conversation),
        text = stringResource(R.string.assistant_clear_conversation_question),
        icon = Icons.Outlined.DeleteSweep
    )
}

@Composable
private fun AssistantTopBar(
    component: AssistantComponent,
    onClearConversation: () -> Unit
) {
    val busy = component.isGenerating
    EnhancedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.marquee()
            ) {
                Text(
                    text = stringResource(R.string.ai_assistant),
                )
                EnhancedBadge(
                    content = {
                        Text(stringResource(R.string.beta))
                    },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .padding(bottom = 12.dp)
                )
            }
        },
        navigationIcon = {
            EnhancedIconButton(onClick = component.onGoBack) {
                Icon(Icons.Rounded.ArrowBack, stringResource(R.string.exit))
            }
        },
        actions = {
            if (component.messages.isNotEmpty()) {
                EnhancedIconButton(
                    onClick = onClearConversation,
                    enabled = !busy && !component.isLoading
                ) {
                    Icon(
                        Icons.Outlined.DeleteSweep,
                        stringResource(R.string.assistant_clear_conversation)
                    )
                }
            }
        }
    )
}

@Composable
private fun AssistantComposer(
    component: AssistantComponent,
    input: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onPickImages: () -> Unit,
    onShowModels: () -> Unit,
    compact: Boolean
) {
    val busy = component.isGenerating
    val currentOnInputChange by rememberUpdatedState(onInputChange)
    val currentOnSend by rememberUpdatedState(onSend)
    val editor = remember(component) {
        movableContentOf<Modifier, String, Boolean> { modifier, value, compact ->
            BasicTextField(
                value = value,
                onValueChange = { currentOnInputChange(it.take(AssistantComponent.MAX_QUERY_LENGTH)) },
                modifier = modifier
                    .padding(
                        horizontal = if (compact) 8.dp else 16.dp,
                        vertical = if (compact) 8.dp else 12.dp
                    )
                    .heightIn(min = 28.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                maxLines = if (compact) 1 else 3,
                enabled = component.enabled && !component.isLoading,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { currentOnSend() })
            ) { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(R.string.assistant_prompt_placeholder),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    innerTextField()
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(
                WindowInsets.navigationBars
                    .union(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                    .union(WindowInsets.ime)
                    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
            )
            .padding(horizontal = 12.dp, vertical = if (compact) 4.dp else 8.dp)
            .container(
                shape = ShapeDefaults.large,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                resultPadding = 0.dp
            )
    ) {
        if (!compact) {
            if (component.selectedUris.isNotEmpty()) {
                AssistantSelection(
                    uris = component.selectedUris,
                    onRemove = { component.updateSelectedUris(component.selectedUris - it) },
                    enabled = !busy,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            editor(Modifier.fillMaxWidth(), input, false)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EnhancedIconButton(
                onClick = onPickImages,
                enabled = component.enabled && !component.isLoading && !busy &&
                        (!component.selectedModel.supportsVision || component.selectedUris.isEmpty())
            ) {
                Icon(Icons.Rounded.Add, stringResource(R.string.add_image))
            }
            if (compact) {
                if (component.selectedUris.isNotEmpty()) {
                    AssistantSelection(
                        uris = component.selectedUris,
                        onRemove = { component.updateSelectedUris(component.selectedUris - it) },
                        enabled = !busy,
                        modifier = Modifier.width(if (component.selectedUris.size == 1) 56.dp else 104.dp),
                        imageSize = 40.dp
                    )
                }
                editor(Modifier.weight(1f), input, true)
            } else {
                Spacer(Modifier.weight(1f))
            }
            EnhancedButton(
                onClick = onShowModels,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(assistantModelTitle(component.selectedModel))
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.models),
                    modifier = Modifier.size(18.dp)
                )
            }
            EnhancedIconButton(
                onClick = { if (busy) component.stop() else currentOnSend() },
                enabled = busy || ((input.isNotBlank() || component.selectedUris.isNotEmpty()) &&
                        component.enabled && !component.isLoading),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(DisabledAlpha),
                disabledContainerColor = MaterialTheme.colorScheme.surface.copy(DisabledAlpha)
            ) {
                Icon(
                    imageVector = if (busy) Icons.Rounded.Square else Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(
                        if (busy) R.string.assistant_stop else R.string.assistant_send
                    ),
                    modifier = if (busy) Modifier.size(18.dp) else Modifier.rotate(90f)
                )
            }
        }
    }
}

@Composable
private fun AssistantSelection(
    uris: List<String>,
    onRemove: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    imageSize: Dp = 72.dp
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        flingBehavior = enhancedFlingBehavior()
    ) {
        items(uris, key = { it }) { uri ->
            Box(Modifier.size(imageSize)) {
                Picture(
                    model = uri,
                    modifier = Modifier.fillMaxSize(),
                    shape = ShapeDefaults.small
                )
                EnhancedIconButton(
                    onClick = { onRemove(uri) },
                    enabled = enabled,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.remove),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AssistantConversation(
    component: AssistantComponent,
    listState: LazyListState,
    modifier: Modifier,
    modelReady: Boolean,
    showSetup: Boolean,
    onShowModels: () -> Unit,
    onLatest: () -> Unit
) {
    val busy = component.isGenerating
    val latestId = component.messages.lastOrNull()?.id
    val selectedModel = component.installations.firstOrNull {
        it.variant == component.selectedModel
    }
    val checkingModel = (!modelReady && component.isCheckingModels) ||
            (selectedModel?.phase == AssistantModels.Phase.Verifying && selectedModel.progress == null)
    val loading = component.isLoading || checkingModel
    val latestButtonHeight = with(LocalDensity.current) { 72.dp.roundToPx() }
    val showLatest by remember(listState, latestButtonHeight) {
        derivedStateOf {
            listState.canScrollForward && listState.layoutInfo.viewportSize.height >= latestButtonHeight
        }
    }
    Box(modifier) {
        if (loading && component.messages.isEmpty()) {
            EnhancedLoadingIndicator(
                Modifier
                    .align(Alignment.Center)
                    .sizeIn(maxWidth = 76.dp, maxHeight = 76.dp)
            )
        } else if (component.messages.isEmpty()) {
            AssistantPlaceholder(
                enabled = component.enabled,
                modelReady = modelReady,
                showSetup = showSetup,
                onShowModels = onShowModels,
                onDisable = component::disable,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .fadingEdges(
                        scrollableState = listState,
                        isVertical = true,
                        gravity = FadingEdgesGravity.End
                    ),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                flingBehavior = enhancedFlingBehavior()
            ) {
                items(
                    items = component.messages,
                    key = { it.id },
                    contentType = { "message" }
                ) { message ->
                    AssistantMessageContent(
                        message = message,
                        component = component,
                        isGenerating = busy && message.id == latestId,
                        canRegenerate = component.enabled && !component.isLoading && !busy &&
                                message.id == latestId
                    )
                }
                item(key = "status", contentType = "status") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        component.error?.let { error ->
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                        if (!component.enabled) {
                            Text(stringResource(R.string.assistant_enabled_required))
                        }
                    }
                }
            }
        }
        if (component.messages.isEmpty()) {
            component.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
        AnimatedVisibility(
            visible = component.messages.isNotEmpty() && showLatest,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
        ) {
            EnhancedFloatingActionButton(
                onClick = onLatest,
                type = EnhancedFloatingActionButtonType.Small,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.assistant_latest_message)
                )
            }
        }
        if (loading && component.messages.isNotEmpty()) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun AssistantPlaceholder(
    enabled: Boolean,
    modelReady: Boolean,
    showSetup: Boolean,
    onShowModels: () -> Unit,
    onDisable: () -> Unit,
    modifier: Modifier
) {
    val supported = AssistantModels.isSupported()
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Neurology,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(
                when {
                    !enabled -> R.string.assistant_enabled_required
                    modelReady -> R.string.assistant_empty_chat
                    !supported -> R.string.assistant_model_unavailable
                    showSetup -> R.string.ai_assistant
                    else -> R.string.assistant_model_not_installed
                }
            ),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (showSetup) {
            if (supported) {
                Text(
                    text = stringResource(R.string.assistant_setup_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier
                    .widthIn(max = 360.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (supported) {
                    EnhancedButton(
                        onClick = onShowModels,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.assistant_download_model))
                    }
                }
                EnhancedButton(
                    onClick = onDisable,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(stringResource(R.string.assistant_disable))
                }
            }
        } else if (enabled && !modelReady) {
            EnhancedButton(onClick = onShowModels) {
                Text(stringResource(R.string.models))
            }
        }
    }
}

@Composable
internal fun assistantModelTitle(model: AssistantModel): String = stringResource(
    when (model) {
        AssistantModel.Advanced -> R.string.assistant_advanced
        AssistantModel.Max -> R.string.assistant_max
        AssistantModel.Lite -> R.string.assistant_lite
        AssistantModel.VisionLite -> R.string.assistant_vision_lite
        AssistantModel.VisionAdvanced -> R.string.assistant_vision_advanced
        AssistantModel.VisionMax -> R.string.assistant_vision_max
    }
)