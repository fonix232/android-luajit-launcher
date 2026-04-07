# KOReader Android Launcher — Refactor Plan

> Current branch: `master`. All phases target zero external behaviour change unless explicitly noted.
> Phases are designed to be executed in order; each phase leaves the build green before the next begins.

---

## Diagnosis: What's Wrong

### Device Management
The current system has **three-way scattered indirection**. To add or modify one device you must touch three separate files and keep them in sync:

1. `DeviceInfo.kt` — detects which device you're on via `Build.*` fields and assigns a global `Id` enum value; also sets four global quirk booleans in `init`.  
2. `EPDFactory.kt` — a second giant `when(DeviceInfo.ID)` block that maps the id to an EPD controller instance.  
3. `LightsFactory.kt` — a third giant `when(DeviceInfo.ID)` block that maps the id to a lights controller instance.  
4. `Device.kt` — aggregates the outputs of all three; is the only public-facing API.

Problems:
- 130+ flat `Id` entries with no grouping or hierarchy.
- `DeviceInfo` is a global `object` with mutable state assigned in `init` — effectively a global variable cluster.
- A single device's configuration lives in *three unrelated* `when` branches across three files.
- Adding a quirk (e.g. "no haptics") requires editing `DeviceInfo`, then consuming it in `Device`, with no structural enforcement.
- `EPDFactory` and `LightsFactory` silently fall through to defaults for unrecognised IDs, making mis-configurations hard to detect.

### LuaInterface / MainActivity
- `LuaInterface` declares **65 methods** in a single flat interface.
- `MainActivity` implements all 65 methods *directly*, making it a 500-line God class.
- The activity also handles: EPD, lights, clipboard, network, battery, permissions, file picker, SAF, orientation, fullscreen, screen timeout, APK updates, haptics, toasts, external app actions, and test activity launch — with no delegation pattern.
- `ActivityExtensions.kt` adds even more extension functions directly onto `Activity`, further blurring responsibility boundaries.

### Build System
- Groovy Gradle scripts (outdated, no IDE type-checking).
- AGP 8.4.2 / Kotlin 1.9.24 (both well behind current releases).
- `compileSdk`/`targetSdk` = 30 (Android 11 — four major versions behind).
- `minSdk` = 18 (Android 4.3 — zero devices in the wild, prevents modern API use).
- No version catalog (`libs.versions.toml`).
- No Spotless / ktlint formatting enforcement.
- Jetpack Compose not adopted (dialogs and secondary activities still use View system).

---

## Phase 0 — Test Strategy and Regression Harness

*This phase happens before any refactor. The goal is to freeze current behaviour so structural changes can be made safely, especially around device detection and JNI-facing interfaces.*

### 0.1 — Add test infrastructure first

Add the following test layers:

- `test/` JVM unit tests for pure Kotlin logic.
- `androidTest/` instrumentation tests only where Android framework access is unavoidable.
- Robolectric for framework-bound logic that does not need a physical device.
- MockK or Mockito for collaborator isolation.

Recommended baseline:

- JVM tests: JUnit 5, Truth, MockK.
- Robolectric tests: Robolectric + AndroidX Test Core.
- Instrumentation: AndroidX Test Runner, with Espresso only where real UI interaction matters.

### 0.2 — Make device detection testable without touching production code

`DeviceInfo` is a Kotlin `object` that reads `Build.*` fields in its `init` block, effectively a static initializer. There is no seam to inject alternative values at runtime within a single JVM process.

The approach that preserves the zero-production-code-change constraint:

- Use **Robolectric** to get Android framework stubs onto the classpath.
- Override `android.os.Build` fields before `DeviceInfo` initializes using `ReflectionHelpers.setStaticField(Build::class.java, "MANUFACTURER", ...)` (and the same for `BRAND`, `MODEL`, `DEVICE`, `PRODUCT`, `HARDWARE`).
- Because `DeviceInfo` is a singleton that caches its result on first access, each test class must run in an isolated JVM. This is enforced by `forkEvery = 1` in `testOptions.unitTests.all`.

Shared helper in `DeviceCharacterizationTestSupport.kt`:

