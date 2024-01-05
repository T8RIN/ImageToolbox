package com.smarttoolfactory.image.beforeafter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Black transparent label to display before or after text
 */
@Composable
internal fun Label(
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    fontWeight: FontWeight = FontWeight.Bold,
    fontSize: TextUnit = 14.sp,
    text: String
) {
    Text(
        text = text,
        color = textColor,
        fontSize = fontSize,
        fontWeight = fontWeight,
        modifier = modifier

    )
}

internal val labelModifier =
    Modifier
        .background(Color.Black.copy(alpha = .5f), RoundedCornerShape(50))
        .padding(horizontal = 12.dp, vertical = 8.dp)

@Composable
fun BoxScope.BeforeLabel(
    text: String = "Before",
    textColor: Color = Color.White,
    fontWeight: FontWeight = FontWeight.Bold,
    fontSize: TextUnit = 14.sp,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter
) {
    Label(
        text = text,
        textColor = textColor,
        fontWeight = fontWeight,
        fontSize = fontSize,
        modifier = Modifier
            .padding(8.dp)
            .align(
                if (contentOrder == ContentOrder.BeforeAfter)
                    Alignment.TopStart else Alignment.TopEnd
            )
            .then(labelModifier)
    )
}

@Composable
fun BoxScope.AfterLabel(
    text: String = "After",
    textColor: Color = Color.White,
    fontWeight: FontWeight = FontWeight.Bold,
    fontSize: TextUnit = 14.sp,
    contentOrder: ContentOrder = ContentOrder.BeforeAfter
) {
    Label(
        text = text,
        textColor = textColor,
        fontWeight = fontWeight,
        fontSize = fontSize,

        modifier = Modifier
            .padding(8.dp)
            .align(
                if (contentOrder == ContentOrder.BeforeAfter)
                    Alignment.TopEnd else Alignment.TopStart
            )
            .then(labelModifier)
    )
}
