package ru.tech.imageresizershrinker.feature.pdf_tools.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedAlertDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField

@Composable
internal fun PdfPasswordRequestDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onFillPassword: (String) -> Unit
) {
    var password by remember(isVisible) {
        mutableStateOf("")
    }
    var hidePassword by remember(isVisible) {
        mutableStateOf(true)
    }

    EnhancedAlertDialog(
        visible = isVisible,
        onDismissRequest = {},
        icon = {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(R.string.password))
        },
        text = {
            RoundedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center
                ),
                label = null,
                modifier = Modifier
                    .container(
                        shape = MaterialTheme.shapes.large,
                        resultPadding = 8.dp
                    ),
                singleLine = true,
                visualTransformation = if (hidePassword) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                hint = {
                    Text(
                        text = stringResource(R.string.pdf_is_protected),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                startIcon = {
                    Icon(
                        imageVector = Icons.Filled.Password,
                        contentDescription = null
                    )
                },
                endIcon = {
                    EnhancedIconButton(
                        onClick = { hidePassword = !hidePassword }
                    ) {
                        Icon(
                            imageVector = if (hidePassword) {
                                Icons.Outlined.VisibilityOff
                            } else {
                                Icons.Outlined.Visibility
                            },
                            contentDescription = null
                        )
                    }
                }
            )
        },
        confirmButton = {
            EnhancedButton(
                enabled = password.isNotEmpty(),
                onClick = { onFillPassword(password) }
            ) {
                Text(stringResource(R.string.unlock))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.close))
            }
        }
    )
}