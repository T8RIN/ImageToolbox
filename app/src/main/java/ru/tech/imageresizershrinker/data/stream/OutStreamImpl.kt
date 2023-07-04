package ru.tech.imageresizershrinker.data.stream

import androidx.datastore.core.Closeable
import ru.tech.imageresizershrinker.domain.stream.OutStream
import java.io.Flushable
import java.io.OutputStream

class OutStreamImpl(
    private val stream: OutputStream
) : OutStream, Closeable, Flushable {
    override fun write(byte: Byte) = stream.write(byteArrayOf(byte))

    override fun write(byteArray: ByteArray) = stream.write(byteArray)

    override fun close() = stream.close()

    override fun flush() = stream.flush()
}