package ru.tech.imageresizershrinker.core.domain.saving

import ru.tech.imageresizershrinker.core.domain.Domain
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat

interface SaveTarget : Domain {
    val originalUri: String
    val data: ByteArray
    val filename: String?
    val imageFormat: ImageFormat
}