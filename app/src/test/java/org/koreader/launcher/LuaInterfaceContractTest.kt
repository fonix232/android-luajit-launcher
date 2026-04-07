package org.koreader.launcher

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LuaInterfaceContractTest {

    @Test
    fun main_activity_still_implements_lua_interface() {
        assertThat(LuaInterface::class.java.isAssignableFrom(MainActivity::class.java)).isTrue()
    }

    @Test
    fun lua_interface_method_surface_is_stable() {
        val signatures = LuaInterface::class.java.declaredMethods
            .map { method ->
                val params = method.parameterTypes.joinToString(",") { it.name }
                "${method.name}($params):${method.returnType.name}"
            }
            .toSet()

        assertThat(signatures).containsExactlyElementsIn(
            setOf(
                "canIgnoreBatteryOptimizations():boolean",
                "canWriteSystemSettings():boolean",
                "dictLookup(java.lang.String,java.lang.String,java.lang.String):void",
                "download(java.lang.String,java.lang.String):int",
                "dumpLogs():void",
                "einkUpdate(int):void",
                "einkUpdate(int,long,int,int,int,int):void",
                "enableFrontlightSwitch():boolean",
                "extractAssets():boolean",
                "getBatteryLevel():int",
                "getClipboardText():java.lang.String",
                "getDeviceProperties():java.lang.String",
                "getEinkConstants():java.lang.String",
                "getEinkPlatform():java.lang.String",
                "getExternalPath():java.lang.String",
                "getExternalSdPath():java.lang.String",
                "getFilePathFromIntent():java.lang.String",
                "getFlavor():java.lang.String",
                "getLastImportedPath():java.lang.String",
                "getLightDialogState():int",
                "getName():java.lang.String",
                "getNetworkInfo():java.lang.String",
                "getPlatformName():java.lang.String",
                "getScreenAvailableHeight():int",
                "getScreenAvailableWidth():int",
                "getScreenBrightness():int",
                "getScreenHeight():int",
                "getScreenMaxBrightness():int",
                "getScreenMinBrightness():int",
                "getScreenMaxWarmth():int",
                "getScreenMinWarmth():int",
                "getScreenOrientation():int",
                "getScreenWarmth():int",
                "getScreenWidth():int",
                "getStatusBarHeight():int",
                "getVersion():java.lang.String",
                "hasBrokenLifecycle():boolean",
                "hasClipboardText():boolean",
                "hasLights():boolean",
                "hasNativeRotation():boolean",
                "hasOTAUpdates():boolean",
                "hasRuntimeChanges():boolean",
                "hasStandaloneWarmth():boolean",
                "installApk():void",
                "isCharging():boolean",
                "isChromeOS():boolean",
                "isColorScreen():boolean",
                "isDebuggable():boolean",
                "isEink():boolean",
                "isEinkFull():boolean",
                "isFullscreen():boolean",
                "isPackageEnabled(java.lang.String):boolean",
                "isPathInsideSandbox(java.lang.String):boolean",
                "isActivityResumed():boolean",
                "isTv():boolean",
                "isWarmthDevice():boolean",
                "needsWakelocks():boolean",
                "openLink(java.lang.String):boolean",
                "openWifiSettings():void",
                "performHapticFeedback(int,int):void",
                "requestIgnoreBatteryOptimizations(java.lang.String,java.lang.String,java.lang.String):void",
                "requestWriteSystemSettings(java.lang.String,java.lang.String,java.lang.String):void",
                "safFilePicker(java.lang.String):boolean",
                "sendText(java.lang.String,java.lang.String,java.lang.String,java.lang.String):void",
                "setFullscreen(boolean):void",
                "setClipboardText(java.lang.String):void",
                "setIgnoreInput(boolean):void",
                "setScreenBrightness(int):void",
                "setScreenWarmth(int):void",
                "setScreenOffTimeout(int):void",
                "setScreenOrientation(int):void",
                "startTestActivity():void",
                "showFrontlightDialog(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String):void",
                "showToast(java.lang.String,boolean):void",
            ),
        )
    }
}