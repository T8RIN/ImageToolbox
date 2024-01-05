package ru.tech.imageresizershrinker.feature.main.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.AUTHOR_LINK
import ru.tech.imageresizershrinker.core.domain.AUTHOR_TG
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.Github
import ru.tech.imageresizershrinker.core.ui.icons.material.Telegram
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem


private val topShape = RoundedCornerShape(
    topStart = 16.dp,
    topEnd = 16.dp,
    bottomStart = 6.dp,
    bottomEnd = 6.dp
)

private val centerShape = RoundedCornerShape(
    topStart = 6.dp,
    topEnd = 6.dp,
    bottomStart = 6.dp,
    bottomEnd = 6.dp
)

private val bottomShape = RoundedCornerShape(
    topStart = 6.dp,
    topEnd = 6.dp,
    bottomStart = 16.dp,
    bottomEnd = 16.dp
)

@Composable
fun AuthorLinksSheet(
    visible: MutableState<Boolean>
) {
    val context = LocalContext.current

    SimpleSheet(
        visible = visible,
        title = {
            TitleItem(
                text = stringResource(R.string.app_developer_nick),
                icon = Icons.Rounded.Person
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = Color.Transparent,
                onClick = { visible.value = false },
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        sheetContent = {
            Box {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.height(16.dp))
                    PreferenceItem(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(AUTHOR_TG)
                                )
                            )
                        },
                        endIcon = Icons.Rounded.Link,
                        shape = topShape,
                        title = stringResource(R.string.telegram),
                        icon = Icons.Rounded.Telegram,
                        subtitle = stringResource(R.string.app_developer_nick)
                    )
                    Spacer(Modifier.height(4.dp))
                    PreferenceItem(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(AUTHOR_LINK)
                                )
                            )
                        },
                        endIcon = Icons.Rounded.Link,
                        shape = centerShape,
                        title = stringResource(R.string.github),
                        icon = Icons.Rounded.Github,
                        subtitle = stringResource(R.string.app_developer_nick)
                    )
                    Spacer(Modifier.height(4.dp))
                    PreferenceItem(
                        onClick = {
                            Intent(Intent.ACTION_SENDTO).apply {
                                data =
                                    Uri.parse("mailto:${context.getString(R.string.developer_email)}")
                                context.startActivity(this)
                            }
                        },
                        shape = bottomShape,
                        endIcon = Icons.Rounded.Link,
                        title = stringResource(R.string.email),
                        icon = Icons.Rounded.AlternateEmail,
                        subtitle = stringResource(R.string.developer_email)
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    )
}