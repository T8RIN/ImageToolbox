package ru.tech.imageresizershrinker.feature.cipher.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.coreui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.coreui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.coreui.widget.text.TitleItem

@Composable
fun CipherTipSheet(
    visible: MutableState<Boolean>
) {
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
                    HorizontalDivider()

                    TitleItem(text = stringResource(R.string.implementation))
                    Text(
                        stringResource(id = R.string.implementation_sub),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                    HorizontalDivider()

                    TitleItem(text = stringResource(R.string.file_size))
                    Text(
                        stringResource(id = R.string.file_size_sub),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                    HorizontalDivider()

                    TitleItem(text = stringResource(R.string.compatibility))
                    Text(
                        stringResource(id = R.string.compatibility_sub),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                }
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
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { visible.value = false }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
    )
}