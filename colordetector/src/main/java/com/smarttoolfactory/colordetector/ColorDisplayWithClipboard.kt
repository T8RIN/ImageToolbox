package com.smarttoolfactory.colordetector

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.extendedcolors.util.ColorUtil


@Composable
fun ColorDisplayWithClipboard(
    modifier: Modifier = Modifier,
    colorData: ColorData
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val color = colorData.color
    val colorName = colorData.name

    val lightness = ColorUtil.colorToHSL(color)[2]
    val textColor = if (lightness < .6f) Color.White else Color.Black

    var expanded by remember { mutableStateOf(false) }

    val hexText = colorData.hexText
    Column(
        modifier = modifier
            .shadow(
                2.dp, if (expanded) RoundedCornerShape(25)
                else RoundedCornerShape(50)
            )
            .width(if (expanded) 170.dp else 160.dp)
            .background(color = color)
            .clickable {
                expanded = !expanded
            }
            .padding(start = 16.dp, end = 2.dp, top = 2.dp, bottom = 2.dp),
    ) {

        Row {
            Column {
                Text(text = colorName, fontSize = 10.sp, color = textColor)
                Text(
                    text = hexText,
                    fontSize = 20.sp,
                    color = textColor
                )
            }
            Spacer(modifier=Modifier.weight(1f))
            IconButton(
                onClick = {
                    Toast.makeText(context, "Copied $hexText", Toast.LENGTH_SHORT).show()
                    clipboardManager.setText(AnnotatedString(hexText))
                }) {
                Icon(
                    tint = textColor,
                    painter = painterResource(id = R.drawable.ic_baseline_content_copy_24),
                    contentDescription = "clipboard"
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                Text(text = colorData.rgb, fontSize = 12.sp, color = textColor)
                Text(text = colorData.hslString, fontSize = 12.sp, color = textColor)
                Text(text = colorData.hsvString, fontSize = 12.sp, color = textColor)
            }
        }
    }
}