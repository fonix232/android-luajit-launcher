package org.koreader.launcher.device

import org.koreader.launcher.driver.BacklightDriver
import org.koreader.launcher.driver.DriverContext
import org.koreader.launcher.driver.EpdDriver
import org.koreader.launcher.driver.epd.*
import org.koreader.launcher.driver.epd.common.FakeEPDController
import org.koreader.launcher.driver.light.*

/**
 * Static registry of all known device descriptors.
 *
 * [detect] walks the list in declaration order and returns the first entry whose
 * [BuildMatch] matches the live [BuildSnapshot]. Precedence is therefore explicit
 * and auditable. If no entry matches, [unknown] is returned.
 */
object DeviceRegistry {

    // -------------------------------------------------------------------------
    // Known build field values
    // -------------------------------------------------------------------------

    private const val MFR_BOYUE       = "boyue"
    private const val MFR_ONYX        = "onyx"
    private const val MFR_SONY        = "sony"
    private const val MFR_HYREAD      = "hyread"
    private const val MFR_HAOQING     = "haoqing"   // Meebook
    private const val MFR_ROCKCHIP    = "rockchip"
    private const val MFR_STORYTEL    = "storytel"
    private const val MFR_XIAOMI      = "xiaomi"

    private const val BRAND_ONYX      = "onyx"
    private const val BRAND_CREMA     = "crema"
    private const val BRAND_TOLINO    = "rakutenkobo" // Tolino devices report this Kobo brand
    private const val BRAND_ROCKCHIP  = "rockchip"
    private const val BRAND_ALLWINNER = "allwinner"
    private const val BRAND_FREESCALE = "freescale"
    private const val BRAND_HISENSE   = "hisense"
    private const val BRAND_RIDI      = "ridi"
    private const val BRAND_XIAOMI    = "xiaomi"

    private const val HW_RK30BOARD       = "rk30board"
    private const val MFR_BARNESANDNOBLE  = "barnesandnoble"

    // -------------------------------------------------------------------------
    // Feature shorthands — keeps entry definitions concise
    // -------------------------------------------------------------------------

    private val FL_NONE      = emptySet<Feature>()
    private val FL_BASIC     = setOf(Feature.FrontLight(warmth = false))
    private val FL_WARMTH    = setOf(Feature.FrontLight(warmth = true))
    private val FL_WARMTH_SA = setOf(Feature.FrontLight(warmth = true, standaloneWarmth = true))
    private val FL_COLOR     = setOf(Feature.ColorScreen)
    private val FL_COLOR_FL  = setOf(Feature.ColorScreen, Feature.FrontLight(warmth = false))

    // Quirk shorthands
    private val Q_BROKEN     = setOf(Quirk.BrokenLifecycle)
    private val Q_WAKE       = setOf(Quirk.NeedsWakelocks)
    private val Q_NOLIGHTS   = setOf(Quirk.NoLights)
    private val Q_WAKE_NOLIGHTS = setOf(Quirk.NeedsWakelocks, Quirk.NoLights)

    // -------------------------------------------------------------------------
    // Registry
    // -------------------------------------------------------------------------

