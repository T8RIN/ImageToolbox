package ru.tech.imageresizershrinker.presentation.widget.controls

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.model.ResizeType
import ru.tech.imageresizershrinker.presentation.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.presentation.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalSettingsState


@Composable
fun ResizeGroup(
    enabled: Boolean,
    resizeType: ResizeType,
    onResizeChange: (ResizeType) -> Unit
) {
    val state = rememberSaveable { mutableStateOf(false) }
    val settingsState = LocalSettingsState.current

    ToggleGroupButton(
        modifier = Modifier
            .block(shape = RoundedCornerShape(24.dp))
            .padding(start = 3.dp, end = 2.dp),
        enabled = enabled,
        title = {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(R.string.resize_type),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                CircleShape
                            )
                            .padding(1.dp)
                            .size(
                                with(LocalDensity.current) {
                                    LocalTextStyle.current.fontSize.toDp()
                                }
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        state.value = true
                                    }
                                )
                            }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            },
            items = listOf(
                stringResource(R.string.explicit),
                stringResource(R.string.flexible),
                stringResource(R.string.ratio)
            ),
            selectedIndex = when (resizeType) {
                ResizeType.Explicit -> 0
                ResizeType.Flexible -> 1
                ResizeType.Ratio -> 2
            },
        indexChanged = {
            onResizeChange(
                when (it) {
                    0 -> ResizeType.Explicit
                    1 -> ResizeType.Flexible
                    else -> ResizeType.Ratio
                }
            )
        }
    )

    SimpleSheet(
        sheetContent = {
            Box {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    TitleItem(text = stringResource(R.string.explicit))
                    Text(
                        text = stringResource(R.string.explicit_description),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                    Divider()

                    TitleItem(text = stringResource(R.string.flexible))
                    Text(
                        text = stringResource(R.string.flexible_description),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                    Divider()

                    TitleItem(text = stringResource(R.string.ratio))
                    Text(
                        stringResource(id = R.string.ratio_description),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                }
                Divider()
                Divider(Modifier.align(Alignment.BottomCenter))
            }
        },
        visible = state,
        title = {
            TitleItem(text = stringResource(R.string.resize_type))
        },
        confirmButton = {
            OutlinedButton(
                colors = ButtonDefaults.filledTonalButtonColors(),
                border = BorderStroke(
                    settingsState.borderWidth,
                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                ),
                onClick = { state.value = false }
            ) {
                Text(stringResource(R.string.close))
            }
        }
    )
}