```kotlin
internal object DeviceCharacterizationTestSupport {
    fun setBuildFields(manufacturer: String, brand: String, model: String,
                       device: String, product: String, hardware: String) {
        ReflectionHelpers.setStaticField(Build::class.java, "MANUFACTURER", manufacturer)
        ReflectionHelpers.setStaticField(Build::class.java, "BRAND",        brand)
        ReflectionHelpers.setStaticField(Build::class.java, "MODEL",        model)
        ReflectionHelpers.setStaticField(Build::class.java, "DEVICE",       device)
        ReflectionHelpers.setStaticField(Build::class.java, "PRODUCT",      product)
        ReflectionHelpers.setStaticField(Build::class.java, "HARDWARE",     hardware)
    }
}
```

This approach requires zero seam extraction and zero production code changes. The same `Build` override technique carries forward into Phase 1 tests.

### 0.3 — Build a golden device matrix

Each `DeviceInfo.Id` value (except `NONE`) maps to a dedicated test class in `app/src/test/java/org/koreader/launcher/device/`. Each class:

- Is annotated `@RunWith(RobolectricTestRunner::class)`.
- Sets the six `Build` fields in `@Before` via `DeviceCharacterizationTestSupport.setBuildFields(...)`.
- Asserts `DeviceInfo.ID == DeviceInfo.Id.<EXPECTED>` in a single `@Test`.

Pattern example:

```kotlin
@RunWith(RobolectricTestRunner::class)
class BoyueT61DeviceIdCaseTest {
    @Before fun setUp() = DeviceCharacterizationTestSupport.setBuildFields(
        manufacturer = "boyue", brand = "boyue",
        model = "t61", device = "t61", product = "t61", hardware = "rk3188"
    )
    @Test fun deviceId() = assertThat(DeviceInfo.ID).isEqualTo(DeviceInfo.Id.BOYUE_T61)
}
```

Coverage is enforced structurally by `DeviceIdCoverageGuardTest`, which asserts that the set of `DeviceInfo.Id` entries (minus `NONE`) equals the set of IDs declared in that guard. If a new enum value is added without a corresponding test class entry, the guard fails.

Special-case fixtures are included for precedence-sensitive entries:
- `BOYUE_T62`: `device=t62` in the `when` chain to distinguish from the rk30sdk fallback.
- `ONYX_DARWIN5`: `brand=maccentre` tests OR-before-AND precedence.
- Nook cascade: `NOOK_GL4 → NOOK_GLPLUS → NOOK` ordering.
- `TOLINO`: generic fallback after all hardware-specific Tolino entries.

### 0.4 — Write characterization tests against current behaviour

Tests implemented in `app/src/test/java/org/koreader/launcher/device/`:

1. **121 × `*DeviceIdCaseTest` classes** (one per `DeviceInfo.Id` except `NONE`)
    Each class runs in a forked JVM, sets `Build` fields, and asserts the resolved `DeviceInfo.ID`.

2. **`DeviceIdCoverageGuardTest`**
    Plain JUnit (no Robolectric needed). Asserts that the declared set of IDs in the guard equals `DeviceInfo.Id.entries.filter { it != NONE }.toSet()`. Fails CI if a new enum value is added without a corresponding test entry.

3. **`LuaInterfaceContractTest`** (in `org.koreader.launcher`)
    Reflection-based; freezes all 65 public method signatures on `LuaInterface`. Acts as the safety net for Phase 2 and Phase 3 interface splits.

4. **`EventReceiverContractTest`** (in `org.koreader.launcher`)
    Robolectric; verifies `EventReceiver` registers the expected broadcast actions.

Note: EPD controller and lights controller mapping tests (`EpdFactorySelectionTest`, `LightsFactorySelectionTest`) and the `DeviceFacadeTest` are deferred to Phase 1, where those factories will be restructured. Adding those tests now would duplicate characterization work that will be discarded.

### 0.5 — Add JNI contract tests

This project has a fragile runtime integration point: Lua calls into Java/Kotlin through JNI by method name. Refactors can compile cleanly while still breaking runtime behaviour.

Add a contract test that asserts:

- `MainActivity` still implements `LuaInterface`.
- Every current `LuaInterface` method name still exists after refactors.
- Overloaded methods keep their signatures.

This becomes the safety net for Phase 2 and Phase 3.

### 0.6 — Cover side-effect-free helpers early

Add unit or Robolectric tests first for logic-heavy helpers that are cheap to verify:

