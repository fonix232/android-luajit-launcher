package org.koreader.launcher.driver

import android.content.ContentResolver
import android.content.Context
import android.view.Window

/**
 * Runtime dependency bundle passed to backlight driver constructors.
 *
 * EPD drivers are entirely stateless — no runtime context is needed, so their
 * factory type in [org.koreader.launcher.device.DeviceDescriptor] remains
 * `() -> EpdDriver`.
 *
 * Backlight drivers may need a [Context] (for SDK reflection calls), a
 * [ContentResolver] (Settings-based drivers), or a [Window] (window-attribute
 * drivers). Wrapping all three in a single [DriverContext] gives every concrete
 * backlight class the same constructor signature `(DriverContext)`, so
 * `::ClassName` resolves as a `(DriverContext) -> BacklightDriver` factory
 * reference in [DeviceDescriptor.lights].
 *
 * Pass a fresh [DriverContext] whenever the hosting Activity is recreated.
 *
 * @param context the application context — stable, not Activity-bound.
 * @param window  the current Activity window, used by [driver.light.WindowBacklightDriver].
 */
data class DriverContext(
    val context: Context,
    val window: Window,
) {
    /** Convenience accessor — equivalent to [context].contentResolver. */
    val contentResolver: ContentResolver get() = context.contentResolver
}
