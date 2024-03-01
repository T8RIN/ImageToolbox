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

package ru.tech.imageresizershrinker.feature.cipher.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.InsertDriveFile
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.twotone.FileOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.utils.readableByteCount
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.icons.material.ShieldKey
import ru.tech.imageresizershrinker.core.ui.icons.material.ShieldOpen
import ru.tech.imageresizershrinker.core.ui.shapes.CloverShape
import ru.tech.imageresizershrinker.core.ui.theme.Green
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getFilename
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.fileSize
import ru.tech.imageresizershrinker.core.ui.utils.helper.ReviewHandler.showReview
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.modifier.materialShadow
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.utils.isScrollingUp
import ru.tech.imageresizershrinker.feature.cipher.presentation.components.CipherTipSheet
import ru.tech.imageresizershrinker.feature.cipher.presentation.viewModel.FileCipherViewModel
import java.io.DataInputStream
import java.io.InputStream
import java.security.InvalidKeyException
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileCipherScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    viewModel: FileCipherViewModel = hiltViewModel()
) {
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(uriState) {
        uriState?.let { viewModel.setUri(it) }
    }

    val context = LocalContext.current
    val settingsState = LocalSettingsState.current
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()
    val confettiController = LocalConfettiController.current

    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    val showTip = rememberSaveable { mutableStateOf(false) }

    var key by rememberSaveable { mutableStateOf("") }

    val onBack = {
        if (viewModel.uri != null && (key.isNotEmpty() || viewModel.byteArray != null)) showExitDialog =
            true
        else onGoBack()
    }

    val saveLauncher = rememberLauncherForActivityResult(
        contract = CreateDocument(),
        onResult = {
            it?.let { uri ->
                viewModel.saveCryptographyTo(
                    outputStream = context.contentResolver.openOutputStream(uri, "rw")
                ) { t ->
                    if (t != null) {
                        scope.launch {
                            toastHostState.showError(context, t)
                        }
                    } else {
                        scope.launch {
                            confettiController.showEmpty()
                        }
                        scope.launch {
                            toastHostState.showToast(
                                context.getString(
                                    R.string.saved_to_without_filename,
                                    ""
                                ),
                                Icons.Rounded.Save
                            )
                            showReview(context)
                        }
                    }
                }
            }
        }
    )

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                viewModel.setUri(it)
            }
        }
    )

    val focus = LocalFocusManager.current

    val state = rememberLazyListState()
    Box {

        Surface(
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focus.clearFocus()
                    }
                )
            },
            color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Box(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                Column(Modifier.fillMaxSize()) {
                    LargeTopAppBar(
                        scrollBehavior = scrollBehavior,
                        modifier = Modifier.drawHorizontalStroke(),
                        title = {
                            Marquee(
                                edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                            ) {
                                AnimatedContent(
                                    targetState = viewModel.uri to viewModel.isEncrypt,
                                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                                ) { (uri, isEncrypt) ->
                                    Text(
                                        if (uri == null) {
                                            stringResource(R.string.cipher)
                                        } else {
                                            listOf(
                                                stringResource(R.string.encryption),
                                                stringResource(R.string.decryption)
                                            )[if (isEncrypt) 0 else 1]
                                        },
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                3.dp
                            )
                        ),
                        navigationIcon = {
                            EnhancedIconButton(
                                containerColor = Color.Transparent,
                                contentColor = LocalContentColor.current,
                                enableAutoShadowAndBorder = false,
                                onClick = onGoBack
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                            }
                        },
                        actions = {
                            TopAppBarEmoji()
                        }
                    )
                    val cutout = if (viewModel.uri != null) {
                        WindowInsets
                            .displayCutout
                            .asPaddingValues()
                    } else PaddingValues()
                    val direction = LocalLayoutDirection.current
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        state = state,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(
                            bottom = 88.dp + WindowInsets
                                .navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding() + WindowInsets
                                .ime
                                .asPaddingValues()
                                .calculateBottomPadding(),
                            top = 20.dp,
                            end = 20.dp + cutout.calculateRightPadding(direction),
                            start = 20.dp + cutout.calculateLeftPadding(direction)
                        )
                    ) {
                        item {
                            AnimatedContent(targetState = viewModel.uri != null) { hasUri ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    if (!hasUri) {
                                        Column(
                                            modifier = Modifier.container(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(Modifier.height(16.dp))
                                            Icon(
                                                imageVector = Icons.TwoTone.FileOpen,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .container(
                                                        shape = CloverShape,
                                                        resultPadding = 0.dp,
                                                        color = MaterialTheme.colorScheme.secondaryContainer
                                                    )
                                                    .clickable {
                                                        haptics.performHapticFeedback(
                                                            HapticFeedbackType.LongPress
                                                        )
                                                        runCatching {
                                                            filePicker.launch(arrayOf("*/*"))
                                                        }.onFailure {
                                                            scope.launch {
                                                                toastHostState.showToast(
                                                                    message = context.getString(R.string.activate_files),
                                                                    icon = Icons.Outlined.FolderOff,
                                                                    duration = ToastDuration.Long
                                                                )
                                                            }
                                                        }
                                                    }
                                                    .padding(12.dp),
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                            Text(
                                                text = stringResource(R.string.pick_file_to_start),
                                                modifier = Modifier.padding(16.dp),
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier
                                                .container(MaterialTheme.shapes.extraLarge)
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val items = listOf(
                                                stringResource(R.string.encryption),
                                                stringResource(R.string.decryption)
                                            )
                                            SingleChoiceSegmentedButtonRow(
                                                space = max(settingsState.borderWidth, 1.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(end = 8.dp, start = 2.dp)
                                            ) {
                                                CompositionLocalProvider(
                                                    LocalMinimumInteractiveComponentEnforcement provides false
                                                ) {
                                                    items.forEachIndexed { index, item ->
                                                        val shape =
                                                            SegmentedButtonDefaults.itemShape(
                                                                index,
                                                                items.size
                                                            )
                                                        val selected =
                                                            index == if (viewModel.isEncrypt) 0 else 1
                                                        SegmentedButton(
                                                            onClick = {
                                                                haptics.performHapticFeedback(
                                                                    HapticFeedbackType.LongPress
                                                                )
                                                                viewModel.setIsEncrypt(index == 0)
                                                            },
                                                            border = BorderStroke(
                                                                width = settingsState.borderWidth,
                                                                color = MaterialTheme.colorScheme.outlineVariant()
                                                            ),
                                                            selected = selected,
                                                            colors = SegmentedButtonDefaults.colors(
                                                                activeBorderColor = MaterialTheme.colorScheme.outlineVariant(),
                                                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                                    6.dp
                                                                )
                                                            ),
                                                            modifier = Modifier.materialShadow(
                                                                shape = shape,
                                                                elevation = animateDpAsState(
                                                                    if (settingsState.borderWidth >= 0.dp || !settingsState.drawButtonShadows) 0.dp
                                                                    else if (selected) 2.dp
                                                                    else 1.dp
                                                                ).value
                                                            ),
                                                            shape = shape
                                                        ) {
                                                            Text(text = item, fontSize = 12.sp)
                                                        }
                                                    }
                                                }
                                            }
                                            EnhancedIconButton(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                onClick = {
                                                    showTip.value = true
                                                }
                                            ) {
                                                Icon(Icons.AutoMirrored.Rounded.HelpOutline, null)
                                            }
                                        }

                                        viewModel.uri?.let { uri ->
                                            PreferenceItem(
                                                modifier = Modifier.padding(top = 16.dp),
                                                title = context.getFilename(uri)
                                                    ?: stringResource(R.string.something_went_wrong),
                                                onClick = null,
                                                titleFontStyle = LocalTextStyle.current.copy(
                                                    lineHeight = 16.sp,
                                                    fontSize = 15.sp
                                                ),
                                                subtitle = viewModel.uri?.let {
                                                    stringResource(
                                                        id = R.string.size,
                                                        readableByteCount(
                                                            it.fileSize(context) ?: 0L
                                                        )
                                                    )
                                                },
                                                startIcon = Icons.AutoMirrored.Rounded.InsertDriveFile
                                            )

                                            EnhancedButton(
                                                onClick = {
                                                    runCatching {
                                                        filePicker.launch(arrayOf("*/*"))
                                                    }.onFailure {
                                                        scope.launch {
                                                            toastHostState.showToast(
                                                                message = context.getString(R.string.activate_files),
                                                                icon = Icons.Outlined.FolderOff,
                                                                duration = ToastDuration.Long
                                                            )
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.padding(top = 16.dp),
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                borderColor = MaterialTheme.colorScheme.outlineVariant(
                                                    onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                                ),
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(Icons.Rounded.FolderOpen, null)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(text = stringResource(id = R.string.pick_file))
                                                }
                                            }

                                            RoundedTextField(
                                                modifier = Modifier
                                                    .padding(top = 16.dp)
                                                    .container(shape = RoundedCornerShape(24.dp))
                                                    .padding(8.dp),
                                                value = key,
                                                startIcon = {
                                                    EnhancedIconButton(
                                                        containerColor = Color.Transparent,
                                                        contentColor = LocalContentColor.current,
                                                        enableAutoShadowAndBorder = false,
                                                        onClick = {
                                                            key = viewModel.generateRandomPassword()
                                                            viewModel.resetCalculatedData()
                                                        },
                                                        modifier = Modifier.padding(start = 4.dp)
                                                    ) {
                                                        Icon(
                                                            Icons.Rounded.Shuffle,
                                                            null,
                                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                                        )
                                                    }
                                                },
                                                endIcon = {
                                                    EnhancedIconButton(
                                                        containerColor = Color.Transparent,
                                                        contentColor = LocalContentColor.current,
                                                        enableAutoShadowAndBorder = false,
                                                        onClick = {
                                                            key = ""
                                                            viewModel.resetCalculatedData()
                                                        },
                                                        modifier = Modifier.padding(end = 4.dp)
                                                    ) {
                                                        Icon(
                                                            Icons.Outlined.Cancel,
                                                            null,
                                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                                        )
                                                    }
                                                },
                                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                                singleLine = false,
                                                onValueChange = {
                                                    key = it
                                                    viewModel.resetCalculatedData()
                                                },
                                                label = {
                                                    Text(stringResource(R.string.key))
                                                }
                                            )
                                        }

                                        EnhancedButton(
                                            enabled = key.isNotEmpty(),
                                            onClick = {
                                                viewModel.startCryptography(
                                                    key = key,
                                                    onFileRequest = { uri ->
                                                        context
                                                            .contentResolver
                                                            .openInputStream(uri)
                                                            ?.use { it.toByteArray() }
                                                    }
                                                ) {
                                                    if (it is InvalidKeyException) {
                                                        scope.launch {
                                                            toastHostState.showToast(
                                                                context.getString(R.string.invalid_password_or_not_encrypted),
                                                                Icons.Rounded.ErrorOutline
                                                            )
                                                        }
                                                    } else if (it != null) {
                                                        scope.launch {
                                                            toastHostState.showError(context, it)
                                                        }
                                                    }
                                                }
                                            },
                                            modifier = Modifier
                                                .padding(top = 16.dp)
                                                .fillMaxWidth()
                                                .height(56.dp),
                                            borderColor = MaterialTheme.colorScheme.outlineVariant(
                                                onTopOf = if (key.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Black
                                            )
                                        ) {
                                            AnimatedContent(
                                                targetState = viewModel.uri to viewModel.isEncrypt,
                                                transitionSpec = { fadeIn() togetherWith fadeOut() }
                                            ) { (uri, isEncrypt) ->
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 16.dp)
                                                ) {
                                                    when {
                                                        uri == null -> {
                                                            Icon(Icons.Rounded.FileOpen, null)
                                                        }

                                                        isEncrypt -> {
                                                            Icon(Icons.Rounded.ShieldKey, null)
                                                        }

                                                        else -> {
                                                            Icon(Icons.Rounded.ShieldOpen, null)
                                                        }
                                                    }
                                                    Spacer(Modifier.width(8.dp))
                                                    when {
                                                        uri == null -> {
                                                            Text(
                                                                text = stringResource(R.string.pick_file),
                                                                fontWeight = FontWeight.SemiBold,
                                                                fontSize = 16.sp
                                                            )
                                                        }

                                                        isEncrypt -> {
                                                            Text(
                                                                text = stringResource(R.string.encrypt),
                                                                fontWeight = FontWeight.SemiBold,
                                                                fontSize = 16.sp
                                                            )
                                                        }

                                                        else -> {
                                                            Text(
                                                                text = stringResource(R.string.decrypt),
                                                                fontWeight = FontWeight.SemiBold,
                                                                fontSize = 16.sp
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        AnimatedVisibility(visible = viewModel.byteArray != null) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 24.dp)
                                                    .container(
                                                        shape = MaterialTheme.shapes.extraLarge,
                                                        color = MaterialTheme
                                                            .colorScheme
                                                            .surfaceColorAtElevation(10.dp),
                                                        resultPadding = 0.dp
                                                    )
                                                    .padding(16.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        Icons.Rounded.CheckCircle,
                                                        null,
                                                        tint = Green,
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .background(
                                                                color = MaterialTheme.colorScheme.surface,
                                                                shape = CircleShape
                                                            )
                                                            .border(
                                                                width = settingsState.borderWidth,
                                                                color = MaterialTheme.colorScheme.outlineVariant(),
                                                                shape = CircleShape
                                                            )
                                                            .padding(4.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(16.dp))
                                                    Text(
                                                        stringResource(R.string.file_proceed),
                                                        fontSize = 17.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                                Text(
                                                    text = stringResource(R.string.store_file_desc),
                                                    fontSize = 13.sp,
                                                    color = LocalContentColor.current.copy(alpha = 0.7f),
                                                    lineHeight = 14.sp,
                                                    modifier = Modifier.padding(vertical = 16.dp)
                                                )
                                                var name by rememberSaveable(viewModel.byteArray) {
                                                    mutableStateOf(
                                                        if (viewModel.isEncrypt) {
                                                            "enc-"
                                                        } else {
                                                            "dec-"
                                                        } + (viewModel.uri?.let {
                                                            context.getFilename(it)
                                                        } ?: Random.nextInt())
                                                    )
                                                }
                                                RoundedTextField(
                                                    modifier = Modifier
                                                        .padding(top = 8.dp)
                                                        .container(shape = RoundedCornerShape(24.dp))
                                                        .padding(8.dp),
                                                    value = name,
                                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                                    singleLine = false,
                                                    onValueChange = { name = it },
                                                    label = {
                                                        Text(stringResource(R.string.filename))
                                                    }
                                                )

                                                Row(
                                                    modifier = Modifier
                                                        .padding(top = 24.dp)
                                                        .fillMaxWidth()
                                                ) {
                                                    EnhancedButton(
                                                        onClick = {
                                                            runCatching {
                                                                saveLauncher.launch("*/*#$name")
                                                            }.onFailure {
                                                                scope.launch {
                                                                    toastHostState.showToast(
                                                                        message = context.getString(
                                                                            R.string.activate_files
                                                                        ),
                                                                        icon = Icons.Outlined.FolderOff,
                                                                        duration = ToastDuration.Long
                                                                    )
                                                                }
                                                            }
                                                        },
                                                        modifier = Modifier
                                                            .padding(end = 8.dp)
                                                            .fillMaxWidth(0.5f)
                                                            .height(50.dp),
                                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.Center
                                                        ) {
                                                            Icon(
                                                                Icons.Rounded.FileDownload,
                                                                null
                                                            )
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            AutoSizeText(
                                                                text = stringResource(id = R.string.save),
                                                                maxLines = 1
                                                            )
                                                        }
                                                    }
                                                    EnhancedButton(
                                                        onClick = {
                                                            viewModel.byteArray?.let {
                                                                viewModel.shareFile(
                                                                    it = it,
                                                                    filename = name
                                                                ) {
                                                                    scope.launch {
                                                                        confettiController.showEmpty()
                                                                    }
                                                                }
                                                            }
                                                        },
                                                        modifier = Modifier
                                                            .padding(start = 8.dp)
                                                            .fillMaxWidth()
                                                            .height(50.dp),
                                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.Center
                                                        ) {
                                                            Icon(Icons.Rounded.Share, null)
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            AutoSizeText(
                                                                text = stringResource(id = R.string.share),
                                                                maxLines = 1
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (viewModel.isSaving) LoadingDialog {
                    viewModel.cancelSaving()
                }
            }
        }


        if (viewModel.uri == null) {
            EnhancedFloatingActionButton(
                onClick = {
                    runCatching {
                        filePicker.launch(arrayOf("*/*"))
                    }.onFailure {
                        scope.launch {
                            toastHostState.showToast(
                                message = context.getString(R.string.activate_files),
                                icon = Icons.Outlined.FolderOff,
                                duration = ToastDuration.Long
                            )
                        }
                    }
                },
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(12.dp)
                    .align(settingsState.fabAlignment)
            ) {
                val expanded =
                    if (settingsState.fabAlignment != Alignment.BottomCenter) state.isScrollingUp() else true
                val horizontalPadding by animateDpAsState(targetValue = if (expanded) 16.dp else 0.dp)

                Row(
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Rounded.FileOpen, contentDescription = null)
                    AnimatedVisibility(visible = expanded) {
                        Row {
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.pick_file))
                        }
                    }
                }
            }
        }
    }

    ExitWithoutSavingDialog(
        onExit = onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    CipherTipSheet(visible = showTip)

    BackHandler(onBack = onBack)

}

private class CreateDocument : ActivityResultContracts.CreateDocument("*/*") {
    override fun createIntent(
        context: Context,
        input: String
    ): Intent {
        return super.createIntent(
            context = context,
            input = input.split("#")[0]
        ).putExtra(Intent.EXTRA_TITLE, input.split("#")[1])
    }
}

private fun InputStream.toByteArray(): ByteArray {
    val bytes = ByteArray(this.available())
    val dis = DataInputStream(this)
    dis.readFully(bytes)
    return bytes
}