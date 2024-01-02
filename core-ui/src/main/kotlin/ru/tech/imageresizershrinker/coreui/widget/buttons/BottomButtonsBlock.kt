package ru.tech.imageresizershrinker.coreui.widget.buttons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.coreui.widget.modifier.container
import ru.tech.imageresizershrinker.coreui.widget.modifier.drawHorizontalStroke

@Composable
fun BottomButtonsBlock(
    targetState: Pair<Boolean, Boolean>,
    onPickImage: () -> Unit,
    onSaveBitmap: () -> Unit,
    canSave: Boolean = true,
    columnarFab: (@Composable ColumnScope.() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit
) {
    AnimatedContent(
        targetState = targetState
    ) { (isNull, inside) ->
        if (isNull) {
            EnhancedFloatingActionButton(
                onClick = onPickImage,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp),
                content = {
                    Spacer(Modifier.width(16.dp))
                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                    Spacer(Modifier.width(16.dp))
                    Text(stringResource(R.string.pick_image_alt))
                    Spacer(Modifier.width(16.dp))
                }
            )
        } else if (inside) {
            BottomAppBar(
                modifier = Modifier.drawHorizontalStroke(true),
                actions = actions,
                floatingActionButton = {
                    Row {
                        EnhancedFloatingActionButton(
                            onClick = onPickImage,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddPhotoAlternate,
                                contentDescription = null
                            )
                        }
                        AnimatedVisibility(visible = canSave) {
                            Row {
                                Spacer(Modifier.width(8.dp))
                                EnhancedFloatingActionButton(
                                    onClick = onSaveBitmap
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Save,
                                        contentDescription = null
                                    )
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
                        color = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row { actions() }
                Spacer(Modifier.height(8.dp))
                EnhancedFloatingActionButton(
                    onClick = onPickImage,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Icon(imageVector = Icons.Rounded.AddPhotoAlternate, contentDescription = null)
                }
                columnarFab?.let {
                    Spacer(Modifier.height(8.dp))
                    it()
                }
                AnimatedVisibility(visible = canSave) {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        EnhancedFloatingActionButton(
                            onClick = onSaveBitmap
                        ) {
                            Icon(imageVector = Icons.Rounded.Save, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}