- Orientation translation helpers in `ActivityExtensions.kt`.
- File helpers in `FileExtensions.kt`.
- URI parsing/copy logic in `UriExtensions.kt` where possible.
- `EventReceiver` event-code mapping.
- `Timeout` logic where branch behaviour exists.

These give good regression protection with low setup cost.

### 0.7 — Add a minimal instrumentation smoke suite

Do not attempt to CI-test hardware-specific EPD or frontlight behaviour in full. That should remain manual or device-lab validation. Instead add a minimal smoke suite that verifies app wiring survives startup:

- `MainActivity` launches.
- `TestActivity` launches.
- `CrashReportActivity` launches.
- Permission-related flows do not crash when invoked in a test environment.

This suite is for lifecycle and wiring regressions, not hardware correctness.

### 0.8 — Define the TDD workflow for later phases

For every later sub-task, use this sequence:

1. Add or update a characterization or contract test for the behaviour being preserved.
2. Make the structural refactor.
3. Run the smallest relevant unit test set.
4. Run the smallest relevant instrumentation subset.
5. Only then continue to the next sub-step.

For this codebase, early refactoring should prefer characterization testing over idealized greenfield TDD, because much of the behaviour is undocumented and hardware-specific.

### 0.9 — CI gate before major refactors

The unit test suite is green and runs via:

```
./gradlew testArm64FdroidDebugUnitTest
```

(The concrete flavor-specific task is required because the project uses ABI × CHANNEL flavor dimensions; the generic `test` task is ambiguous.)

CI is enforced by `.github/workflows/unit-tests.yml`, which runs on every PR (and can be triggered manually). It uses the standard `ubuntu-latest` runner — no custom Docker image is needed because Robolectric runs entirely on the JVM. The workflow is intended to be a **required status check** on the default branch.

Instrumentation tests (emulator) are deferred. The eventual split:

- **Required on every PR**: unit tests + Robolectric (the workflow above).
- **Scheduled/nightly**: instrumentation smoke suite once 0.7 is implemented.

---

## Phase 1 — Device Management Refactor

*This is a pure black-box refactor. The public API of `Device.kt` must remain identical.*

### 1.1 — Create `DeviceQuirks` and `DeviceProfile` data classes

**New file: `device/DeviceProfile.kt`**

```kotlin
data class DeviceQuirks(
    val brokenLifecycle: Boolean = false,
    val needsWakelocks: Boolean = false,
    val noLights: Boolean = false,
)

data class DeviceProfile(
    val id: DeviceId,                       // replaces DeviceInfo.Id
    val epdController: EPDInterface,
    val lightsController: LightsInterface,
    val hasColorScreen: Boolean = false,
    val quirks: DeviceQuirks = DeviceQuirks(),
) {
    companion object {
        /** Returned for any unrecognised/generic Android device. */
        fun unknown() = DeviceProfile(
            id = DeviceId.NONE,
            epdController = NoOpEPDController(),
            lightsController = GenericController(),
        )
    }
}
```

`DeviceId` is the existing `DeviceInfo.Id` enum moved to its own file with the same values — a pure mechanical rename to decouple it from the old singleton.

### 1.2 — Create `BuildProperties` (replaces the property-reading side of `DeviceInfo`)

**New file: `device/BuildProperties.kt`**

A plain `object` that reads and lowercases `Build.*` fields exactly as `DeviceInfo.init` does today. No state-mutation; all properties are `val` computed at class-load time. Logging of the six fields stays here.

```kotlin
internal object BuildProperties {
    val manufacturer: String = lowerCase(Build.MANUFACTURER)
    val brand: String        = lowerCase(Build.BRAND)
    val model: String        = lowerCase(Build.MODEL)
    val device: String       = lowerCase(Build.DEVICE)
    val product: String      = lowerCase(Build.PRODUCT)
    val hardware: String     = lowerCase(Build.HARDWARE)

    /** Human-readable summary for `Device.properties` — same format as before. */
    val summary: String get() = "$manufacturer;$brand;$model;$device;$product;$hardware"
}
```

### 1.3 — Create `DeviceRegistry`

**New file: `device/DeviceRegistry.kt`**

This is the heart of the refactor. It replaces `DeviceInfo`'s detection logic, `EPDFactory`, and `LightsFactory` in a **single place**.

Each entry in the registry is a `DeviceEntry`:

