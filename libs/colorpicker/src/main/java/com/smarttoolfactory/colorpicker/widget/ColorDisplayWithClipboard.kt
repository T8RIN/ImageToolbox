package com.smarttoolfactory.colorpicker.widget

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colordetector.ColorData
import com.smarttoolfactory.colorpicker.R
import com.smarttoolfactory.extendedcolors.util.ColorUtil
import com.smarttoolfactory.extendedcolors.util.fractionToIntPercent
import kotlin.math.roundToInt


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
            .width(200.dp)
            .background(color = color)
            .clickable {
                expanded = !expanded
            }
            .padding(start = 20.dp, end = 4.dp, top = 5.dp, bottom = 5.dp),
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
            Spacer(modifier = Modifier.weight(1f))
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

@Composable
fun ColorDisplayWithClipboard(
    modifier: Modifier = Modifier,
    color: Color
) {

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current


    val lightness = ColorUtil.colorToHSL(color)[2]
    val textColor = if (lightness < .6f) Color.White else Color.Black

    var expanded by remember { mutableStateOf(false) }

    val hexText = ColorUtil.colorToHex(color)
    Column(
        modifier = modifier
            .shadow(
                2.dp, if (expanded) RoundedCornerShape(25)
                else RoundedCornerShape(50)
            )
            .width(200.dp)
            .background(color = color)
            .clickable {
                expanded = !expanded
            }
            .padding(start = 16.dp, end = 2.dp, top = 2.dp, bottom = 2.dp),
    ) {

        Row {
            Column {
                Text(
                    text = hexText,
                    fontSize = 20.sp,
                    color = textColor
                )
            }
            Spacer(modifier = Modifier.weight(1f))
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

                val rgb = ColorUtil.colorToRGBArray(color)
                val hslArray: FloatArray = ColorUtil.colorToHSL(color)
                val hsvArray: FloatArray = ColorUtil.colorToHSV(color)

                Text(
                    text = "R: ${rgb[0]} G: ${rgb[1]} B: ${rgb[2]}",
                    fontSize = 12.sp,
                    color = textColor
                )
                Text(
                    text = "H: ${hslArray[0].roundToInt()}° " +
                            "S: ${hslArray[1].fractionToIntPercent()}% " +
                            "L: ${hslArray[2].fractionToIntPercent()}%",
                    fontSize = 12.sp,
                    color = textColor
                )
                Text(
                    text = "H: ${hsvArray[0].roundToInt()}° " +
                            "S: ${hsvArray[1].fractionToIntPercent()}% " +
                            "V: ${hsvArray[2].fractionToIntPercent()}%",
                    fontSize = 12.sp,
                    color = textColor
                )
            }
        }
    }
}