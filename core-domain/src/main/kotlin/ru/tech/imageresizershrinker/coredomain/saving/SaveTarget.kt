package ru.tech.imageresizershrinker.coredomain.saving

import ru.tech.imageresizershrinker.coredomain.Domain
import ru.tech.imageresizershrinker.coredomain.model.ImageFormat

interface SaveTarget : Domain {
    val originalUri: String
    val data: ByteArray
    val filename: String?
    val imageFormat: ImageFormat
}