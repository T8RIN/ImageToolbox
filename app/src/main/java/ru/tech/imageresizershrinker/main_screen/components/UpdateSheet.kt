package ru.tech.imageresizershrinker.main_screen.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.common.APP_RELEASES
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.widget.TitleItem
import ru.tech.imageresizershrinker.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.widget.text.HtmlText
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState

@Composable
fun UpdateSheet(changelog: String, tag: String, visible: MutableState<Boolean>) {
    val settingsState = LocalSettingsState.current
    val context = LocalContext.current

    SimpleSheet(
        endConfirmButtonPadding = 0.dp,
        visible = visible,
        title = {},
        sheetContent = {
            ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
                Box {
                    Column(
                        modifier = Modifier.align(Alignment.TopCenter),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor.provides(MaterialTheme.colorScheme.onSurface),
                            LocalTextStyle.provides(MaterialTheme.typography.bodyLarge)
                        ) {
                            TitleItem(
                                text = stringResource(R.string.new_version, tag),
                                icon = Icons.Rounded.Download
                            )
                        }
                        Divider()
                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            HtmlText(
                                nightMode = settingsState.isNightMode,
                                html = changelog,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    Divider(Modifier.align(Alignment.BottomCenter))
                }
            }
        },
        confirmButton = {
            OutlinedButton(
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                border = BorderStroke(
                    settingsState.borderWidth,
                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                ),
                onClick = {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("$APP_RELEASES/tag/${tag}")
                        )
                    )
                }
            ) {
                Text(stringResource(id = R.string.update))
            }
        }
    )
}