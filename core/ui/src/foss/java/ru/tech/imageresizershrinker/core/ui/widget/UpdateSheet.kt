package ru.tech.imageresizershrinker.core.ui.widget

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ru.tech.imageresizershrinker.core.domain.APP_RELEASES
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleDragHandle
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.HtmlText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@Composable
fun UpdateSheet(
    changelog: String,
    tag: String,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    SimpleSheet(
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        },
        title = {},
        dragHandle = {
            SimpleDragHandle {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CompositionLocalProvider(
                        LocalContentColor.provides(MaterialTheme.colorScheme.onSurface),
                        LocalTextStyle.provides(MaterialTheme.typography.bodyLarge)
                    ) {
                        TitleItem(
                            text = stringResource(R.string.new_version, tag),
                            icon = Icons.Rounded.NewReleases
                        )
                    }
                }
            }
        },
        sheetContent = {
            ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
                Box {
                    Column(
                        modifier = Modifier.align(Alignment.TopCenter),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            HtmlText(
                                html = changelog.trimIndent(),
                                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp)
                            ) { uri ->
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri.toUri()))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("$APP_RELEASES/tag/${tag}")
                        )
                    )
                }
            ) {
                AutoSizeText(stringResource(id = R.string.update))
            }
        }
    )
}