```kotlin
private class DeviceEntry(
    val id: DeviceId,
    val match: BuildProperties.() -> Boolean,   // predicate evaluated against build props
    val epd: () -> EPDInterface,
    val lights: () -> LightsInterface,
    val hasColorScreen: Boolean = false,
    val quirks: DeviceQuirks = DeviceQuirks(),
)
```

`DeviceRegistry.detect()` iterates the list, calls `match()` on `BuildProperties`, and on first hit constructs and returns a `DeviceProfile`. If no entry matches, it returns `DeviceProfile.unknown()`.

Example entries (illustrating the consolidated format):

```kotlin
// ---- Boyue ----
DeviceEntry(
    id = DeviceId.BOYUE_T61,
    match = { isBoyue && (product.startsWith("t61") || model == "rk30sdk") && device.startsWith("t61") },
    epd = ::RK3026EPDController,
    lights = ::GenericController,
),
DeviceEntry(
    id = DeviceId.BOYUE_C64P,
    match = { brand == "c64p" && product == "c64p" },
    epd = ::RK3368EPDController,
    lights = ::GenericController,
),

// ---- Onyx Poke 2 (broken lifecycle quirk) ----
DeviceEntry(
    id = DeviceId.ONYX_POKE2,
    match = { manufacturer == "onyx" && product == "poke2" },
    epd = ::OnyxEPDController,
    lights = ::OnyxWarmthController,
    quirks = DeviceQuirks(brokenLifecycle = true),
),

// ---- Sony RP1 (needs wakelocks, no lights) ----
DeviceEntry(
    id = DeviceId.SONY_RP1,
    match = { manufacturer == "sony" && model == "dpt-rp1" },
    epd = ::NookEPDController,
    lights = ::GenericController,
    quirks = DeviceQuirks(needsWakelocks = true, noLights = true),
),

// ---- Onyx Nova 3 Color (color screen) ----
DeviceEntry(
    id = DeviceId.ONYX_NOVA3_COLOR,
    match = { manufacturer == "onyx" && model == "nova3color" },
    epd = ::OnyxEPDController,
    lights = ::OnyxWarmthController,
    hasColorScreen = true,
),
```

Every device's full specification now lives in **one entry** — no cross-file synchronisation.

### 1.4 — Refactor `Device.kt`

`Device` calls `DeviceRegistry.detect()` once in its constructor and holds the resulting `DeviceProfile`. All properties are derived from the profile:

```kotlin
class Device(activity: Activity) {
    private val profile: DeviceProfile = DeviceRegistry.detect()

    val epd: EPDInterface     = profile.epdController
    val lights: LightsInterface = profile.lightsController

    val product: String       = BuildProperties.product
    val hasColorScreen: Boolean = profile.hasColorScreen
    val needsWakelocks: Boolean = profile.quirks.needsWakelocks
    val bugLifecycle: Boolean   = profile.quirks.brokenLifecycle

    val hasEinkSupport: Boolean  = epd.getPlatform() != "none"
    val hasFullEinkSupport: Boolean = epd.getMode() == "all"

    val hasLights: Boolean = when (activity.platform) {
        "android" -> !profile.quirks.noLights
        else -> false
    }
    val needsView: Boolean = when (activity.platform) {
        "android_tv", "chrome" -> true
        else -> epd.needsView()
    }
    val einkPlatform: String = epd.getPlatform()
    val properties: String   = BuildProperties.summary
}
```

The public API is **byte-for-byte identical** to the current `Device.kt` — `MainActivity` requires zero changes.

### 1.5 — Delete legacy files

Once all usages compile against the new structure, delete:
- `device/DeviceInfo.kt`
- `device/EPDFactory.kt`
- `device/LightsFactory.kt`

`DeviceId.kt` (new) contains the renamed enum. `BuildProperties.kt` replaces the build-field-reading side of `DeviceInfo`.

### 1.6 — Validation

- Run `./gradlew assembleDebug` — must succeed with zero errors.
- Run `./gradlew detekt` — address any new findings.
- Manual smoke test: `TestActivity` EPD/lights tests exercise the controllers.
- Verify log output: `DeviceRegistry` should log the matched device id and controller names at `INFO` level (same information as today, just from one place).

---

## Phase 2 — Split `LuaInterface` into Domain Sub-Interfaces

