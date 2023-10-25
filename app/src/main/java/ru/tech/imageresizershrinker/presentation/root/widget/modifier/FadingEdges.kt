package ru.tech.imageresizershrinker.presentation.root.widget.modifier

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import com.gigamole.composefadingedges.content.FadingEdgesContentType
import com.gigamole.composefadingedges.content.scrollconfig.FadingEdgesScrollConfig
import com.gigamole.composefadingedges.fill.FadingEdgesFillType
import com.gigamole.composefadingedges.horizontalFadingEdges
import com.gigamole.composefadingedges.verticalFadingEdges


@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.fadingEdges(
    scrollableState: ScrollableState? = null,
    color: Color = Color.Unspecified,
    isVertical: Boolean = false,
    spanCount: Int? = null,
    scrollFactor: Float = 1.25f,
    enabled: Boolean = true
) = composed {
    if (!enabled) return@composed this

    val fillType = if (color.isSpecified) {
        FadingEdgesFillType.FadeColor(
            color = color
        )
    } else {
        FadingEdgesFillType.FadeClip()
    }

    val scrollConfig = FadingEdgesScrollConfig.Dynamic(
        scrollFactor = scrollFactor
    )

    when (scrollableState) {
        is ScrollState -> {
            if (isVertical) {
                verticalFadingEdges(
                    contentType = FadingEdgesContentType.Dynamic.Scroll(
                        scrollConfig = scrollConfig,
                        state = scrollableState
                    ),
                    fillType = fillType
                )
            } else {
                horizontalFadingEdges(
                    contentType = FadingEdgesContentType.Dynamic.Scroll(
                        scrollConfig = scrollConfig,
                        state = scrollableState
                    ),
                    fillType = fillType
                )
            }
        }

        is LazyListState -> {
            if (isVertical) {
                verticalFadingEdges(
                    contentType = FadingEdgesContentType.Dynamic.Lazy.List(
                        scrollConfig = scrollConfig,
                        state = scrollableState
                    ),
                    fillType = fillType
                )
            } else {
                horizontalFadingEdges(
                    contentType = FadingEdgesContentType.Dynamic.Lazy.List(
                        scrollConfig = scrollConfig,
                        state = scrollableState
                    ),
                    fillType = fillType
                )
            }
        }

        is LazyGridState -> {
            require(spanCount != 0)
            if (isVertical) {
                verticalFadingEdges(
                    contentType = FadingEdgesContentType.Dynamic.Lazy.Grid(
                        scrollConfig = scrollConfig,
                        state = scrollableState,
                        spanCount = spanCount!!
                    ),
                    fillType = fillType
                )
            } else {
                horizontalFadingEdges(
                    contentType = FadingEdgesContentType.Dynamic.Lazy.Grid(
                        scrollConfig = scrollConfig,
                        state = scrollableState,
                        spanCount = spanCount!!
                    ),
                    fillType = fillType
                )
            }
        }

        is LazyStaggeredGridState -> {
            require(spanCount != 0)
            if (isVertical) {
                verticalFadingEdges(
                    contentType = FadingEdgesContentType.Dynamic.Lazy.StaggeredGrid(
                        scrollConfig = scrollConfig,
                        state = scrollableState,
                        spanCount = spanCount!!
                    ),
                    fillType = fillType
                )
            } else {
                horizontalFadingEdges(
                    contentType = FadingEdgesContentType.Dynamic.Lazy.StaggeredGrid(
                        scrollConfig = scrollConfig,
                        state = scrollableState,
                        spanCount = spanCount!!
                    ),
                    fillType = fillType
                )
            }
        }

        else -> {
            if (isVertical) {
                verticalFadingEdges(
                    contentType = FadingEdgesContentType.Static,
                    fillType = fillType
                )
            } else {
                horizontalFadingEdges(
                    contentType = FadingEdgesContentType.Static,
                    fillType = fillType
                )
            }
        }
    }
}