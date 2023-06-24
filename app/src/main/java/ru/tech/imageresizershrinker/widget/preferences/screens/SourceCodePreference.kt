package ru.tech.imageresizershrinker.widget.preferences.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.common.APP_LINK
import ru.tech.imageresizershrinker.theme.icons.Github
import ru.tech.imageresizershrinker.widget.preferences.PreferenceItem


@Composable
fun SourceCodePreference(
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp),
    color: Color = MaterialTheme.colorScheme.primaryContainer
) {
    val context = LocalContext.current
    PreferenceItem(
        onClick = {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(APP_LINK)
                )
            )
        },
        icon = Icons.Rounded.Github,
        title = stringResource(R.string.check_source_code),
        subtitle = stringResource(R.string.check_source_code_sub),
        color = color,
        modifier = modifier
    )
}

