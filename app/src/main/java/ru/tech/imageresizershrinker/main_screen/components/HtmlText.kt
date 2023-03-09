package ru.tech.imageresizershrinker.main_screen.components

import android.os.Build
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.takeOrElse
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.resize_screen.components.blend

@Composable
fun HtmlText(
    html: String,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier: Modifier = Modifier
) {
    val style = LocalTextStyle.current
    val textColor = color.toArgb()
    val linkColor =
        if (isSystemInDarkTheme()) Color.Cyan.blend(MaterialTheme.colorScheme.primary).toArgb()
        else Color.Blue.blend(MaterialTheme.colorScheme.primary, 0.5f).toArgb()

    val density = LocalDensity.current

    val textSize = with(density) {
        style.fontSize.toPx()
    }

    val lineHeight = with(density) {
        style.lineHeight
            .takeOrElse { LocalTextStyle.current.lineHeight }
            .roundToPx()
    }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context)
                .apply {
                    setTextColor(textColor)
                    setLinkTextColor(linkColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                    if (style.lineHeight.isSp && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        this.lineHeight = lineHeight
                    }
                    movementMethod = LinkMovementMethod.getInstance()
                    typeface = ResourcesCompat.getFont(context, R.font.montserrat_regular)
                }
        },
        update = {
            it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    )
}