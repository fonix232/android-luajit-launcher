package org.koreader.launcher.driver

import android.view.View

/**
 * Contract for an e-ink display refresh (EPD) driver.
 *
 * This replaces [org.koreader.launcher.deviceOld.EPDInterface].
 *
 * Design principles:
 * - All EPD drivers are stateless. They reach hardware through platform-private
 *   reflection entry-points that require no caller-supplied context. Constructor
 *   parameters are therefore not needed; every concrete implementation keeps a
 *   no-arg constructor so that `::ClassName` resolves directly as a
 *   `() -> EpdDriver` factory reference in
 *   [org.koreader.launcher.device.DeviceDescriptor.epd].
 * - Driver identity values ([platform], [mode]) and hardware codes ([waveformFull]
 *   etc.) are Kotlin properties. Implementations express them as `override val …`
 *   declarations, not method bodies.
 * - Timing delays default to 0; most platforms do not add artificial delays.
 * - [resume] and [pause] are lifecycle hooks retained for forward compatibility.
 *   All current implementations leave them empty.
 *
 * See [driver/epd/] for abstract base classes providing per-platform helper logic.
 */
interface EpdDriver {

    // -------------------------------------------------------------------------
    // Driver identity
    // -------------------------------------------------------------------------

    /**
     * Hardware platform string forwarded to KOReader Lua.
     * Established values: `"freescale"`, `"qualcomm"`, `"rockchip"`.
     */
    val platform: String

    /**
     * EPD update mode string forwarded to KOReader Lua.
     * `"all"` — both full and partial updates are used.
     * `"full-only"` — only full-screen refreshes are issued.
     */
    val mode: String

    // -------------------------------------------------------------------------
    // View requirement
    // -------------------------------------------------------------------------

    /**
     * Whether a live UI [View] is required by [setEpdMode].
     *
     * When `true` the view must be attached and have a valid drawing surface
     * (the driver invokes methods on `android.view.View` via reflection).
     * When `false` the caller may pass a placeholder; the driver will not
     * dereference it for the refresh call.
     */
    val needsView: Boolean get() = false

    // -------------------------------------------------------------------------
    // Waveform mode codes
    // -------------------------------------------------------------------------

    /** Waveform code for a full-quality, full-screen page refresh. */
    val waveformFull: Int

    /** Waveform code for a partial (region) refresh. */
    val waveformPartial: Int

    /** Waveform code for a full-quality UI overlay refresh. */
    val waveformFullUi: Int

    /** Waveform code for a partial-quality UI refresh. */
    val waveformPartialUi: Int

    /** Waveform code for a fast (lower-quality) refresh. */
    val waveformFast: Int

    // -------------------------------------------------------------------------
    // Timing delays (milliseconds) — most drivers return 0
    // -------------------------------------------------------------------------

    val waveformDelay: Int get() = 0
    val waveformDelayUi: Int get() = 0
    val waveformDelayFast: Int get() = 0

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    /**
     * Trigger a display refresh.
     *
     * @param view     The target view; may be unused when [needsView] is `false`.
     * @param mode     Numeric waveform mode (one of the `waveform*` constants).
     * @param delay    Pre-refresh pause in milliseconds.
     * @param x        Refresh-region origin X.
     * @param y        Refresh-region origin Y.
     * @param width    Refresh-region width.
     * @param height   Refresh-region height.
     * @param epdMode  Named mode string used by some platforms (e.g. `"EPD_FULL"`).
     *                 `null` for platforms that do not use string modes.
     */
    fun setEpdMode(
        view: View,
        mode: Int,
        delay: Long,
        x: Int, y: Int, width: Int, height: Int,
        epdMode: String?,
    )

    /** Called when the hosting Activity resumes. */
    fun resume() {}

    /** Called when the hosting Activity pauses. */
    fun pause() {}
}