*Zero behaviour change. The JNI layer calls methods by name on the implementing object; splitting the interface does not affect this.*

### 2.1 — Identify domains

| Sub-interface | Methods |
|---|---|
| `EinkLuaInterface` | `einkUpdate(mode)`, `einkUpdate(mode,delay,x,y,w,h)`, `getEinkConstants`, `getEinkPlatform`, `isEink`, `isEinkFull` |
| `LightsLuaInterface` | `enableFrontlightSwitch`, `getScreenBrightness/Warmth`, `getScreenMax/MinBrightness/Warmth`, `hasLights`, `hasStandaloneWarmth`, `isWarmthDevice`, `setScreenBrightness/Warmth`, `showFrontlightDialog`, `getLightDialogState` |
| `ScreenLuaInterface` | `getScreenAvailableHeight/Width`, `getScreenHeight/Width`, `getScreenOrientation`, `getStatusBarHeight`, `hasNativeRotation`, `isFullscreen`, `setFullscreen`, `setIgnoreInput`, `setScreenOrientation`, `performHapticFeedback` |
| `SystemLuaInterface` | `getBatteryLevel`, `getDeviceProperties`, `getFlavor`, `getName`, `getPlatformName`, `getVersion`, `hasBrokenLifecycle`, `hasOTAUpdates`, `hasRuntimeChanges`, `isActivityResumed`, `isCharging`, `isChromeOS`, `isColorScreen`, `isDebuggable`, `isTv`, `needsWakelocks`, `setScreenOffTimeout`, `dumpLogs` |
| `ClipboardLuaInterface` | `getClipboardText`, `hasClipboardText`, `setClipboardText` |
| `NetworkLuaInterface` | `getNetworkInfo`, `openLink`, `openWifiSettings` |
| `StorageLuaInterface` | `extractAssets`, `getExternalPath`, `getExternalSdPath`, `getFilePathFromIntent`, `getLastImportedPath`, `isPathInsideSandbox`, `safFilePicker` |
| `PermissionLuaInterface` | `canIgnoreBatteryOptimizations`, `canWriteSystemSettings`, `requestIgnoreBatteryOptimizations`, `requestWriteSystemSettings` |
| `AppLuaInterface` | `dictLookup`, `download`, `installApk`, `isPackageEnabled`, `sendText`, `showToast`, `startTestActivity` |

### 2.2 — Create sub-interface files

Each sub-interface lives in `launcher/lua/` (new package to avoid cluttering the root launcher package):

```
launcher/lua/EinkLuaInterface.kt
launcher/lua/LightsLuaInterface.kt
launcher/lua/ScreenLuaInterface.kt
launcher/lua/SystemLuaInterface.kt
launcher/lua/ClipboardLuaInterface.kt
launcher/lua/NetworkLuaInterface.kt
launcher/lua/StorageLuaInterface.kt
launcher/lua/PermissionLuaInterface.kt
launcher/lua/AppLuaInterface.kt
```

### 2.3 — Update `LuaInterface` to be a composed interface

```kotlin
// launcher/LuaInterface.kt
@WorkerThread
interface LuaInterface :
    EinkLuaInterface,
    LightsLuaInterface,
    ScreenLuaInterface,
    SystemLuaInterface,
    ClipboardLuaInterface,
    NetworkLuaInterface,
    StorageLuaInterface,
    PermissionLuaInterface,
    AppLuaInterface
```

`MainActivity` still `implements LuaInterface` — no change to the JNI bridge or to any call-site.

### 2.4 — Add `@WorkerThread` to sub-interfaces

Each sub-interface inherits `@WorkerThread` from the composed `LuaInterface`. This is already the implied contract; making it explicit per-interface allows Lint to enforce it for each domain independently.

---

## Phase 3 — Decompose `MainActivity` into Delegate Managers

*Still zero external behaviour change. Each step moves a cohesive group of methods out of `MainActivity` into a dedicated class, then replaces the implementations with one-line delegations.*

### 3.1 — Create `EinkManager`

**`launcher/manager/EinkManager.kt`**

Owns a reference to `device.epd` and `view` (passed in). Implements `EinkLuaInterface`:

