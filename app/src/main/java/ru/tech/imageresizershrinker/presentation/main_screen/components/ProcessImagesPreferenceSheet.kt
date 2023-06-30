package ru.tech.imageresizershrinker.presentation.main_screen.components

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olshevski.navigation.reimagined.navigate
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.LocalNavController
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.Screen
import ru.tech.imageresizershrinker.presentation.root.widget.image.Picture
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.BatchResizePreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.BytesResizePreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.CipherPreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.ComparePreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.CropPreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.DeleteExifPreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.DrawPreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.FilterPreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.GeneratePalettePreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.ImagePreviewPreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.LimitsPreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.PickColorPreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.SingleResizePreference
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun ProcessImagesPreferenceSheet(
    uris: List<Uri>,
    visible: MutableState<Boolean>
) {
    val settingsState = LocalSettingsState.current
    val navController = LocalNavController.current
    SimpleSheet(
        title = {
            TitleItem(
                text = stringResource(R.string.image),
                icon = Icons.Rounded.Image
            )
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    visible.value = false
                },
                border = BorderStroke(
                    settingsState.borderWidth,
                    MaterialTheme.colorScheme.outlineVariant()
                )
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        sheetContent = {
            val navigate: (Screen) -> Unit = { screen ->
                navController.apply {
                    navigate(screen)
                    visible.value = false
                }
            }
            val color = MaterialTheme.colorScheme.secondaryContainer
            Box(Modifier.fillMaxWidth()) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterVertically
                    )
                ) {
                    item {
                        val pic: @Composable (Uri?, Dp, Int) -> Unit =
                            { uri, height, extra ->
                                Box(
                                    Modifier
                                        .padding(
                                            bottom = 8.dp,
                                            start = 4.dp,
                                            end = 4.dp
                                        )
                                        .height(height)
                                        .width(120.dp)
                                        .border(
                                            width = settingsState.borderWidth,
                                            color = MaterialTheme.colorScheme.outlineVariant(),
                                            shape = MaterialTheme.shapes.extraLarge
                                        )
                                        .clip(shape = MaterialTheme.shapes.extraLarge)
                                ) {
                                    Picture(
                                        model = uri,
                                        shape = MaterialTheme.shapes.extraLarge,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    if (extra > 0) {
                                        Box(
                                            Modifier
                                                .fillMaxSize()
                                                .background(
                                                    MaterialTheme.colorScheme.scrim.copy(
                                                        alpha = 0.6f
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "+$extra",
                                                color = Color.White,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        if (uris.size in 1..2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(uris.size) {
                                    pic(uris.getOrNull(it), 100.dp, 0)
                                }
                            }
                        } else if (uris.size >= 3) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                pic(uris.getOrNull(0), 100.dp, 0)
                                Column {
                                    pic(uris.getOrNull(1), 60.dp, 0)
                                    pic(uris.getOrNull(2), 60.dp, uris.size - 3)
                                }
                            }
                        }
                    }
                    if (uris.size <= 1) {
                        item {
                            SingleResizePreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navigate(
                                        Screen.SingleResize(uris.firstOrNull())
                                    )
                                },
                                color = color
                            )
                        }
                        item {
                            BytesResizePreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navigate(
                                        Screen.ResizeByBytes(uris)
                                    )
                                },
                                color = color
                            )
                        }
                        item {
                            CropPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.Crop(uris.firstOrNull())) },
                                color = color
                            )
                        }
                        item {
                            FilterPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.Filter(uris)) },
                                color = color
                            )
                        }
                        item {
                            DrawPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.Draw(uris.firstOrNull())) },
                                color = color
                            )
                        }
                        item {
                            CipherPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.Cipher(uris.firstOrNull())) },
                                color = color
                            )
                        }
                        item {
                            ImagePreviewPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navigate(
                                        Screen.ImagePreview(uris)
                                    )
                                },
                                color = color
                            )
                        }
                        item {
                            PickColorPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navigate(
                                        Screen.PickColorFromImage(uris.firstOrNull())
                                    )
                                },
                                color = color
                            )
                        }
                        item {
                            GeneratePalettePreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navigate(
                                        Screen.GeneratePalette(uris.firstOrNull())
                                    )
                                },
                                color = color
                            )
                        }
                        item {
                            DeleteExifPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.DeleteExif(uris)) },
                                color = color
                            )
                        }
                        item {
                            LimitsPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.LimitResize(uris)) },
                                color = color
                            )
                        }
                    } else {
                        item {
                            BatchResizePreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navigate(
                                        Screen.BatchResize(uris)
                                    )
                                },
                                color = color
                            )
                        }
                        item {
                            BytesResizePreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navigate(
                                        Screen.ResizeByBytes(uris)
                                    )
                                },
                                color = color
                            )
                        }
                        item {
                            FilterPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navigate(
                                        Screen.Filter(uris)
                                    )
                                },
                                color = color
                            )
                        }
                        item {
                            ImagePreviewPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navigate(
                                        Screen.ImagePreview(uris)
                                    )
                                },
                                color = color
                            )
                        }
                        if (uris.size == 2) {
                            item {
                                ComparePreference(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        navigate(
                                            Screen.Compare(uris)
                                        )
                                    },
                                    color = color
                                )
                            }
                        }
                        item {
                            LimitsPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.LimitResize(uris)) },
                                color = color
                            )
                        }
                        item {
                            DeleteExifPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.DeleteExif(uris)) },
                                color = color
                            )
                        }
                    }
                }
                Divider(Modifier.align(Alignment.TopCenter))
                Divider(Modifier.align(Alignment.BottomCenter))
            }
        },
        visible = visible,
    )
}