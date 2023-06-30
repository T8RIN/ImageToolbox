package ru.tech.imageresizershrinker.presentation.root.widget.sheets

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.image.zoom.ZoomLevel
import com.smarttoolfactory.image.zoom.animatedZoom
import com.smarttoolfactory.image.zoom.rememberAnimatedZoomState
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoomModalSheet(
    bitmap: Bitmap?,
    visible: MutableState<Boolean>
) {
    val settingsState = LocalSettingsState.current
    var showSheet by visible

    val sheetContent: @Composable ColumnScope.() -> Unit = {
        Box(
            Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(
                        bottom = 80.dp,
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
                    .animatedZoom(
                        animatedZoomState = rememberAnimatedZoomState(
                            moveToBounds = true,
                            minZoom = 0.5f,
                            maxZoom = 10f
                        ),
                        zoomOnDoubleTap = { zoomLevel ->
                            when (zoomLevel) {
                                ZoomLevel.Min -> 1f
                                ZoomLevel.Mid -> 5f
                                ZoomLevel.Max -> 10f
                            }
                        }
                    )
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                TitleItem(text = stringResource(R.string.zoom), icon = Icons.Rounded.ZoomIn)
                Spacer(Modifier.weight(1f))
                OutlinedButton(
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                    border = BorderStroke(
                        settingsState.borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                    ),
                    onClick = {
                        showSheet = false
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        }
    }

    if (bitmap != null) {
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