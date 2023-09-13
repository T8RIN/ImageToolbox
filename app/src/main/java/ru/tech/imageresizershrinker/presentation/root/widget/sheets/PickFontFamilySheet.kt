package ru.tech.imageresizershrinker.presentation.root.widget.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FontDownload
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.main_screen.components.FontSelectionItem
import ru.tech.imageresizershrinker.presentation.root.model.UiFontFam
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem

@Composable
fun PickFontFamilySheet(
    visible: MutableState<Boolean>,
    onFontSelected: (UiFontFam) -> Unit
) {
    SimpleSheet(
        visible = visible,
        sheetContent = {
            Box {
                HorizontalDivider(
                    Modifier.zIndex(100f)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .verticalScroll(
                            rememberScrollState()
                        )
                        .padding(vertical = 16.dp, horizontal = 8.dp)
                ) {
                    UiFontFam.entries.forEach { font ->
                        FontSelectionItem(
                            font = font,
                            onClick = {
                                onFontSelected(font)
                            }
                        )
                    }
                }
                HorizontalDivider(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(100f)
                )
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = { visible.value = false }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                icon = Icons.Rounded.FontDownload,
                text = stringResource(R.string.font),
            )
        }
    )
}