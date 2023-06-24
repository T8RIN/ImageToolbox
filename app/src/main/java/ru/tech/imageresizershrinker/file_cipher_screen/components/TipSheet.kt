package ru.tech.imageresizershrinker.file_cipher_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.widget.text.TitleItem
import ru.tech.imageresizershrinker.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState

@Composable
fun TipSheet(
    visible: MutableState<Boolean>
) {
    val settingsState = LocalSettingsState.current
    SimpleSheet(
        sheetContent = {
            Box {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    TitleItem(text = stringResource(R.string.features))
                    Text(
                        stringResource(R.string.features_sub),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                    Divider()

                    TitleItem(text = stringResource(R.string.implementation))
                    Text(
                        stringResource(id = R.string.implementation_sub),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                    Divider()

                    TitleItem(text = stringResource(R.string.file_size))
                    Text(
                        stringResource(id = R.string.file_size_sub),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                    Divider()

                    TitleItem(text = stringResource(R.string.compatibility))
                    Text(
                        stringResource(id = R.string.compatibility_sub),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                }
                Divider()
                Divider(Modifier.align(Alignment.BottomCenter))
            }
        },
        visible = visible,
        title = {
            TitleItem(
                text = stringResource(R.string.cipher),
                icon = Icons.Rounded.Security
            )
        },
        confirmButton = {
            OutlinedButton(
                colors = ButtonDefaults.filledTonalButtonColors(),
                border = BorderStroke(
                    settingsState.borderWidth,
                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                ),
                onClick = { visible.value = false }
            ) {
                Text(stringResource(R.string.close))
            }
        },
    )
}