    private val entries: List<DeviceDescriptor> = listOf(

        // ---- Boyue ----

        // Boyue C64P (Boyue P6 Clone)
        DeviceDescriptor(
            id       = "BOYUE_C64P",
            match    = BuildMatch(brand = "c64p", product = "c64p"),
            epd      = ::RK3368EPDController,
            lights   = ::GenericController,
        ),

        // Boyue Likebook Ares
        DeviceDescriptor(
            id       = "BOYUE_K78W",
            match    = BuildMatch(
                manufacturer  = MFR_BOYUE,
                productAliases = setOf("k78w", "ares"),
            ),
            epd      = ::RK3368EPDController,
            lights   = ::GenericController,
        ),

        // Boyue Likebook Alita
        DeviceDescriptor(
            id       = "BOYUE_K103",
            match    = BuildMatch(
                manufacturer  = MFR_BOYUE,
                productAliases = setOf("k103", "alita"),
            ),
            epd      = ::RK3368EPDController,
            lights   = ::GenericController,
        ),

        // Boyue Likebook P6
        DeviceDescriptor(
            id       = "BOYUE_P6",
            match    = BuildMatch(manufacturer = MFR_BOYUE, product = "p6"),
            epd      = ::RK3368EPDController,
            lights   = ::GenericController,
        ),

        // Boyue Lemon
        DeviceDescriptor(
            id       = "BOYUE_P61",
            match    = BuildMatch(manufacturer = MFR_BOYUE, product = "p61-k12-l"),
            epd      = ::RK3368EPDController,
            lights   = ::GenericController,
        ),

        // Boyue Likebook P78
        DeviceDescriptor(
            id       = "BOYUE_P78",
            match    = BuildMatch(manufacturer = MFR_BOYUE, product = "p78"),
            epd      = ::RK3368EPDController,
            lights   = ::GenericController,
        ),

        // Boyue Likebook P101
        DeviceDescriptor(
            id       = "BOYUE_P101",
            match    = BuildMatch(manufacturer = MFR_BOYUE, product = "p101"),
            epd      = ::RK3368EPDController,
            lights   = ::GenericController,
        ),

        // Boyue Likebook LemonRead S62A
        DeviceDescriptor(
            id       = "BOYUE_S62",
            match    = BuildMatch(manufacturer = MFR_BOYUE, product = "s62"),
            epd      = ::RK3368EPDController,
            lights   = ::BoyueS62RootController,
            features = FL_BASIC,
        ),

        // Boyue T61
        DeviceDescriptor(
            id       = "BOYUE_T61",
            match    = BuildMatch(
                manufacturer  = MFR_BOYUE,
                productPrefix = "t61",
                devicePrefix  = "t61",
            ),
            epd      = ::RK3026EPDController,
            lights   = ::GenericController,
            features = FL_BASIC,
        ),

        // Boyue T61 (rk30sdk variant)
        DeviceDescriptor(
            id       = "BOYUE_T61",
            match    = BuildMatch(manufacturer = MFR_BOYUE, model = "rk30sdk", devicePrefix = "t61"),
            epd      = ::RK3026EPDController,
            lights   = ::GenericController,
            features = FL_BASIC,
        ),

        // Boyue T62 — must come after T61 to avoid rk30sdk collision
        DeviceDescriptor(
            id       = "BOYUE_T62",
            match    = BuildMatch(manufacturer = MFR_BOYUE, productPrefix = "t62", devicePrefix = "t62"),
            epd      = ::RK3026EPDController,
            lights   = ::GenericController,
        ),

        // Boyue T62 (rk30sdk variant)
        DeviceDescriptor(
            id       = "BOYUE_T62",
            match    = BuildMatch(manufacturer = MFR_BOYUE, model = "rk30sdk", devicePrefix = "t62"),
            epd      = ::RK3026EPDController,
            lights   = ::GenericController,
        ),

        // Boyue T62 (rk30sdk, any manufacturer — preserves prior operator-precedence behaviour)
        DeviceDescriptor(
            id       = "BOYUE_T62",
            match    = BuildMatch(model = "rk30sdk", devicePrefix = "t62"),
            epd      = ::RK3026EPDController,
            lights   = ::GenericController,
        ),

        // Boyue/JDRead T65S
        DeviceDescriptor(
            id       = "BOYUE_T65S",
            match    = BuildMatch(manufacturer = MFR_BOYUE, product = "t65s"),
            epd      = ::NookEPDController,
            lights   = ::GenericController,
        ),

        // Boyue Likebook Muses
        DeviceDescriptor(
            id       = "BOYUE_T78D",
            match    = BuildMatch(manufacturer = MFR_BOYUE, productAliases = setOf("t78d", "muses")),
            epd      = ::RK3368EPDController,
            lights   = ::GenericController,
        ),

        // Boyue Likebook Mars
        DeviceDescriptor(
            id       = "BOYUE_T80D",
            match    = BuildMatch(manufacturer = MFR_BOYUE, productAliases = setOf("t80d", "mars")),
            epd      = ::RK3368EPDController,
            lights   = ::GenericController,
        ),

        // Boyue Likebook Plus
        DeviceDescriptor(
            id       = "BOYUE_T80S",
            match    = BuildMatch(manufacturer = MFR_BOYUE, product = "t80s"),
            epd      = ::RK3026EPDController,
            lights   = ::GenericController,
        ),

        // Boyue Likebook Mimas
        DeviceDescriptor(
            id       = "BOYUE_T103D",
            match    = BuildMatch(manufacturer = MFR_BOYUE, productAliases = setOf("t103d", "mimas")),
            epd      = ::RK3368EPDController,
            lights   = ::GenericController,
        ),

        // ---- Crema ----

        // Crema Note (1010P)
        DeviceDescriptor(
            id       = "CREMA",
            match    = BuildMatch(brand = BRAND_CREMA, product = "note"),
            epd      = ::TolinoEPDController,
            lights   = ::GenericController,
        ),

        // Crema Carta+
        DeviceDescriptor(
            id       = "CREMA_0650L",
            match    = BuildMatch(brand = BRAND_CREMA, product = "keplerb"),
            epd      = ::RK3026EPDController,
            lights   = ::GenericController,
        ),

        // Crema Soundup
        DeviceDescriptor(
            id       = "CREMA_0660L",
            match    = BuildMatch(brand = BRAND_CREMA, product = "sound2"),
            epd      = ::NookEPDController,
            lights   = ::TolinoNtxNoWarmthController,
            features = FL_BASIC,
        ),

        // Crema Grande
        DeviceDescriptor(
            id       = "CREMA_0710C",
            match    = BuildMatch(brand = BRAND_CREMA, model = "crema-0710c"),
            epd      = ::NookEPDController,
            lights   = ::TolinoNtxController,
            features = FL_WARMTH,
        ),

        // Crema Carta G
        DeviceDescriptor(
            id       = "CREMA_CARTA_G",
            match    = BuildMatch(brand = BRAND_CREMA, model = "crema-0670c"),
            epd      = ::TolinoEPDController,
            lights   = ::TolinoNtxController,
            features = FL_WARMTH,
        ),

        // ---- Energy ----

        // Energy Sistem eReaders (tested on Energy Ereader Pro 4)
        DeviceDescriptor(
            id       = "ENERGY",
            match    = BuildMatch(
                brandAliases = setOf("energysistem", "energy_sistem"),
                extra        = { model.startsWith("ereader") },
            ),
            epd      = ::RK3026EPDController,
            lights   = ::GenericController,
        ),

        // ---- Fidibook ----

        DeviceDescriptor(
            id       = "FIDIBOOK",
            match    = BuildMatch(manufacturer = "fidibo", model = "fidibook"),
            epd      = ::RK3026EPDController,
            lights   = ::GenericController,
        ),

        // ---- Hanvon ----

        DeviceDescriptor(
            id       = "HANVON_960",
            match    = BuildMatch(brand = BRAND_FREESCALE, product = "evk_6sl_eink"),
            epd      = ::TolinoEPDController,
            lights   = ::GenericController,
        ),

        // ---- Hisense ----

        DeviceDescriptor(
            id       = "HISENSE_TOUCH_LITE",
            match    = BuildMatch(brand = BRAND_HISENSE, model = "hitv205n"),
            epd      = ::NookEPDController,
            lights   = ::TolinoNtxNoWarmthController,
            features = FL_BASIC,
        ),

        // ---- Hyread ----

        // Hyread Gaze Note
        DeviceDescriptor(
            id       = "HYREAD_GAZE_NOTE",
            match    = BuildMatch(manufacturer = MFR_HYREAD, model = "r08p"),
            epd      = ::RK3368EPDController,
            lights   = ::GenericController,
        ),

        // Hyread Gaze Note Plus CC
        DeviceDescriptor(
            id       = "HYREAD_GAZE_NOTE_CC",
            match    = BuildMatch(manufacturer = MFR_HYREAD, model = "k08cc"),
            epd      = ::RK3566EPDController,
            lights   = ::GenericController,
            features = FL_COLOR,
        ),

        // Hyread Mini 6
        DeviceDescriptor(
            id       = "HYREAD_MINI6",
            match    = BuildMatch(manufacturer = MFR_HYREAD, model = "k06nu"),
            epd      = ::RK3566EPDController,
            lights   = ::GenericController,
        ),

        // ---- Iflytek ----

        DeviceDescriptor(
            id       = "IFLYTEK_R3",
            match    = BuildMatch(manufacturer = "iflytek", model = "iflytek ebook r3"),
            epd      = ::RK3566EPDController,
            lights   = ::GenericController,
            features = FL_COLOR,
        ),

        // ---- InkBook ----

        // Artatech InkBook Prime/Prime HD
        DeviceDescriptor(
            id       = "INKBOOK",
            match    = BuildMatch(
                manufacturer = "artatech",
                brand        = "inkbook",
                extra        = { model.startsWith("prime") },
            ),
            epd      = ::RK3026EPDController,
            lights   = ::GenericController,
        ),

        // InkBook Focus
        DeviceDescriptor(
            id       = "INKBOOKFOCUS",
            match    = BuildMatch(device = "px30_eink", model = "focus"),
            epd      = ::RK3026EPDController,
            lights   = ::GenericController,
        ),

        // InkBook Focus Plus
        DeviceDescriptor(
            id       = "INKBOOKFOCUS_PLUS",
            match    = BuildMatch(device = "rk3566_eink", model = "focus plus"),
            epd      = ::RK3566EPDController,
            lights   = ::GenericController,
        ),

        // ---- InkPalm ----

        DeviceDescriptor(
            id       = "INKPALM_PLUS",
            match    = BuildMatch(manufacturer = MFR_ROCKCHIP, model = "inkpalmplus"),
            epd      = ::RK3566EPDController,
            lights   = ::GenericController,
        ),

        // ---- JDRead ----

        DeviceDescriptor(
            id       = "JDREAD",
            match    = BuildMatch(manufacturer = MFR_ONYX, model = "jdread"),
            epd      = ::NookEPDController,
            lights   = ::GenericController,
        ),

        // ---- Linfiny ----

        // Linfiny A4 (13.3") eNote / Avalue ENT-13T1 / QuirkLogic Papyr
        DeviceDescriptor(
            id       = "LINFINY_ENOTE",
            match    = BuildMatch(manufacturer = "linfiny", model = "ent-13t1"),
            epd      = ::NookEPDController,
            lights   = ::GenericController,
            quirks   = Q_NOLIGHTS,
        ),

        // ---- Meebook ----

        DeviceDescriptor(
            id       = "MEEBOOK_M6",
            match    = BuildMatch(manufacturer = MFR_HAOQING, model = "m6"),
            epd      = ::RK3566EPDController,
            lights   = ::GenericController,
        ),

        DeviceDescriptor(
            id       = "MEEBOOK_M6C",
            match    = BuildMatch(manufacturer = MFR_HAOQING, model = "m6c"),
            epd      = ::RK3566EPDController,
            lights   = ::GenericController,
            features = FL_COLOR,
        ),

        DeviceDescriptor(
            id       = "MEEBOOK_M7",
            match    = BuildMatch(manufacturer = MFR_HAOQING, model = "m7"),
            epd      = ::RK3566EPDController,
            lights   = ::GenericController,
        ),

        DeviceDescriptor(
            id       = "MEEBOOK_P6",
            match    = BuildMatch(manufacturer = MFR_HAOQING, model = "p6"),
            epd      = ::RK3026EPDController,
            lights   = ::TolinoNtxController,
            features = FL_WARMTH,
        ),

        // ---- Moaan ----

        DeviceDescriptor(
            id       = "MOAAN_MIX7",
            match    = BuildMatch(manufacturer = MFR_ROCKCHIP, model = "moaanmix7"),
            epd      = ::RK3566EPDController,
            lights   = ::GenericController,
        ),

        // ---- Mooink ----

        DeviceDescriptor(
            id       = "MOOINKPLUS2C",
            match    = BuildMatch(brand = BRAND_ALLWINNER, model = "mooink plus 2c"),
            epd      = ::NGL4EPDController,
            lights   = ::GenericController,
            features = FL_COLOR,
        ),

        // ---- Nabuk ----

        DeviceDescriptor(
            id       = "NABUK",
            match    = BuildMatch(manufacturer = MFR_ONYX, model = "nabukreg_hd"),
            epd      = ::OldTolinoEPDController,
            lights   = ::GenericController,
        ),

        // ---- Nook ----

        // Nook Glowlight 4 (4/4e/4plus) — before NOOK_GLPLUS / NOOK
        DeviceDescriptor(
            id       = "NOOK_GL4",
            match    = BuildMatch(
                manufacturer   = MFR_BARNESANDNOBLE,
                modelAliases   = setOf("bnrv1000", "bnrv1100", "bnrv1300"),
            ),
            epd      = ::NGL4EPDController,
            lights   = ::TolinoRootController,
            features = FL_WARMTH,
        ),

        // Nook Glowlight Plus 7.8" (2019) — before generic NOOK
        DeviceDescriptor(
            id       = "NOOK_GLPLUS",
            match    = BuildMatch(manufacturer = MFR_BARNESANDNOBLE, model = "bnrv700", product = "ntx_6sl"),
            epd      = ::NookEPDController,
            lights   = ::TolinoNtxController,
            features = FL_WARMTH,
        ),

        // Nook catch-all
        DeviceDescriptor(
            id       = "NOOK",
            match    = BuildMatch(
                manufacturerAliases = setOf(MFR_BARNESANDNOBLE, BRAND_FREESCALE),
                extra = { model in setOf("bnrv510", "bnrv520", "bnrv700", "evk_mx6sl")
                       || model.startsWith("ereader") },
            ),
            epd      = ::NookEPDController,
            lights   = ::GenericController,
        ),

        // ---- OBook ----

        DeviceDescriptor(
            id       = "OBOOK_P10D",
            match    = BuildMatch(manufacturer = MFR_ROCKCHIP, model = "p10d"),
            epd      = ::RK3566EPDController,
            lights   = ::GenericController,
        ),

        DeviceDescriptor(
            id       = "OBOOK_P78D",
            match    = BuildMatch(manufacturer = MFR_ROCKCHIP, product = "rk3566_78d", model = "p78d"),
            epd      = ::RK3566EPDController,
            lights   = ::GenericController,
        ),

        // ---- Onyx ----

        // Onyx C67
        DeviceDescriptor(
            id       = "ONYX_C67",
            match    = BuildMatch(manufacturer = MFR_ONYX, productPrefix = "c67", devicePrefix = "c67"),
            epd      = ::RK3026EPDController,
            lights   = ::OnyxC67Controller,
            features = FL_BASIC,
        ),

        // Onyx C67 (rk30sdk variant)
        DeviceDescriptor(
            id       = "ONYX_C67",
            match    = BuildMatch(manufacturer = MFR_ONYX, model = "rk30sdk", devicePrefix = "c67"),
            epd      = ::RK3026EPDController,
            lights   = ::OnyxC67Controller,
            features = FL_BASIC,
        ),

        // Onyx Darwin 5 — brand=maccentre OR model=mc_c68pctm; must come before generic onyx catch-alls
        DeviceDescriptor(
            id       = "ONYX_DARWIN5",
            match    = BuildMatch(manufacturer = MFR_ONYX, brand = "maccentre"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxSdkLightsController,
            features = FL_WARMTH,
        ),

        DeviceDescriptor(
            id       = "ONYX_DARWIN5",
            match    = BuildMatch(model = "mc_c68pctm"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxSdkLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Darwin 7
        DeviceDescriptor(
            id       = "ONYX_DARWIN7",
            match    = BuildMatch(manufacturer = MFR_ONYX, productAliases = setOf("mc_darwin7", "darwin7"), extra = { device in setOf("mc_darwin7", "darwin7") }),
            epd      = ::OldTolinoEPDController,
            lights   = ::OnyxWarmthController,
            features = FL_WARMTH_SA,
        ),

        // Onyx Darwin 9
        DeviceDescriptor(
            id       = "ONYX_DARWIN9",
            match    = BuildMatch(manufacturer = MFR_ONYX, productAliases = setOf("mc_darwin9", "darwin9"), extra = { device in setOf("mc_darwin9", "darwin9") }),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxSdkLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Edison
        DeviceDescriptor(
            id       = "ONYX_EDISON",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "edison", device = "edison"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxWarmthController,
            features = FL_WARMTH_SA,
        ),

        // Onyx Faust 3
        DeviceDescriptor(
            id       = "ONYX_FAUST3",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "mc_faust3", device = "mc_faust3"),
            epd      = ::OldTolinoEPDController,
            lights   = ::OnyxWarmthController,
            features = FL_WARMTH_SA,
        ),

        // Onyx Galileo 2
        DeviceDescriptor(
            id       = "ONYX_GALILEO2",
            match    = BuildMatch(brand = BRAND_ONYX, model = "galileo2"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Go 10.3
        DeviceDescriptor(
            id       = "ONYX_GO_103",
            match    = BuildMatch(brand = BRAND_ONYX, model = "go103"),
            epd      = ::OnyxEPDController,
            lights   = ::GenericController,
        ),

        // Onyx Go Color 7
        DeviceDescriptor(
            id       = "ONYX_GO_COLOR7",
            match    = BuildMatch(brand = BRAND_ONYX, model = "gocolor7"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_COLOR_FL,
        ),

        // Onyx Go Color 7 Gen II
        DeviceDescriptor(
            id       = "ONYX_GO7GEN2",
            match    = BuildMatch(brand = BRAND_ONYX, model = "gocolor7_2"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_COLOR_FL,
        ),

        // Onyx Go 6
        DeviceDescriptor(
            id       = "ONYX_GO6",
            match    = BuildMatch(brand = BRAND_ONYX, model = "go6"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Go 7
        DeviceDescriptor(
            id       = "ONYX_GO7",
            match    = BuildMatch(brand = BRAND_ONYX, model = "go7"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_WARMTH,
        ),

        // Onyx JDRead (brand=onyx, not manufacturer=onyx — must come after JDREAD)
        DeviceDescriptor(
            id       = "ONYX_JDREAD",
            match    = BuildMatch(brand = BRAND_ONYX, model = "jdread"),
            epd      = ::TolinoEPDController,
            lights   = ::OnyxColorController,
            features = FL_BASIC,
        ),

        // Onyx Kon-Tiki 2
        DeviceDescriptor(
            id       = "ONYX_KON_TIKI2",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "kon_tiki2", device = "kon_tiki2"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxWarmthController,
            features = FL_WARMTH_SA,
        ),

        // Onyx Leaf
        DeviceDescriptor(
            id       = "ONYX_LEAF",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "leaf", device = "leaf"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxWarmthController,
            features = FL_WARMTH_SA,
        ),

        // Onyx Leaf 2 / Leaf 2 Plus
        DeviceDescriptor(
            id       = "ONYX_LEAF2",
            match    = BuildMatch(
                manufacturer   = MFR_ONYX,
                productAliases = setOf("leaf2", "leaf2_p"),
                extra          = { device in setOf("leaf2", "leaf2_p") },
            ),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxSdkLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Livingstone 3
        DeviceDescriptor(
            id       = "ONYX_LIVINGSTONE3",
            match    = BuildMatch(manufacturer = MFR_ONYX, device = "livingstone3"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxSdkLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Lomonosov
        DeviceDescriptor(
            id       = "ONYX_LOMONOSOV",
            match    = BuildMatch(manufacturer = MFR_ONYX, device = "lomonosov"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxWarmthController,
            features = FL_WARMTH_SA,
        ),

        // Onyx MagicBook
        DeviceDescriptor(
            id       = "ONYX_MAGICBOOK",
            match    = BuildMatch(manufacturer = MFR_ONYX, brand = "magicbook"),
            epd      = ::RK3026EPDController,
            lights   = ::OnyxC67Controller,
            features = FL_BASIC,
        ),

        // Onyx Max
        DeviceDescriptor(
            id       = "ONYX_MAX",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "max", device = "max"),
            epd      = ::OnyxEPDController,
            lights   = ::GenericController,
            quirks   = Q_NOLIGHTS,
        ),

        // Onyx Max 2 Pro
        DeviceDescriptor(
            id       = "ONYX_MAX2_PRO",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "max2pro", device = "max2pro"),
            epd      = ::OnyxEPDController,
            lights   = ::GenericController,
            quirks   = Q_NOLIGHTS,
        ),

        // Onyx Montecristo 3
        DeviceDescriptor(
            id       = "ONYX_MONTECRISTO3",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "mc_kepler_c", device = "mc_kepler_c"),
            epd      = ::RK3026EPDController,
            lights   = ::OnyxC67Controller,
            features = FL_BASIC,
        ),

        // Onyx Note
        DeviceDescriptor(
            id       = "ONYX_NOTE",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "note", device = "note"),
            epd      = ::OnyxEPDController,
            lights   = ::GenericController,
            quirks   = Q_NOLIGHTS,
        ),

        // Onyx Note 3
        DeviceDescriptor(
            id       = "ONYX_NOTE3",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "note3", device = "note3"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxWarmthController,
            features = FL_WARMTH_SA,
        ),

        // Onyx Note 4
        DeviceDescriptor(
            id       = "ONYX_NOTE4",
            match    = BuildMatch(manufacturer = MFR_ONYX, model = "mc_note4"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxSdkLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Note 5
        DeviceDescriptor(
            id       = "ONYX_NOTE5",
            match    = BuildMatch(brand = BRAND_ONYX, product = "note5", device = "note5"),
            epd      = ::OnyxEPDController,
            lights   = ::GenericController,
        ),

        // Onyx Note Air
        DeviceDescriptor(
            id       = "ONYX_NOTE_AIR",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "noteair", device = "noteair"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxWarmthController,
            features = FL_WARMTH_SA,
        ),

        // Onyx Note Air 2 / Note Air 2 Plus
        DeviceDescriptor(
            id       = "ONYX_NOTE_AIR2",
            match    = BuildMatch(brand = BRAND_ONYX, modelAliases = setOf("noteair2", "noteair2p")),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxSdkLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Note Air 3C
        DeviceDescriptor(
            id       = "ONYX_NOTE_AIR_3C",
            match    = BuildMatch(brand = BRAND_ONYX, model = "noteair3c"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_COLOR_FL,
        ),

        // Onyx Note Air 4C
        DeviceDescriptor(
            id       = "ONYX_NOTE_AIR_4C",
            match    = BuildMatch(brand = BRAND_ONYX, model = "noteair4c"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_COLOR_FL,
        ),

        // Onyx Note Air 5C
        DeviceDescriptor(
            id       = "ONYX_NOTE_AIR_5C",
            match    = BuildMatch(brand = BRAND_ONYX, model = "noteair5c"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_COLOR_FL,
        ),

        // Onyx Note Max
        DeviceDescriptor(
            id       = "ONYX_NOTE_MAX",
            match    = BuildMatch(brand = BRAND_ONYX, product = "notemax", device = "notemax"),
            epd      = ::OnyxEPDController,
            lights   = ::GenericController,
        ),

        // Onyx Note Pro
        DeviceDescriptor(
            id       = "ONYX_NOTE_PRO",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "notepro", device = "notepro"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxWarmthController,
            features = FL_WARMTH_SA,
        ),

        // Onyx Note S
        DeviceDescriptor(
            id       = "ONYX_NOTE_S",
            match    = BuildMatch(manufacturer = MFR_ONYX, model = "notes"),
            epd      = ::OnyxEPDController,
            lights   = ::GenericController,
        ),

        // Onyx Note X2
        DeviceDescriptor(
            id       = "ONYX_NOTE_X2",
            match    = BuildMatch(manufacturer = MFR_ONYX, model = "notex2"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxSdkLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Nova
        DeviceDescriptor(
            id       = "ONYX_NOVA",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "nova", device = "nova"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxSdkLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Nova 2
        DeviceDescriptor(
            id       = "ONYX_NOVA2",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "nova2", device = "nova2"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxSdkLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Nova 3
        DeviceDescriptor(
            id       = "ONYX_NOVA3",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "nova3", device = "nova3"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxWarmthController,
            features = FL_WARMTH_SA,
        ),

        // Onyx Nova 3 Color
        DeviceDescriptor(
            id       = "ONYX_NOVA3_COLOR",
            match    = BuildMatch(manufacturer = MFR_ONYX, model = "nova3color"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxColorController,
            features = FL_COLOR_FL,
        ),

        // Onyx Nova Air
        DeviceDescriptor(
            id       = "ONYX_NOVA_AIR",
            match    = BuildMatch(manufacturer = MFR_ONYX, model = "novaair"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Nova Air 2
        DeviceDescriptor(
            id       = "ONYX_NOVA_AIR_2",
            match    = BuildMatch(manufacturer = MFR_ONYX, model = "novaair2"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxSdkLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Nova Air C
        DeviceDescriptor(
            id       = "ONYX_NOVA_AIR_C",
            match    = BuildMatch(brand = BRAND_ONYX, model = "novaairc"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxSdkLightsController,
            features = FL_COLOR_FL,
        ),

        // Onyx Nova Pro
        DeviceDescriptor(
            id       = "ONYX_NOVA_PRO",
            match    = BuildMatch(brand = BRAND_ONYX, model = "novapro"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxWarmthController,
            features = FL_WARMTH_SA,
        ),

        // Onyx Page
        DeviceDescriptor(
            id       = "ONYX_PAGE",
            match    = BuildMatch(brand = BRAND_ONYX, model = "page"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Palma
        DeviceDescriptor(
            id       = "ONYX_PALMA",
            match    = BuildMatch(brand = BRAND_ONYX, model = "palma"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Palma 2
        DeviceDescriptor(
            id       = "ONYX_PALMA2",
            match    = BuildMatch(brand = BRAND_ONYX, model = "palma2"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Palma 2 Pro
        DeviceDescriptor(
            id       = "ONYX_PALMA2_PRO",
            match    = BuildMatch(brand = BRAND_ONYX, model = "palma2_pro_c"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxPalma2ProController,
            features = FL_WARMTH_SA,
        ),

        // Onyx Poke 2
        DeviceDescriptor(
            id       = "ONYX_POKE2",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "poke2"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxWarmthController,
            quirks   = Q_BROKEN,
            features = FL_WARMTH_SA,
        ),

        // Onyx Poke 3
        DeviceDescriptor(
            id       = "ONYX_POKE3",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "poke3", device = "poke3"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxWarmthController,
            features = FL_WARMTH_SA,
        ),

        // Onyx Poke 4
        DeviceDescriptor(
            id       = "ONYX_POKE4",
            match    = BuildMatch(brand = BRAND_ONYX, model = "poke4"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxSdkLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Poke 4 Lite
        DeviceDescriptor(
            id       = "ONYX_POKE4LITE",
            match    = BuildMatch(brand = BRAND_ONYX, model = "poke4lite"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxSdkLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Poke 5
        DeviceDescriptor(
            id       = "ONYX_POKE5",
            match    = BuildMatch(brand = BRAND_ONYX, model = "poke5p"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Poke 5S
        DeviceDescriptor(
            id       = "ONYX_POKE5S",
            match    = BuildMatch(brand = BRAND_ONYX, model = "poke5s"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Poke 6
        DeviceDescriptor(
            id       = "ONYX_POKE6",
            match    = BuildMatch(brand = BRAND_ONYX, model = "poke6"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Poke Pro
        DeviceDescriptor(
            id       = "ONYX_POKE_PRO",
            match    = BuildMatch(manufacturer = MFR_ONYX, product = "poke_pro"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxWarmthController,
            features = FL_WARMTH_SA,
        ),

        // Onyx Tab Ultra
        DeviceDescriptor(
            id       = "ONYX_TAB_ULTRA",
            match    = BuildMatch(manufacturer = MFR_ONYX, model = "tabultra"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxSdkLightsController,
            features = FL_WARMTH,
        ),

        // Onyx Tab Ultra C
        DeviceDescriptor(
            id       = "ONYX_TAB_ULTRA_C",
            match    = BuildMatch(manufacturer = MFR_ONYX, model = "tabultrac"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_COLOR_FL,
        ),

        // Onyx Tab Ultra C Pro
        DeviceDescriptor(
            id       = "ONYX_TAB_ULTRA_C_PRO",
            match    = BuildMatch(brand = BRAND_ONYX, product = "tabultracpro"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxAdbLightsController,
            features = FL_COLOR_FL,
        ),

        // ---- Pubu ----

        DeviceDescriptor(
            id       = "PUBU_PUBOOK",
            match    = BuildMatch(
                manufacturer = MFR_ROCKCHIP,
                brand        = BRAND_ROCKCHIP,
                model        = "pubook",
                device       = "pubook",
                hardware     = HW_RK30BOARD,
            ),
            epd      = ::RK3566EPDController,
            lights   = ::GenericController,
        ),

        // ---- Ridi ----

        DeviceDescriptor(
            id       = "RIDI_PAPER_3",
            match    = BuildMatch(brand = BRAND_RIDI, model = "ridipaper", product = "rp1"),
            epd      = ::TolinoEPDController,
            lights   = ::TolinoNtxController,
            features = FL_WARMTH,
        ),

        // ---- Sony ----

        DeviceDescriptor(
            id       = "SONY_CP1",
            match    = BuildMatch(manufacturer = MFR_SONY, model = "dpt-cp1"),
            epd      = ::NookEPDController,
            lights   = ::GenericController,
            quirks   = Q_NOLIGHTS,
        ),

        DeviceDescriptor(
            id       = "SONY_RP1",
            match    = BuildMatch(manufacturer = MFR_SONY, model = "dpt-rp1"),
            epd      = ::NookEPDController,
            lights   = ::GenericController,
            quirks   = Q_WAKE_NOLIGHTS,
        ),

        // ---- Storytel ----

        DeviceDescriptor(
            id       = "STORYTEL_READER2",
            match    = BuildMatch(manufacturer = MFR_STORYTEL, model = "reader 2"),
            epd      = ::OnyxEPDController,
            lights   = ::OnyxSdkLightsController,
            features = FL_WARMTH,
        ),

        // ---- Tagus ----

        DeviceDescriptor(
            id       = "TAGUS_GEA",
            match    = BuildMatch(manufacturer = MFR_ONYX, model = "tagus_pokep"),
            epd      = ::OldTolinoEPDController,
            lights   = ::OnyxColorController,
            features = FL_BASIC,
        ),

        // ---- Tolino ----

        // Tolino Epos 1
        DeviceDescriptor(
            id       = "TOLINO_EPOS1",
            match    = BuildMatch(brand = BRAND_TOLINO, model = "tolino", device = "ntx_6sl", hardware = "e70q20"),
            epd      = ::TolinoEPDController,
            lights   = ::TolinoNtxController,
            features = FL_WARMTH,
        ),

        // Tolino Epos 2
        DeviceDescriptor(
            id       = "TOLINO_EPOS2",
            match    = BuildMatch(brand = BRAND_TOLINO, model = "tolino", device = "ntx_6sl", hardware = "e80k00"),
            epd      = ::TolinoEPDController,
            lights   = ::TolinoRootController,
            features = FL_WARMTH,
        ),

        // Tolino Epos 3
        DeviceDescriptor(
            id       = "TOLINO_EPOS3",
            match    = BuildMatch(brand = BRAND_TOLINO, model = "tolino epos 3"),
            epd      = ::NGL4EPDController,
            lights   = ::TolinoB300Controller,
            features = FL_WARMTH,
        ),

        // Tolino Page 2 — no warmth
        DeviceDescriptor(
            id       = "TOLINO_PAGE2",
            match    = BuildMatch(brand = BRAND_TOLINO, model = "tolino", device = "ntx_6sl", hardware = "e60qv0"),
            epd      = ::TolinoEPDController,
            lights   = ::TolinoNtxNoWarmthController,
            features = FL_BASIC,
        ),

        // Tolino Shine 3
        DeviceDescriptor(
            id       = "TOLINO_SHINE3",
            match    = BuildMatch(brand = BRAND_TOLINO, model = "tolino", device = "ntx_6sl", hardware = "e60k00"),
            epd      = ::TolinoEPDController,
            lights   = ::TolinoNtxController,
            features = FL_WARMTH,
        ),

        // Tolino Shine 4
        DeviceDescriptor(
            id       = "TOLINO_SHINE4",
            match    = BuildMatch(brand = BRAND_TOLINO, model = "tolino shine 4", device = "tolino", hardware = "sun8iw15p1"),
            epd      = ::NGL4EPDController,
            lights   = ::TolinoB300InvertedWarmthController,
            features = FL_WARMTH,
        ),

        // Tolino Vision 4
        DeviceDescriptor(
            id       = "TOLINO_VISION4",
            match    = BuildMatch(brand = BRAND_TOLINO, model = "tolino", device = "ntx_6sl", hardware = "e60q50"),
            epd      = ::TolinoEPDController,
            lights   = ::TolinoNtxController,
            features = FL_WARMTH,
        ),

        // Tolino Vision 5
        DeviceDescriptor(
            id       = "TOLINO_VISION5",
            match    = BuildMatch(brand = BRAND_TOLINO, model = "tolino", device = "ntx_6sl", hardware = "e70k00"),
            epd      = ::TolinoEPDController,
            lights   = ::TolinoNtxController,
            features = FL_WARMTH,
        ),

        // Tolino Vision 6
        DeviceDescriptor(
            id       = "TOLINO_VISION6",
            match    = BuildMatch(brand = BRAND_TOLINO, model = "tolino vision 6", device = "tolino", hardware = "sun8iw15p1"),
            epd      = ::NGL4EPDController,
            lights   = ::TolinoB300InvertedWarmthController,
            features = FL_WARMTH,
        ),

        // Tolino catch-all — must come after all specific Tolino entries
        DeviceDescriptor(
            id       = "TOLINO",
            match    = BuildMatch(
                extra = { (brand == "tolino" && model == "imx50_rdp")
                       || (model == "tolino" && (device == "tolino_vision2" || device == "ntx_6sl")) },
            ),
            epd      = ::TolinoEPDController,
            lights   = ::GenericController,
        ),

        // ---- Xiaomi ----

        DeviceDescriptor(
            id       = "XIAOMI_READER",
            match    = BuildMatch(
                manufacturer = MFR_XIAOMI,
                brand        = BRAND_XIAOMI,
                model        = "xiaomi_reader",
                device       = "rk3566_eink",
                hardware     = HW_RK30BOARD,
            ),
            epd      = ::RK3566EPDController,
            lights   = ::GenericController,
        ),
    )

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Returned when no registry entry matches the current device.
     * Uses [FakeEPDController] (platform="none") so callers can detect
     * e-ink is unavailable. Generic colour-screen safe defaults are assumed.
     */
    val unknown = DeviceDescriptor(
        id       = "NONE",
        match    = BuildMatch(),
        epd      = ::FakeEPDController,
        lights   = ::GenericController,
        features = setOf(Feature.ColorScreen),
    )

    /** Detect the current device against [BuildSnapshot.current]. */
    fun detect(): DeviceDescriptor = detect(BuildSnapshot.current)

    /** Detect against an explicit snapshot — used in unit tests. */
    fun detect(snapshot: BuildSnapshot): DeviceDescriptor =
        entries.firstOrNull { it.match.matches(snapshot) } ?: unknown
}
