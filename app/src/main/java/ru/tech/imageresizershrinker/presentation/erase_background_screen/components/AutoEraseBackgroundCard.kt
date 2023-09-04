@file:Suppress("KotlinConstantConditions")

package ru.tech.imageresizershrinker.presentation.erase_background_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.mixedColor
import ru.tech.imageresizershrinker.presentation.root.theme.onMixedColor
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun AutoEraseBackgroundCard(
    onClick: () -> Unit,
    onReset: () -> Unit
) {
    val settingsState = LocalSettingsState.current
    Column(
        Modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            .block(resultPadding = 8.dp, shape = RoundedCornerShape(24.dp))
    ) {
        if (BuildConfig.FLAVOR != "foss") {
            Row(
                modifier = Modifier
                    .block(
                        applyResultPadding = false,
                        color = MaterialTheme.colorScheme.mixedColor.copy(0.7f)
                    )
                    .clickable { onClick() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(id = R.string.auto_erase_background),
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onMixedColor
                )
                Icon(
                    imageVector = Icons.Rounded.AutoFixHigh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onMixedColor
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        OutlinedButton(
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.mixedColor.copy(0.4f),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            border = BorderStroke(
                settingsState.borderWidth,
                MaterialTheme.colorScheme.outlineVariant(
                    0.1f,
                    onTopOf = MaterialTheme.colorScheme.mixedColor
                )
            ),
            onClick = onReset,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 3.dp)
        ) {
            Icon(Icons.Rounded.SettingsBackupRestore, null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(id = R.string.restore_image))
        }
    }
}