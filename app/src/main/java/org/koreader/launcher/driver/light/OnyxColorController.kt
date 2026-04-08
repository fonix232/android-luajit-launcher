/* Controller for some Onyx Color devices.
 * Tested on a Onyx Nova3 Color.
 *
 * Thanks to @ilyats
 */

package org.koreader.launcher.driver.light

import org.koreader.launcher.driver.DriverContext

class OnyxColorController(@Suppress("UNUSED_PARAMETER") ctx: DriverContext) : org.koreader.launcher.driver.light.common.SysfsBacklightDriver() {

    override val platform: String = "onyx-color"
    override val brightnessWritePath: String = "/sys/class/backlight/pwm-backlight.0/brightness"
    override val brightnessReadPath: String = "/sys/class/backlight/pwm-backlight.0/actual_brightness"
    override val maxBrightness: Int = 255
}
