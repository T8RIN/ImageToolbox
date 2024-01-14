package ru.tech.imageresizershrinker.feature.main.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.core.domain.APP_LINK
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.alertDialogBorder

@Composable
fun GithubReviewDialog(
    onDismiss: () -> Unit,
    showNotShowAgainButton: Boolean,
    onNotShowAgain: () -> Unit
) {
    val linkHandler = LocalUriHandler.current
    AlertDialog(
        modifier = Modifier.alertDialogBorder(),
        onDismissRequest = onDismiss,
        confirmButton = {
            EnhancedButton(
                onClick = {
                    linkHandler.openUri(APP_LINK)
                }
            ) {
                Text(text = stringResource(id = R.string.rate))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    if (showNotShowAgainButton) onNotShowAgain()

                    onDismiss()
                }
            ) {
                Text(stringResource(id = R.string.close))
            }
        },
        title = {
            Text(stringResource(R.string.rate_app))
        },
        icon = {
            Icon(
                imageVector = Icons.Rounded.StarRate,
                contentDescription = null
            )
        },
        text = {
            Text(stringResource(R.string.rate_app_sub))
        }
    )
}