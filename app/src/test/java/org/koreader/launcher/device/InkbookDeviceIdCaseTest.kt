package org.koreader.launcher.device

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koreader.launcher.device.DeviceCharacterizationTestSupport
import org.koreader.launcher.device.DeviceInfo
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InkbookDeviceIdCaseTest {

    @Before
    fun setUpBuild() {
        DeviceCharacterizationTestSupport.setBuildFields(
            manufacturer = "artatech",
            brand = "inkbook",
            model = "prime",
            device = "other",
            product = "other",
            hardware = "",
        )
    }

    @Test
    fun detects_expected_id() {
        assertThat(DeviceInfo.ID).isEqualTo(DeviceInfo.Id.INKBOOK)
    }
}
