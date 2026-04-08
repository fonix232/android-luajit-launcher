package org.koreader.launcher.device

import android.app.Activity
import org.koreader.launcher.driver.BacklightDriver
import org.koreader.launcher.driver.DriverContext
import org.koreader.launcher.driver.EpdDriver
import org.koreader.launcher.extensions.platform

class Device(activity: Activity) {

    private val descriptor = DeviceRegistry.detect(BuildSnapshot.fromBuild())

    val epd: EpdDriver = descriptor.epd()
    val lights: BacklightDriver = descriptor.lights(
        DriverContext(
            context = activity.applicationContext,
            window  = activity.window,
        )
    )

    @Suppress("unused")
    val product = BuildSnapshot.current.product

    val needsWakelocks = descriptor.needsWakelocks
    val bugLifecycle = descriptor.hasBrokenLifecycle
    val hasColorScreen = descriptor.hasColorScreen

    val hasEinkSupport = epd.platform != "none"
    val hasFullEinkSupport = epd.mode == "all"

    val hasLights = when (activity.platform) {
        "android" -> descriptor.hasLights
        else -> false
    }

    val needsView = when (activity.platform) {
        "android_tv" -> true
        "chrome" -> true
        else -> epd.needsView
    }

    val einkPlatform = epd.platform

    val properties: String
        get() = BuildSnapshot.current.let {
            "${it.manufacturer};${it.brand};${it.model};${it.device};${it.product};${it.hardware}"
        }
}
