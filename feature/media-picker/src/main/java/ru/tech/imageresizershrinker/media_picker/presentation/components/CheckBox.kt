/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package ru.tech.imageresizershrinker.media_picker.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter

@Composable
fun CheckBox(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    onCheck: (() -> Unit)? = null
) {
    val image = if (isChecked) Icons.Filled.CheckCircle else Icons.Outlined.Circle
    val color = if (isChecked) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurface
    if (onCheck != null) {
        IconButton(
            onClick = onCheck,
            modifier = modifier
        ) {
            Image(
                imageVector = image,
                colorFilter = ColorFilter.tint(color),
                contentDescription = null
            )
        }
    } else {
        Image(
            imageVector = image,
            colorFilter = ColorFilter.tint(color),
            modifier = modifier,
            contentDescription = null
        )
    }
}