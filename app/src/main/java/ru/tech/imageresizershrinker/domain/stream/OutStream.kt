package ru.tech.imageresizershrinker.domain.stream

interface OutStream {
    fun write(byte: Byte)

    fun write(byteArray: ByteArray)

    fun close()

    fun flush()
}