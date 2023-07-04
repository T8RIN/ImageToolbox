package ru.tech.imageresizershrinker.domain.saving

import ru.tech.imageresizershrinker.domain.Domain
import ru.tech.imageresizershrinker.domain.model.MimeType

interface SaveTarget : Domain {
    val originalUri: String
    val data: ByteArray
    val filename: String?
    val mimeType: MimeType
}