```kotlin
class EinkManager(
    private val epd: EPDInterface,
    private val viewProvider: () -> View?,
) : EinkLuaInterface {
    override fun einkUpdate(mode: Int) { ... }
    override fun einkUpdate(mode: Int, delay: Long, x: Int, y: Int, width: Int, height: Int) { ... }
    override fun getEinkConstants(): String { ... }
    override fun getEinkPlatform(): String = epd.getPlatform()
    override fun isEink(): Boolean = epd.getPlatform() != "none"
    override fun isEinkFull(): Boolean = epd.getMode() == "all"
}
```

`MainActivity` delegates:
```kotlin
private lateinit var einkManager: EinkManager

override fun einkUpdate(mode: Int) = einkManager.einkUpdate(mode)
// etc.
```

### 3.2 — Create `LightsManager`

**`launcher/manager/LightsManager.kt`**

Wraps `device.lights` and `LightDialog`. Implements `LightsLuaInterface`. Receives an `Activity` reference via a lambda (avoids holding a strong reference beyond call scope).

### 3.3 — Create `ScreenManager`

**`launcher/manager/ScreenManager.kt`**

Owns the orientation/fullscreen logic currently in `ActivityExtensions.kt` and `MainActivity`. Implements `ScreenLuaInterface`. Constructor takes `window`, a `screenIsLandscape` flag, and a `topInsetHeight` provider.

This is also where `setFullscreenLayout()` moves — it is called from `onWindowFocusChanged` and can be exposed as an internal function the activity calls.

### 3.4 — Create `BatteryHelper`

**`launcher/manager/BatteryHelper.kt`**

A plain Kotlin `object` with a single function: `getState(context, isPercent): Int`. Takes the `IntentFilter` constant with it. No lifecycle dependency.

### 3.5 — Create `ClipboardHelper`

**`launcher/manager/ClipboardHelper.kt`**

Pure functions operating on a `Context`. The `CountDownLatch`/`Box` pattern for reading clipboard from a background thread stays here. Implements `ClipboardLuaInterface`.

### 3.6 — Create `NetworkHelper`

**`launcher/manager/NetworkHelper.kt`**

Moves `networkInfo()`, `openWifi()`, and `openLink()` logic out of `ActivityExtensions.kt` and `MainActivity`. Implements `NetworkLuaInterface`.

### 3.7 — Create `StorageHelper`

**`launcher/manager/StorageHelper.kt`**

Holds `Assets` and `ApkUpdater` references. Manages `lastImportedPath`, `safFilePicker`, `onActivityResult` logic. Implements `StorageLuaInterface`.

This is a stateful class (holds `lastImportedPath`) — instantiated in `onCreate`, not a singleton.

### 3.8 — Create `PermissionHelper`

**`launcher/manager/PermissionHelper.kt`**

Extracts `hasMandatoryPermissions`, `requestMandatoryPermissions`, `canIgnoreBatteryOptimizations`, `canWriteSystemSettings`, `requestIgnoreBatteryOptimizations`, `requestWriteSystemSettings`, and `requestSpecialPermission`. Implements `PermissionLuaInterface`.

### 3.9 — Create `AppHelper`

**`launcher/manager/AppHelper.kt`**

Moves dictionary/send/share/external-app actions out of `ActivityExtensions.kt` into a typed class. Implements `AppLuaInterface`. Holds `ApkUpdater` (shared with `StorageHelper` via DI in `onCreate`).

### 3.10 — Slim down `ActivityExtensions.kt`

After the above, `ActivityExtensions.kt` should only contain:
- `val Activity.platform: String` (used in `Device`)
- `fun Activity.hapticFeedback(...)` (if not moved to `ScreenManager`)
- `fun Activity.getSdcardPath(): String?`

Everything else has been absorbed by the manager classes. The extension file becomes a small set of genuinely Activity-scoped utilities.

### 3.11 — Result: `MainActivity` target shape

```kotlin
class MainActivity : NativeActivity(), LuaInterface,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var device: Device
    private lateinit var timeout: Timeout
    private lateinit var event: EventReceiver

    // Managers
    private lateinit var eink: EinkManager
    private lateinit var lights: LightsManager
    private lateinit var screen: ScreenManager
    private lateinit var battery: BatteryHelper
    private lateinit var clipboard: ClipboardHelper
    private lateinit var network: NetworkHelper
    private lateinit var storage: StorageHelper
    private lateinit var permissions: PermissionHelper
    private lateinit var app: AppHelper

    // Lifecycle (onCreate/onResume/onPause/onDestroy/onActivityResult/etc.)
    // ~ 80 lines

    // LuaInterface delegation
    override fun einkUpdate(mode: Int) = eink.einkUpdate(mode)
    // ~ 65 one-line delegations
}
```

