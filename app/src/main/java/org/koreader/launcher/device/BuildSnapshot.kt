package org.koreader.launcher.device

import android.os.Build
import android.util.Log
import java.util.Locale

/**
 * Immutable snapshot of the six Android build fields used for device detection,
 * all lowercased for case-insensitive comparison.
 *
 * The singleton [BuildSnapshot.current] is computed once at class-load time from
 * the live [Build] fields. Tests override this by constructing instances directly.
 */
data class BuildSnapshot(
    val manufacturer: String,
    val brand: String,
    val model: String,
    val device: String,
    val product: String,
    val hardware: String,
) {
    companion object {
        private const val TAG = "DeviceInfo"

        /**
         * Reads the current [Build] fields and returns a fresh [BuildSnapshot].
         * Used in [Device] construction so each instance captures the build
         * fields as they are at that moment — important for test correctness.
         */
        fun fromBuild(): BuildSnapshot = BuildSnapshot(
            manufacturer = lowerCase(Build.MANUFACTURER),
            brand        = lowerCase(Build.BRAND),
            model        = lowerCase(Build.MODEL),
            device       = lowerCase(Build.DEVICE),
            product      = lowerCase(Build.PRODUCT),
            hardware     = lowerCase(Build.HARDWARE),
        ).also { s ->
            Log.i(TAG, """
                MANUFACTURER: ${s.manufacturer}
                BRAND       : ${s.brand}
                MODEL       : ${s.model}
                DEVICE      : ${s.device}
                PRODUCT     : ${s.product}
                HARDWARE    : ${s.hardware}
            """.trimIndent())
        }

        /**
         * Singleton snapshot of the build fields at first access. Prefer
         * [fromBuild] when you need accurate results across multiple calls
         * in tests or when build fields may change.
         */
        val current: BuildSnapshot by lazy { fromBuild() }

        private fun lowerCase(s: String) = s.lowercase(Locale.US)
    }
}
