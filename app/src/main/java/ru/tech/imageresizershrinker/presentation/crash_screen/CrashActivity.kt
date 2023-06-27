package ru.tech.imageresizershrinker.presentation.crash_screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.twotone.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.crash_screen.viewModel.CrashViewModel
import ru.tech.imageresizershrinker.presentation.main_screen.MainActivity
import ru.tech.imageresizershrinker.presentation.theme.ImageResizerTheme
import ru.tech.imageresizershrinker.presentation.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.utils.exception.GlobalExceptionHandler.Companion.getExceptionString
import ru.tech.imageresizershrinker.presentation.widget.other.ToastHost
import ru.tech.imageresizershrinker.presentation.widget.activity.M3Activity
import ru.tech.imageresizershrinker.presentation.widget.other.rememberToastHostState
import ru.tech.imageresizershrinker.presentation.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.widget.utils.isNightMode
import ru.tech.imageresizershrinker.presentation.widget.utils.rememberSettingsState

@AndroidEntryPoint
class CrashActivity : M3Activity() {

    val viewModel by viewModels<CrashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crashReason = getExceptionString()

        setContent {
            val toastHostState = rememberToastHostState()
            val scope = rememberCoroutineScope()

            CompositionLocalProvider(
                LocalSettingsState provides rememberSettingsState(
                    isNightMode = viewModel.nightMode.isNightMode(),
                    isDynamicColors = viewModel.dynamicColors,
                    isAmoledMode = viewModel.amoledMode,
                    appColorTuple = viewModel.appColorTuple,
                    borderWidth = animateFloatAsState(viewModel.borderWidth).value.dp
                )
            ) {
                val settingsState = LocalSettingsState.current
                ImageResizerTheme {
                    val conf = LocalConfiguration.current
                    val size = min(conf.screenWidthDp.dp, conf.screenHeightDp.dp)
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Box {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Icon(
                                    imageVector = Icons.TwoTone.ErrorOutline,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(size * 0.3f)
                                        .statusBarsPadding()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.something_went_wrong),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 26.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedCard(
                                    colors = CardDefaults.cardColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .navigationBarsPadding(),
                                    border = BorderStroke(
                                        settingsState.borderWidth,
                                        MaterialTheme.colorScheme.outlineVariant()
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Text(
                                        text = crashReason,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                            Row(
                                Modifier
                                    .padding(8.dp)
                                    .navigationBarsPadding()
                                    .align(Alignment.BottomCenter)
                            ) {
                                Button(
                                    colors = ButtonDefaults.buttonColors(),
                                    border = BorderStroke(
                                        settingsState.borderWidth,
                                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                                    ),
                                    onClick = {
                                        startActivity(
                                            Intent(
                                                this@CrashActivity,
                                                MainActivity::class.java
                                            )
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.RestartAlt,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AutoSizeText(
                                        text = stringResource(R.string.restart_app),
                                        maxLines = 1
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                OutlinedIconButton(
                                    colors = IconButtonDefaults.filledIconButtonColors(),
                                    border = BorderStroke(
                                        settingsState.borderWidth,
                                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                                    ),
                                    onClick = {
                                        (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).apply {
                                            setPrimaryClip(
                                                ClipData.newPlainText(
                                                    getString(R.string.exception),
                                                    crashReason
                                                )
                                            )
                                        }
                                        scope.launch {
                                            toastHostState.showToast(
                                                icon = Icons.Rounded.ContentCopy,
                                                message = getString(R.string.copied),
                                            )
                                        }
                                    }
                                ) {
                                    Icon(Icons.Rounded.ContentCopy, null)
                                }
                            }
                        }
                    }

                    ToastHost(hostState = toastHostState)
                }
            }
        }
    }

}