package org.koreader.launcher.device

import android.app.Activity
import android.os.Build
import org.robolectric.Robolectric
import org.robolectric.util.ReflectionHelpers

internal object DeviceCharacterizationTestSupport {
    fun setBuildFields(
        manufacturer: String,
        brand: String,
        model: String,
        device: String,
        product: String,
        hardware: String,
    ) {
        ReflectionHelpers.setStaticField(Build::class.java, "MANUFACTURER", manufacturer)
        ReflectionHelpers.setStaticField(Build::class.java, "BRAND", brand)
        ReflectionHelpers.setStaticField(Build::class.java, "MODEL", model)
        ReflectionHelpers.setStaticField(Build::class.java, "DEVICE", device)
        ReflectionHelpers.setStaticField(Build::class.java, "PRODUCT", product)
        ReflectionHelpers.setStaticField(Build::class.java, "HARDWARE", hardware)
    }

    fun buildActivity(): Activity {
        return Robolectric.buildActivity(Activity::class.java).setup().get()
    }
}