package org.koreader.launcher.driver.light

import org.koreader.launcher.driver.DriverContext

class OnyxC67Controller(@Suppress("UNUSED_PARAMETER") ctx: DriverContext) : org.koreader.launcher.driver.light.common.SysfsBacklightDriver() {

    override val platform: String = "onyx-c67"
    override val brightnessWritePath: String = "/sys/class/backlight/rk28_bl/brightness"
    override val maxBrightness: Int = 255
    override val hasFallback: Boolean = true
}
