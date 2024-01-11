package ru.tech.imageresizershrinker.core.ui.widget.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoomModalSheet(
    data: Any?,
    visible: MutableState<Boolean>
) {
    val settingsState = LocalSettingsState.current
    var showSheet by visible

    val sheetContent: @Composable ColumnScope.() -> Unit = {
        val zoomState = rememberZoomState(maxScale = 10f)
        Column(
            Modifier.navigationBarsPadding()
        ) {
            Picture(
                model = data,
                contentDescription = null,
                shape = RectangleShape,
                contentScale = ContentScale.Inside,
                showTransparencyChecker = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .border(
                        settingsState.borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(),
                        RoundedCornerShape(4.dp)
                    )
                    .background(
                        MaterialTheme.colorScheme
                            .outlineVariant()
                            .copy(alpha = 0.1f),
                        RoundedCornerShape(4.dp)
                    )
                    .transparencyChecker()
                    .zoomable(
                        zoomState = zoomState
                    )
            )
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TitleItem(text = stringResource(R.string.zoom), icon = Icons.Rounded.ZoomIn)
                Spacer(Modifier.weight(1f))
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = {
                        showSheet = false
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    AutoSizeText(stringResource(R.string.close))
                }
            }
        }
    }

    if (data != null) {
        SimpleSheet(
            sheetContent = sheetContent,
            visible = visible,
            dragHandle = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    BottomSheetDefaults.DragHandle()
                }
            }
        )
    }
}