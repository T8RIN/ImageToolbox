package ru.tech.imageresizershrinker.core.ui.widget.modifier

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

object ContainerShapeDefaults {

    fun shapeForIndex(
        index: Int,
        size: Int
    ): Shape = when {
        index == -1 || size == 1 -> defaultShape
        index == 0 && size > 1 -> topShape
        index == size - 1 -> bottomShape
        else -> centerShape
    }

    val topShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 6.dp,
        bottomEnd = 6.dp
    )
    val centerShape = RoundedCornerShape(
        topStart = 6.dp,
        topEnd = 6.dp,
        bottomStart = 6.dp,
        bottomEnd = 6.dp
    )
    val bottomShape = RoundedCornerShape(
        topStart = 6.dp,
        topEnd = 6.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )
    val defaultShape = RoundedCornerShape(16.dp)
}