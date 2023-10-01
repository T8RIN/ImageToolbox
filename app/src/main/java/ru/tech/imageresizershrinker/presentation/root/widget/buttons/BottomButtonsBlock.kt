package ru.tech.imageresizershrinker.presentation.root.widget.buttons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.autoElevatedBorder
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.containerFabBorder
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.drawHorizontalStroke

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
                    .autoElevatedBorder(),
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
                            modifier = Modifier.containerFabBorder(),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                        ) {
                            Icon(Icons.Rounded.AddPhotoAlternate, null)
                        }
                        AnimatedVisibility(visible = canSave) {
                            Row {
                                Spacer(Modifier.width(8.dp))
                                FloatingActionButton(
                                    onClick = onSaveBitmap,
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                    modifier = Modifier.containerFabBorder(),
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
                modifier = Modifier
                    .fillMaxHeight()
                    .container(
                        shape = RectangleShape,
                        color = MaterialTheme.colorScheme.surfaceContainer
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row { actions() }
                Spacer(Modifier.height(8.dp))
                FloatingActionButton(
                    onClick = onPickImage,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    modifier = Modifier.containerFabBorder(),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                ) {
                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                }
                AnimatedVisibility(visible = canSave) {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        FloatingActionButton(
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                            modifier = Modifier.containerFabBorder(),
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