Target: MainActivity under 200 lines, fully delegating, no business logic inline.

---

## Phase 4 — Build System Modernisation

*This phase does not change any Kotlin source files — only build infrastructure.*

### 4.1 — Migrate Gradle scripts to Kotlin DSL (`.gradle.kts`)

Rename and convert:
- `settings.gradle` → `settings.gradle.kts`  
- `build.gradle` → `build.gradle.kts`  
- `app/build.gradle` → `app/build.gradle.kts`  

Key changes:
- Replace `apply plugin: '...'` with `plugins { }` block (already partially done).
- Replace `ext.foo = bar` with `val foo by extra(bar)` or move to version catalog.
- Change string method calls to named parameters and type-safe DSL (e.g. `buildTypes { release { isMinifyEnabled = true } }`).

### 4.2 — Add Version Catalog (`gradle/libs.versions.toml`)

```toml
[versions]
agp              = "8.9.1"
kotlin           = "2.1.20"
compileSdk       = "35"
targetSdk        = "35"
minSdk           = "21"
androidxCore     = "1.16.0"
androidxAppcompat = "1.7.0"
composeBom       = "2025.04.00"
detekt           = "1.23.8"
spotless         = "7.0.3"

[libraries]
androidx-core            = { group = "androidx.core",         name = "core-ktx",           version.ref = "androidxCore" }
androidx-appcompat       = { group = "androidx.appcompat",    name = "appcompat",           version.ref = "androidxAppcompat" }
compose-bom              = { group = "androidx.compose",      name = "compose-bom",         version.ref = "composeBom" }
compose-ui               = { group = "androidx.compose.ui",   name = "ui" }
compose-ui-tooling       = { group = "androidx.compose.ui",   name = "ui-tooling-preview" }
compose-material3        = { group = "androidx.compose.material3", name = "material3" }
compose-activity         = { group = "androidx.activity",     name = "activity-compose",    version = "1.10.1" }

[plugins]
android-application = { id = "com.android.application",          version.ref = "agp" }
kotlin-android      = { id = "org.jetbrains.kotlin.android",      version.ref = "kotlin" }
kotlin-compose      = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
detekt              = { id = "io.gitlab.arturbosch.detekt",        version.ref = "detekt" }
spotless            = { id = "com.diffplug.spotless",              version.ref = "spotless" }
```

All version strings move here; `build.gradle.kts` and `app/build.gradle.kts` reference `libs.*`.

### 4.3 — Bump SDK versions and Kotlin

| Field | Before | After |
|---|---|---|
| `compileSdk` | 30 | 35 |
| `targetSdk` | 30 | 35 |
| `minSdk` | 18 | 21 |
| AGP | 8.4.2 | 8.9.1 |
| Kotlin | 1.9.24 | 2.1.20 |

`minSdk` bump from 18 → 21: Android 4.3 is zero market share on e-readers. This unlocks `Os.symlink` (`LOLLIPOP`+, used in `FileExtensions.kt`) unconditionally and allows removing the `@SuppressLint("DiscouragedPrivateApi")` reflection fallback.

Remove `@Suppress("NewApi")` annotations that were guarding API 23+ calls guarded by `isAtLeastApi(M)` checks — these become unconditional.

### 4.4 — Add Spotless (ktlint)

Root `build.gradle.kts`:

```kotlin
spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**")
        ktlint("1.5.0").editorConfigOverride(mapOf(
            "max_line_length" to "120",
            "indent_size" to "4",
        ))
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint("1.5.0")
    }
}
```

Run `./gradlew spotlessApply` once to reformat all existing files, then add `spotlessCheck` to the CI `check` task.

### 4.5 — Update and integrate Detekt

- Upgrade Detekt plugin to version in catalog.
- Extend `detekt.yml` to cover new packages (`launcher/lua/`, `launcher/manager/`, `device/`).
- Remove the blanket `@Suppress("detekt:all")` on `DeviceInfo`/`DeviceRegistry` — now that the logic is structured, individual rules can be tuned or suppressed with precision.
- Wire `detekt` into `check`: `tasks.check { dependsOn(tasks.detekt) }`.

