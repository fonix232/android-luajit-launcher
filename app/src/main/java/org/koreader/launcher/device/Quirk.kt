package org.koreader.launcher.device

/**
 * A device quirk describes a known behavioural deviation that requires a workaround.
 *
 * Variants are [data object] so they compare correctly when stored in a [Set].
 * Future quirks that need to carry a payload should use [data class] instead.
 */
sealed class Quirk {
    /** Activity lifecycle callbacks are unreliable on this device (e.g. Onyx Poke 2). */
    data object BrokenLifecycle : Quirk()

    /** The device requires wakelocks to keep the screen on during long operations. */
    data object NeedsWakelocks : Quirk()

    /** The device has no controllable front-light hardware. */
    data object NoLights : Quirk()
}
