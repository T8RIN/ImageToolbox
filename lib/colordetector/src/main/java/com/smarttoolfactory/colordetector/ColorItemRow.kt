package com.smarttoolfactory.colordetector

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colordetector.model.ColorData

@Composable
fun ColorItemRow(
    modifier: Modifier = Modifier,
    contentColor: Color = Color.Unspecified,
    containerColor: Color = Color.Unspecified,
    shape: Shape,
    populationPercent: String,
    style: TextStyle,
    colorModifier: Modifier = Modifier,
    colorData: ColorData,
    onClick: (ColorData) -> Unit,
) {
    Row(
        modifier = modifier
            .background(containerColor)
            .clip(shape)
            .clickable {
                onClick(colorData)
            }
            .padding(top = 4.dp, bottom = 4.dp, start = 4.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .then(colorModifier)
                .size(38.dp)
                .background(colorData.color, shape = CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = colorData.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                style = style
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = colorData.hexText.uppercase(),
            style = style,
            fontSize = 16.sp,
            color = contentColor,
        )
    }
}