### 4.6 — Jetpack Compose (scoped adoption)

`MainActivity` is a `NativeActivity` — the native surface owns the window, making Compose incompatible there. Compose is adopted in supporting UI surfaces only:

**`CrashReportActivity`** — Replace the current `crash_report.xml` layout with a Compose `Scaffold` + `LazyColumn` for the log, `Button` row for actions. Delete `crash_report.xml`.

**`TestActivity`** — Rewrite with a Compose `Column` of `Button` and `Text` widgets. Delete `test.xml`.

**`LightDialog`** — Replace the `AlertDialog(Builder)` + `SeekBar` XML approach with a `composable` `AlertDialog` using `Slider` from Material3. `LightDialog.show()` becomes `LightDialog.showCompose(activity, ...)` calling `setContent { ... }` on a `ComposeView` or via a `DialogFragment`.

`app/build.gradle.kts` additions:
```kotlin
buildFeatures {
    compose = true
}
composeOptions {
    // With kotlin.plugin.compose applied, this is inferred from the Kotlin version
}
dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.activity)
    debugImplementation(libs.compose.ui.tooling)
}
```

---

## Execution Order Summary

| # | Phase | Scope | Risk |
|---|---|---|---|
| 0 | Test strategy / TDD harness | `test/`, `androidTest/`, contract tests | Low |
| 1 | Device registry refactor | `device/` package | Low — zero external API change |
| 2 | `LuaInterface` sub-interfaces | New `lua/` package | Very low — pure interface split |
| 3.1–3.9 | Manager extraction (one manager at a time) | `manager/` package + `MainActivity` | Low — one domain at a time, compile-verified |
| 3.10 | Slim `ActivityExtensions.kt` | `extensions/` | Low — after all managers absorbed their logic |
| 4.1 | Groovy → KTS migration | Build files only | Medium — syntax change, no logic change |
| 4.2 | Version catalog | Build files only | Low — mechanical extraction |
| 4.3 | SDK/AGP/Kotlin bump | Build files + minor source | Medium — may surface new lint/deprecation warnings |
| 4.4 | Spotless | All `.kt` files | Very low — formatting only |
| 4.5 | Detekt update | Config + CI | Low |
| 4.6 | Compose adoption | 3 secondary activities/dialogs | Low — isolated, no NativeActivity involvement |

---

## Files Created / Deleted (summary)

### Created
```
app/src/test/...
app/src/androidTest/...
device/BuildProperties.kt
device/DeviceId.kt              (renamed from DeviceInfo.Id)
device/DeviceQuirks.kt
device/DeviceProfile.kt
device/DeviceRegistry.kt
launcher/lua/EinkLuaInterface.kt
launcher/lua/LightsLuaInterface.kt
launcher/lua/ScreenLuaInterface.kt
launcher/lua/SystemLuaInterface.kt
launcher/lua/ClipboardLuaInterface.kt
launcher/lua/NetworkLuaInterface.kt
launcher/lua/StorageLuaInterface.kt
launcher/lua/PermissionLuaInterface.kt
launcher/lua/AppLuaInterface.kt
launcher/manager/EinkManager.kt
launcher/manager/LightsManager.kt
launcher/manager/ScreenManager.kt
launcher/manager/BatteryHelper.kt
launcher/manager/ClipboardHelper.kt
launcher/manager/NetworkHelper.kt
launcher/manager/StorageHelper.kt
launcher/manager/PermissionHelper.kt
launcher/manager/AppHelper.kt
gradle/libs.versions.toml
build.gradle.kts
app/build.gradle.kts
settings.gradle.kts
```

### Deleted
```
device/DeviceInfo.kt
device/EPDFactory.kt
device/LightsFactory.kt
build.gradle
app/build.gradle
settings.gradle
app/src/main/res/layout/crash_report.xml   (replaced by Compose)
app/src/main/res/layout/test.xml           (replaced by Compose)
```

### Significantly shrunk
```
device/Device.kt               (delegating shell only)
launcher/LuaInterface.kt       (composed interface, no method bodies)
launcher/MainActivity.kt       (lifecycle + 65 one-line delegations, ~200 lines total)
extensions/ActivityExtensions.kt  (3-4 genuine Activity utilities remain)
```
