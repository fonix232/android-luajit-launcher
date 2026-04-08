package org.koreader.launcher.device

import org.koreader.launcher.driver.BacklightDriver
import org.koreader.launcher.driver.DriverContext
import org.koreader.launcher.driver.EpdDriver

/**
 * A compile-time static descriptor for one known device (or family of devices).
 *
 * No driver objects are created when a [DeviceDescriptor] is constructed — [epd] and
 * [lights] are constructor references (`() -> T`) that are only invoked by
 * [DeviceRegistry] after a match is confirmed at runtime.
 *
 * @param id       Stable identifier for this device, used in logging and tests.
 * @param match    Declarative build-property matcher.
 * @param epd      Factory for the EPD (e-ink display refresh) driver.
 * @param lights   Factory for the front-light driver.
 * @param quirks   Set of known behavioural deviations requiring workarounds.
 * @param features Set of hardware capabilities present on this device.
 */
data class DeviceDescriptor(
    val id: String,
    val match: BuildMatch,
    val epd: () -> EpdDriver,
    val lights: (DriverContext) -> BacklightDriver,
    val quirks: Set<Quirk> = emptySet(),
    val features: Set<Feature> = emptySet(),
) {
    // Convenience accessors — avoid repeated filterIsInstance at call sites.
    val frontLight: Feature.FrontLight?
        get() = features.filterIsInstance<Feature.FrontLight>().firstOrNull()

    val hasColorScreen: Boolean
        get() = Feature.ColorScreen in features

    val hasLights: Boolean
        get() = frontLight != null && Quirk.NoLights !in quirks

    val hasWarmth: Boolean
        get() = frontLight?.warmth ?: false

    val hasStandaloneWarmth: Boolean
        get() = frontLight?.standaloneWarmth ?: false

    val hasBrokenLifecycle: Boolean
        get() = Quirk.BrokenLifecycle in quirks

    val needsWakelocks: Boolean
        get() = Quirk.NeedsWakelocks in quirks
}
