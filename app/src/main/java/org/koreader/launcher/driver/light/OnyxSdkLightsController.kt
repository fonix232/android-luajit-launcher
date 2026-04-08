package org.koreader.launcher.driver.light

import android.content.Context
import android.util.Log
import org.koreader.launcher.driver.BacklightDriver
import org.koreader.launcher.driver.DriverContext
import java.lang.Class.forName
import java.lang.reflect.Method

class OnyxSdkLightsController(ctx: DriverContext) : BacklightDriver {
    override val platform: String = "onyx-sdk-lights"

    private val context: Context = ctx.context

    companion object {
        private const val TAG = "BacklightDriver"
    }

    override val hasWarmth: Boolean = true
    override val maxBrightness: Int = 255
    override val maxWarmth: Int = 255

    override fun getBrightness(): Int = FrontLightOnyx.getCold(context)
    override fun getWarmth(): Int = FrontLightOnyx.getWarm(context)

    override fun setBrightness(brightness: Int) {
        if (brightness !in minBrightness..maxBrightness) {
            Log.w(TAG, "brightness value out of range: $brightness")
            return
        }
        Log.v(TAG, "setBrightness: $brightness")
        FrontLightOnyx.setCold(brightness, context)
    }

    override fun setWarmth(warmth: Int) {
        if (warmth !in minWarmth..maxWarmth) {
            Log.w(TAG, "warmth value out of range: $warmth")
            return
        }
        Log.v(TAG, "setWarmth: $warmth")
        FrontLightOnyx.setWarm(warmth, context)
    }
}

private object FrontLightOnyx {
    private const val TAG = "BacklightDriver"
    private const val BRIGHTNESS_CONFIG_WARM_IDX: Int = 2
    private const val BRIGHTNESS_CONFIG_COLD_IDX: Int = 3

    private val flController: Class<*>? = try {
        forName("android.onyx.hardware.DeviceController")
    } catch (e: Exception) {
        Log.w(TAG, "$e")
        null
    }

    private val setWarmBrightness: Method? = try {
        flController!!.getMethod("setWarmLightDeviceValue", Context::class.java, Integer.TYPE)
    } catch (e: Exception) { Log.w(TAG, "$e"); null }

    private val setColdBrightness: Method? = try {
        flController!!.getMethod("setColdLightDeviceValue", Context::class.java, Integer.TYPE)
    } catch (e: Exception) { Log.w(TAG, "$e"); null }

    private val getCoolWarmBrightness: Method? = try {
        flController!!.getMethod("getBrightnessConfig", Context::class.java, Integer.TYPE)
    } catch (e: Exception) { Log.w(TAG, "$e"); null }

    fun getWarm(context: Context?): Int = try {
        getCoolWarmBrightness!!.invoke(flController!!, context, BRIGHTNESS_CONFIG_WARM_IDX) as Int
    } catch (e: Exception) { e.printStackTrace(); 0 }

    fun getCold(context: Context?): Int = try {
        getCoolWarmBrightness!!.invoke(flController!!, context, BRIGHTNESS_CONFIG_COLD_IDX) as Int
    } catch (e: Exception) { e.printStackTrace(); 0 }

    fun setWarm(value: Int, context: Context?) {
        try { setWarmBrightness!!.invoke(flController!!, context, value) }
        catch (e: Exception) { e.printStackTrace() }
    }

    fun setCold(value: Int, context: Context?) {
        try { setColdBrightness!!.invoke(flController!!, context, value) }
        catch (e: Exception) { e.printStackTrace() }
    }
}
