package org.koreader.launcher.driver

/**
 * Contract for a front-light (backlight) hardware driver.
 *
 * This replaces [org.koreader.launcher.deviceOld.LightsInterface].
 *
 * Design principles:
 * - Any Activity, Context, or ContentResolver dependency is supplied to the
 *   driver's constructor, not to individual method calls. The interface itself
 *   has no Android framework imports.
 * - Logging/diagnostic identifiers are implementation details and belong in the
 *   concrete class, not in this contract.
 * - Stable capabilities are Kotlin properties with default implementations.
 *   Drivers that do not deviate from the defaults omit those properties.
 * - Warmth defaults to a fully no-op implementation: drivers without warmth
 *   hardware override nothing.
 *
 * See [driver/light/] for abstract base classes providing common I/O strategies.
 */
interface BacklightDriver {

    // -------------------------------------------------------------------------
    // Identity
    // -------------------------------------------------------------------------

    /** Stable identifier for this driver implementation, used in logging and diagnostics. */
    val platform: String

    // -------------------------------------------------------------------------
    // Capabilities
    // -------------------------------------------------------------------------

    /**
     * Whether the driver falls back to a software path (e.g. Android window
     * brightness) when hardware access fails.
     */
    val hasFallback: Boolean get() = false

    /**
     * Whether the driver requires [android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS]
     * permission to function.
     */
    val needsPermission: Boolean get() = false

    /** Whether this device has a colour-temperature (warmth) channel. */
    val hasWarmth: Boolean get() = false

    /**
     * Whether the warmth channel can be adjusted independently of brightness
     * (i.e. without modifying the brightness channel).
     */
    val hasStandaloneWarmth: Boolean get() = false

    // -------------------------------------------------------------------------
    // Brightness range
    // -------------------------------------------------------------------------

    /** Minimum accepted brightness value (inclusive). Usually 0. */
    val minBrightness: Int get() = 0

    /** Maximum accepted brightness value (inclusive). Often 255 or 100. */
    val maxBrightness: Int

    // -------------------------------------------------------------------------
    // Warmth range (no-op defaults for drivers without warmth)
    // -------------------------------------------------------------------------

    /** Minimum accepted warmth value (inclusive). */
    val minWarmth: Int get() = 0

    /** Maximum accepted warmth value (inclusive). */
    val maxWarmth: Int get() = 0

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    /**
     * Attempt to ensure the front-light is switched on.
     *
     * Returns 1 if the switch was (or was already) on, 0 if it could not be
     * confirmed. Most drivers simply return 1.
     */
    fun enableFrontlight(): Int = 1

    fun getBrightness(): Int
    fun setBrightness(brightness: Int)

    /** Returns current warmth. Defaults to 0 for drivers without warmth. */
    fun getWarmth(): Int = 0

    /** Sets warmth. No-op by default for drivers without warmth. */
    fun setWarmth(warmth: Int) {}
}
