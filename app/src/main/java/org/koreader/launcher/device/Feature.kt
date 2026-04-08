package org.koreader.launcher.device

/**
 * A device feature describes a hardware capability that is present on this device.
 *
 * Variants are [data object] for simple flags and [data class] for capability descriptors
 * that carry configuration. Storing features in a [Set] lets callers use
 * [filterIsInstance] to retrieve a specific variant with its payload, without needing
 * to instantiate any driver.
 */
sealed class Feature {
    /** Device has a color e-ink display. */
    data object ColorScreen : Feature()

    /**
     * Device has a controllable front-light.
     *
     * @param warmth           Whether a color-temperature (warmth) channel is present.
     * @param standaloneWarmth Whether warmth can be adjusted independently of brightness.
     */
    data class FrontLight(
        val warmth: Boolean,
        val standaloneWarmth: Boolean = false,
    ) : Feature()
}
