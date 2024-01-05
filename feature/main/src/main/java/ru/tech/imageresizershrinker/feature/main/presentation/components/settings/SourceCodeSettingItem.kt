package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coredomain.APP_LINK
import ru.tech.imageresizershrinker.coreui.icons.material.Github
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceItem


@Composable
fun SourceCodeSettingItem(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp),
    shape: Shape = RoundedCornerShape(16.dp),
    color: Color = MaterialTheme.colorScheme.primaryContainer.copy(0.7f),
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.9f)
) {
    val context = LocalContext.current
    PreferenceItem(
        contentColor = contentColor,
        shape = shape,
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

