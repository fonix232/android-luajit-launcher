package org.koreader.launcher.device

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koreader.launcher.device.DeviceCharacterizationTestSupport
import org.koreader.launcher.device.DeviceInfo
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Mooinkplus2cDeviceIdCaseTest {

    @Before
    fun setUpBuild() {
        DeviceCharacterizationTestSupport.setBuildFields(
            manufacturer = "other",
            brand = "allwinner",
            model = "mooink plus 2c",
            device = "other",
            product = "other",
            hardware = "",
        )
    }

    @Test
    fun detects_expected_id() {
        assertThat(DeviceInfo.ID).isEqualTo(DeviceInfo.Id.MOOINKPLUS2C)
    }
}
