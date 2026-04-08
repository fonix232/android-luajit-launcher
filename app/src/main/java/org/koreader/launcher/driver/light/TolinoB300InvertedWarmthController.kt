// Subclass of TolinoB300Controller for devices where warmth scale is inverted:
// Tolino Vision 6 and Tolino Shine 4.

package org.koreader.launcher.driver.light

import org.koreader.launcher.driver.DriverContext

class TolinoB300InvertedWarmthController(ctx: DriverContext) : TolinoB300Controller(ctx) {
    override val invertedWarmth: Boolean = true
}
