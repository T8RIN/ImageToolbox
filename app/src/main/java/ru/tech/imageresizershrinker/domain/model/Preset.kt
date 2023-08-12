package ru.tech.imageresizershrinker.domain.model

sealed class Preset {

    data object Telegram : Preset()

    data object None : Preset()

    class Numeric(val value: Int) : Preset()

    fun isTelegram(): Boolean = this is Telegram

    fun value(): Int? = (this as? Numeric)?.value

    fun isEmpty(): Boolean = this is None
}