package ru.tech.imageresizershrinker.presentation.root.utils.helper

object ListUtils {
    fun <T> List<T>.nearestFor(item: T): T? {
        return if (isEmpty()) null
        else if (size == 1) first()
        else {
            val curIndex = indexOf(item)
            if (curIndex - 1 >= 0) {
                get(curIndex - 1)
            } else if (curIndex + 1 <= lastIndex) {
                get(curIndex + 1)
            } else {
                null
            }
        }
    }
}