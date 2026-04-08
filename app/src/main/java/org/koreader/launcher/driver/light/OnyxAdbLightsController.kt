@file:Suppress("SameParameterValue", "UNUSED_PARAMETER")

package org.koreader.launcher.driver.light

import android.content.Context
import android.util.Log
import org.koreader.launcher.driver.BacklightDriver
import org.koreader.launcher.driver.DriverContext
import java.lang.Class.forName
import java.lang.reflect.Method

class OnyxAdbLightsController(ctx: DriverContext) : BacklightDriver {
    override val platform: String = "onyx-adb-lights"

    private val context: Context = ctx.context

    companion object {
        private const val TAG = "BacklightDriver"
    }

    override val hasWarmth: Boolean = true
    override val maxBrightness: Int get() = FrontLightAdb.getMaxBrightness()
    override val maxWarmth: Int get() = FrontLightAdb.getMaxWarmth()

    override fun getBrightness(): Int = FrontLightAdb.getBrightness()
    override fun getWarmth(): Int = FrontLightAdb.getWarmth()

    override fun setBrightness(brightness: Int) {
        if (brightness !in minBrightness..maxBrightness) {
            Log.w(TAG, "brightness value out of range: $brightness")
            return
        }
        Log.v(TAG, "setBrightness: $brightness")
        FrontLightAdb.setBrightness(brightness)
    }

    override fun setWarmth(warmth: Int) {
        if (warmth !in minWarmth..maxWarmth) {
            Log.w(TAG, "warmth value out of range: $warmth")
            return
        }
        Log.v(TAG, "setWarmth: $warmth")
        FrontLightAdb.setWarmth(warmth)
    }
}

private object FrontLightAdb {
    private const val TAG = "BacklightDriver"

    private val flController: Class<*>? = try {
        forName("android.onyx.hardware.DeviceController")
    } catch (e: Exception) {
        Log.w(TAG, "$e")
        null
    }

    private fun getMethod(name: String, vararg parameterTypes: Class<*>): Method? {
        return try { flController?.getMethod(name, *parameterTypes) }
        catch (e: Exception) { Log.w(TAG, "$e"); null }
    }

    private val setLightValueMethod: Method? = getMethod("setLightValue", Integer.TYPE, Integer.TYPE)
    private val getLightValueMethod: Method? = getMethod("getLightValue", Integer.TYPE)
    private val getMaxLightValueMethod: Method? = getMethod("getMaxLightValue", Integer.TYPE)
    private val checkCTMMethod: Method? = getMethod("checkCTM")

    private fun getMaxLightValue(lightType: Int): Int =
        (getMaxLightValueMethod?.invoke(flController, lightType) as? Int ?: 0).let {
            if (it == 0) 100 else it
        }

    private val type: Int = if (checkCTMMethod?.invoke(flController) as? Boolean == true) 1 else 0

    private val brightnessType: Int = if (type == 1) 7 else 3
    private val warmthType: Int = if (type == 1) 6 else 2
    private val brightnessMax: Int = getMaxLightValue(brightnessType)
    private val warmthMax: Int = getMaxLightValue(warmthType)

    fun getMaxBrightness(): Int = brightnessMax
    fun getMaxWarmth(): Int = warmthMax

    private fun getValue(method: Method?, lightType: Int): Int =
        method?.invoke(flController, lightType) as? Int ?: 0

    private fun setValue(method: Method?, lightType: Int, value: Int) {
        try { method?.invoke(flController, lightType, value) }
        catch (e: Exception) { Log.e(TAG, "Error setting light value", e) }
    }

    fun getWarmth(): Int = getValue(getLightValueMethod, warmthType)
    fun getBrightness(): Int = getValue(getLightValueMethod, brightnessType)
    fun setWarmth(value: Int) = setValue(setLightValueMethod, warmthType, value)
    fun setBrightness(value: Int) = setValue(setLightValueMethod, brightnessType, value)
}
