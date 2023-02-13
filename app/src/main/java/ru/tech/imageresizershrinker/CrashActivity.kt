package ru.tech.imageresizershrinker

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.twotone.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.GlobalExceptionHandler.Companion.getExceptionString

class CrashActivity : ComponentActivity() {

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val crashReason = getExceptionString()

        setContent {
            val toastHostState = rememberToastHostState()
            val scope = rememberCoroutineScope()

            ImageResizerShrinkerTheme() {
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
                                text = "Something went wrong",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 26.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .navigationBarsPadding(),
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
                                .padding(bottom = 16.dp)
                                .navigationBarsPadding()
                                .align(Alignment.BottomCenter)
                        ) {
                            Button(
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
                                Text(text = "Restart app")
                            }
                            Spacer(Modifier.width(8.dp))
                            FilledIconButton(onClick = {
                                (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).apply {
                                    setPrimaryClip(
                                        ClipData.newPlainText(
                                            "Exception",
                                            crashReason
                                        )
                                    )
                                }
                                scope.launch {
                                    toastHostState.showToast(
                                        icon = Icons.Rounded.ContentCopy,
                                        message = "Copied to clipboard",
                                    )
                                }
                            }) {
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