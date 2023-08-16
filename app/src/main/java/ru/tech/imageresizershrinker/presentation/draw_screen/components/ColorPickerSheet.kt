package ru.tech.imageresizershrinker.presentation.draw_screen.components

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.smarttoolfactory.colordetector.ImageColorDetector
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.pick_color_from_image_screen.copyColorIntoClipboard
import ru.tech.imageresizershrinker.presentation.pick_color_from_image_screen.format
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.shimmer
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun ColorPickerSheet(
    visible: MutableState<Boolean>,
    bitmap: Bitmap?,
    onColorChange: (Color) -> Unit,
    color: Color
) {
    val context = LocalContext.current
    val settingsState = LocalSettingsState.current
    val scope = rememberCoroutineScope()
    val toastHostState = LocalToastHost.current

    SimpleSheet(
        sheetContent = {
            Box {
                remember(bitmap) { bitmap?.asImageBitmap() }?.let {
                    ImageColorDetector(
                        color = color,
                        imageBitmap = it,
                        onColorChange = onColorChange,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center)
                            .block(resultPadding = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } ?: Box(
                    Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                        .block(resultPadding = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .fillMaxSize()
                        .shimmer(true)
                )
                HorizontalDivider(
                    Modifier
                        .zIndex(100f)
                        .align(Alignment.BottomCenter)
                )
                HorizontalDivider()
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    visible.value = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = BorderStroke(
                    settingsState.borderWidth,
                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                )
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp
                    )
            ) {
                Box(
                    Modifier
                        .padding(end = 16.dp)
                        .background(
                            color = animateColorAsState(color).value,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .size(40.dp)
                        .border(
                            width = settingsState.borderWidth,
                            color = MaterialTheme.colorScheme.outlineVariant(
                                onTopOf = animateColorAsState(color).value
                            ),
                            shape = RoundedCornerShape(11.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            context.copyColorIntoClipboard(
                                context.getString(R.string.color),
                                color.format()
                            )
                            scope.launch {
                                toastHostState.showToast(
                                    icon = Icons.Rounded.ContentPaste,
                                    message = context.getString(R.string.color_copied)
                                )
                            }
                        }
                )

                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            context.copyColorIntoClipboard(
                                context.getString(R.string.color),
                                color.format()
                            )
                            scope.launch {
                                toastHostState.showToast(
                                    icon = Icons.Rounded.ContentPaste,
                                    message = context.getString(R.string.color_copied)
                                )
                            }
                        }
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .border(
                            settingsState.borderWidth,
                            MaterialTheme.colorScheme.outlineVariant(
                                onTopOf = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            RoundedCornerShape(8.dp)
                        ),
                    text = color.format(),
                    style = LocalTextStyle.current.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }
        },
        visible = visible
    )
}