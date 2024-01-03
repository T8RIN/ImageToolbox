package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.CHAT_LINK
import ru.tech.imageresizershrinker.coreui.icons.material.Telegram
import ru.tech.imageresizershrinker.coreui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceRow

@Composable
fun TelegramSettingItem(
    shape: Shape = ContainerShapeDefaults.centerShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val context = LocalContext.current
    PreferenceRow(
        shape = shape,
        applyHorPadding = false,
        onClick = {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(CHAT_LINK)
                )
            )
        },
        startContent = {
            Icon(
                imageVector = Icons.Rounded.Telegram,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 14.dp)
            )
        },
        title = stringResource(R.string.tg_chat),
        subtitle = stringResource(R.string.tg_chat_sub),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
            alpha = 0.9f
        ),
        modifier = modifier
    )
}