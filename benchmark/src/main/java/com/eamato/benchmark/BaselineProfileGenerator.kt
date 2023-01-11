package com.eamato.benchmark

import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalBaselineProfilesApi
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineRule = BaselineProfileRule()

    @Test
    fun generateBaselineProfile() = baselineRule.collectBaselineProfile(
        packageName = "eamato.funn.r6companion"
    ) {
        pressHome()
        startActivityAndWait()

        val newsList = device.findObject(By.res(packageName, "rv_news"))

        device.waitForIdle()

        newsList.setGestureMargin(device.displayWidth / 5)
        newsList.fling(Direction.DOWN)
    }
}