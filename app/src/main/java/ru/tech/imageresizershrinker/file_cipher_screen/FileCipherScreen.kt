package ru.tech.imageresizershrinker.file_cipher_screen

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.twotone.FileOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.file_cipher_screen.viewModel.FileCipherViewModel
import ru.tech.imageresizershrinker.theme.icons.ShieldKey
import ru.tech.imageresizershrinker.theme.icons.ShieldOpen
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.modifier.block
import ru.tech.imageresizershrinker.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.utils.storage.LocalFileController
import ru.tech.imageresizershrinker.widget.LoadingDialog
import ru.tech.imageresizershrinker.widget.TopAppBarEmoji
import ru.tech.imageresizershrinker.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.widget.text.Marquee
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.widget.utils.isScrollingUp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileCipherScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    viewModel: FileCipherViewModel = viewModel()
) {
    val context = LocalContext.current
    val fileController = LocalFileController.current
    val settingsState = LocalSettingsState.current

    val saveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*"),
        onResult = {

        }
    )

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                viewModel.setUri(it)
            }
        }
    )

    val focus = LocalFocusManager.current
    var showSaveLoading by rememberSaveable { mutableStateOf(false) }

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
                                Text(
                                    stringResource(R.string.cipher),
                                    textAlign = TextAlign.Center
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                3.dp
                            )
                        ),
                        navigationIcon = {
                            IconButton(onClick = onGoBack) {
                                Icon(Icons.Rounded.ArrowBack, null)
                            }
                        },
                        actions = {
                            TopAppBarEmoji()
                        }
                    )
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
                                .calculateBottomPadding(),
                            top = 20.dp,
                            end = 20.dp,
                            start = 20.dp
                        )
                    ) {
                        item {
                            if (viewModel.uri == null) {
                                Column(
                                    modifier = Modifier.block(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Spacer(Modifier.height(16.dp))
                                    FilledIconButton(
                                        onClick = { filePicker.launch("*/*") },
                                        modifier = Modifier.size(100.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                6.dp
                                            ),
                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    ) {
                                        Icon(
                                            Icons.TwoTone.FileOpen,
                                            null,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .border(
                                                    settingsState.borderWidth,
                                                    MaterialTheme.colorScheme.outlineVariant(0.2f),
                                                    RoundedCornerShape(16.dp)
                                                )
                                                .padding(12.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        stringResource(R.string.pick_file_to_start),
                                        Modifier.padding(16.dp),
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                Row(Modifier.block()) {
                                    ToggleGroupButton(
                                        enabled = true,
                                        items = listOf(
                                            stringResource(R.string.encryption),
                                            stringResource(R.string.decryption)
                                        ),
                                        title = null,
                                        selectedIndex = if (viewModel.isEncrypt) 0 else 1,
                                        indexChanged = {
                                            viewModel.setIsEncrypt(it == 0)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {

                                        }
                                    ) {
                                        Icon(Icons.Rounded.HelpOutline, null)
                                    }
                                }
                            }
                        }
                    }
                }


                if (showSaveLoading) LoadingDialog()

                BackHandler { onGoBack() }
            }
        }


        FloatingActionButton(
            onClick = {
                if (viewModel.uri == null) {
                    filePicker.launch("*/*")
                } else viewModel.startCryptography {

                }
            },
            modifier = Modifier
                .navigationBarsPadding()
                .padding(12.dp)
                .align(settingsState.fabAlignment)
                .fabBorder(),
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        ) {
            val expanded =
                if (settingsState.fabAlignment != Alignment.BottomCenter) state.isScrollingUp() else true
            val horizontalPadding by animateDpAsState(targetValue = if (expanded) 16.dp else 0.dp)

            AnimatedContent(viewModel.uri to viewModel.isEncrypt) { (uri, isEncrypt) ->
                Row(
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    verticalAlignment = Alignment.CenterVertically
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
                    AnimatedVisibility(visible = expanded) {
                        Row {
                            Spacer(Modifier.width(8.dp))
                            when {
                                uri == null -> {
                                    Text(stringResource(R.string.pick_file))
                                }

                                isEncrypt -> {
                                    Text(stringResource(R.string.encrypt))
                                }

                                else -> {
                                    Text(stringResource(R.string.decrypt))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}