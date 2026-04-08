/* handle frontlight within the activity, without affecting other activities */

package org.koreader.launcher.driver.light

import org.koreader.launcher.driver.DriverContext

class GenericController(ctx: DriverContext) : org.koreader.launcher.driver.light.common.WindowBacklightDriver(ctx) {
    override val platform: String = "generic"
}
