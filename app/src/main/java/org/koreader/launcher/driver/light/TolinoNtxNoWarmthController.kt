/* Special controller for Tolino Page 2
 *
 * Same as `./TolinoNtxController.kt` but without warmth support.
 */

package org.koreader.launcher.driver.light

import org.koreader.launcher.driver.DriverContext

class TolinoNtxNoWarmthController(ctx: DriverContext) : org.koreader.launcher.driver.light.common.SystemSettingsBacklightDriver(ctx) {

    override val platform: String = "tolino"
    override val brightnessKey: String = "screen_brightness"
    override val maxBrightness: Int = 255
}
