package ru.tech.imageresizershrinker.core.domain.model

sealed class Preset {

    data object Telegram : Preset()

    data object None : Preset()

    class Numeric(val value: Int) : Preset()

    fun isTelegram(): Boolean = this is Telegram

    fun value(): Int? = (this as? Numeric)?.value

    fun isEmpty(): Boolean = this is None

    companion object {
        fun createListFromInts(presets: String?): List<Preset> {
            return ((presets?.split("*")?.map {
                it.toInt()
            } ?: List(6) { 100 - it * 10 })).toSortedSet().reversed().toList()
                .map { Numeric(it) }
        }
    }
}