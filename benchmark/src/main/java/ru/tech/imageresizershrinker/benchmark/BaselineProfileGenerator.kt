package ru.tech.imageresizershrinker.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import org.junit.Rule
import org.junit.Test

class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun startup() = baselineProfileRule.collect(
        packageName = "ru.tech.imageresizershrinker",
        includeInStartupProfile = true,
        profileBlock = {
            startActivityAndWait()
            device.pressBack()
        }
    )
}