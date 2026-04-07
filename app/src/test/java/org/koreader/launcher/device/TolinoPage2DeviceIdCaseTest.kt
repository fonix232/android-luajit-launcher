package org.koreader.launcher.device

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koreader.launcher.device.DeviceCharacterizationTestSupport
import org.koreader.launcher.device.DeviceInfo
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TolinoPage2DeviceIdCaseTest {

    @Before
    fun setUpBuild() {
        DeviceCharacterizationTestSupport.setBuildFields(
            manufacturer = "other",
            brand = "rakutenkobo",
            model = "tolino",
            device = "ntx_6sl",
            product = "other",
            hardware = "e60qv0",
        )
    }

    @Test
    fun detects_expected_id() {
        assertThat(DeviceInfo.ID).isEqualTo(DeviceInfo.Id.TOLINO_PAGE2)
    }
}
