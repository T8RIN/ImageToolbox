package ru.tech.imageresizershrinker.presentation.widget.buttons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.utils.modifier.fabBorder

@Composable
fun BottomButtonsBlock(
    targetState: Pair<Boolean, Boolean>,
    onPickImage: () -> Unit,
    onSaveBitmap: () -> Unit,
    canSave: Boolean = true,
    actions: @Composable RowScope.() -> Unit
) {
    AnimatedContent(
        targetState = targetState
    ) { (isNull, inside) ->
        if (isNull) {
            ExtendedFloatingActionButton(
                onClick = onPickImage,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .fabBorder(),
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                text = {
                    Text(stringResource(R.string.pick_image_alt))
                },
                icon = {
                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                }
            )
        } else if (inside) {
            BottomAppBar(
                modifier = Modifier.drawHorizontalStroke(true),
                actions = actions,
                floatingActionButton = {
                    Row {
                        FloatingActionButton(
                            onClick = onPickImage,
                            modifier = Modifier.fabBorder(),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                        ) {
                            Icon(Icons.Rounded.AddPhotoAlternate, null)
                        }
                        AnimatedVisibility(visible = canSave) {
                            Row {
                                Spacer(Modifier.width(16.dp))
                                FloatingActionButton(
                                    onClick = onSaveBitmap,
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                    modifier = Modifier.fabBorder(),
                                ) {
                                    Icon(Icons.Rounded.Save, null)
                                }
                            }
                        }
                    }
                }
            )
        } else {
            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
            ) {
                FloatingActionButton(
                    onClick = onPickImage,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    modifier = Modifier.fabBorder(),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                ) {
                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                }
                AnimatedVisibility(visible = canSave) {
                    Column {
                        Spacer(Modifier.height(16.dp))
                        FloatingActionButton(
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                            modifier = Modifier.fabBorder(),
                            onClick = onSaveBitmap
                        ) {
                            Icon(Icons.Rounded.Save, null)
                        }
                    }
                }
            }
        }
    }
}