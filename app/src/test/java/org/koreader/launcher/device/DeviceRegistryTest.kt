package org.koreader.launcher.device

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Consolidated device-detection tests.
 *
 * Each test calls [DeviceRegistry.detect] with an explicit [BuildSnapshot],
 * so no Robolectric, no Build-field reflection, and no JVM forking is needed.
 * All 121 known devices plus precedence/characterisation cases run in a single JVM.
 */
class DeviceRegistryTest {

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private fun snap(
        manufacturer: String = "",
        brand: String = "",
        model: String = "",
        device: String = "",
        product: String = "",
        hardware: String = "",
    ) = BuildSnapshot(manufacturer, brand, model, device, product, hardware)

    private fun detect(s: BuildSnapshot) = DeviceRegistry.detect(s)

    // -------------------------------------------------------------------------
    // Unknown / fallback
    // -------------------------------------------------------------------------

    @Test fun unknown_device_returns_NONE_with_fake_epd() {
        val d = detect(snap(manufacturer = "google", brand = "google", model = "pixel",
            device = "redfin", product = "redfin", hardware = "redfin"))
        assertThat(d.id).isEqualTo("NONE")
        assertThat(d.epd().javaClass.simpleName).isEqualTo("FakeEPDController")
        assertThat(d.hasColorScreen).isTrue()
        assertThat(d.hasLights).isFalse()
    }

    // -------------------------------------------------------------------------
    // Boyue
    // -------------------------------------------------------------------------

    @Test fun boyue_c64p() {
        val d = detect(snap(brand = "c64p", product = "c64p"))
        assertThat(d.id).isEqualTo("BOYUE_C64P")
    }

    @Test fun boyue_k78w() {
        val d = detect(snap(manufacturer = "boyue", product = "k78w"))
        assertThat(d.id).isEqualTo("BOYUE_K78W")
    }

    @Test fun boyue_k103() {
        val d = detect(snap(manufacturer = "boyue", product = "k103"))
        assertThat(d.id).isEqualTo("BOYUE_K103")
    }

    @Test fun boyue_p6() {
        val d = detect(snap(manufacturer = "boyue", product = "p6"))
        assertThat(d.id).isEqualTo("BOYUE_P6")
    }

    @Test fun boyue_p61() {
        val d = detect(snap(manufacturer = "boyue", product = "p61-k12-l"))
        assertThat(d.id).isEqualTo("BOYUE_P61")
    }

    @Test fun boyue_p78() {
        val d = detect(snap(manufacturer = "boyue", product = "p78"))
        assertThat(d.id).isEqualTo("BOYUE_P78")
    }

    @Test fun boyue_p101() {
        val d = detect(snap(manufacturer = "boyue", product = "p101"))
        assertThat(d.id).isEqualTo("BOYUE_P101")
    }

    @Test fun boyue_s62() {
        val d = detect(snap(manufacturer = "boyue", product = "s62"))
        assertThat(d.id).isEqualTo("BOYUE_S62")
    }

    @Test fun boyue_t61_by_product_and_device() {
        val d = detect(snap(manufacturer = "boyue", product = "t61d", device = "t61d"))
        assertThat(d.id).isEqualTo("BOYUE_T61")
    }

    @Test fun boyue_t61_rk30sdk_variant() {
        val d = detect(snap(manufacturer = "boyue", model = "rk30sdk", device = "t61"))
        assertThat(d.id).isEqualTo("BOYUE_T61")
    }

    @Test fun boyue_t62_by_product_and_device() {
        val d = detect(snap(manufacturer = "boyue", product = "t62e", device = "t62e"))
        assertThat(d.id).isEqualTo("BOYUE_T62")
    }

    @Test fun boyue_t62_rk30sdk_variant_any_manufacturer() {
        // Preserves prior operator-precedence behaviour — no manufacturer required
        val d = detect(snap(manufacturer = "other", model = "rk30sdk", device = "t62e"))
        assertThat(d.id).isEqualTo("BOYUE_T62")
    }

    @Test fun boyue_t65s() {
        val d = detect(snap(manufacturer = "boyue", product = "t65s"))
        assertThat(d.id).isEqualTo("BOYUE_T65S")
    }

    @Test fun boyue_t78d() {
        val d = detect(snap(manufacturer = "boyue", product = "t78d"))
        assertThat(d.id).isEqualTo("BOYUE_T78D")
    }

    @Test fun boyue_t80d() {
        val d = detect(snap(manufacturer = "boyue", product = "t80d"))
        assertThat(d.id).isEqualTo("BOYUE_T80D")
    }

    @Test fun boyue_t80s() {
        val d = detect(snap(manufacturer = "boyue", product = "t80s"))
        assertThat(d.id).isEqualTo("BOYUE_T80S")
    }

    @Test fun boyue_t103d() {
        val d = detect(snap(manufacturer = "boyue", product = "t103d"))
        assertThat(d.id).isEqualTo("BOYUE_T103D")
    }

    // -------------------------------------------------------------------------
    // Crema
    // -------------------------------------------------------------------------

    @Test fun crema() {
        val d = detect(snap(brand = "crema", product = "note"))
        assertThat(d.id).isEqualTo("CREMA")
    }

    @Test fun crema_0650l() {
        val d = detect(snap(brand = "crema", product = "keplerb"))
        assertThat(d.id).isEqualTo("CREMA_0650L")
    }

    @Test fun crema_0660l() {
        val d = detect(snap(brand = "crema", product = "sound2"))
        assertThat(d.id).isEqualTo("CREMA_0660L")
    }

    @Test fun crema_0710c() {
        val d = detect(snap(brand = "crema", model = "crema-0710c"))
        assertThat(d.id).isEqualTo("CREMA_0710C")
    }

    @Test fun crema_carta_g() {
        val d = detect(snap(brand = "crema", model = "crema-0670c"))
        assertThat(d.id).isEqualTo("CREMA_CARTA_G")
    }

    // -------------------------------------------------------------------------
    // Energy
    // -------------------------------------------------------------------------

    @Test fun energy() {
        val d = detect(snap(brand = "energysistem", model = "ereader"))
        assertThat(d.id).isEqualTo("ENERGY")
    }

    // -------------------------------------------------------------------------
    // Fidibook
    // -------------------------------------------------------------------------

    @Test fun fidibook() {
        val d = detect(snap(manufacturer = "fidibo", model = "fidibook"))
        assertThat(d.id).isEqualTo("FIDIBOOK")
    }

    // -------------------------------------------------------------------------
    // Hanvon
    // -------------------------------------------------------------------------

    @Test fun hanvon_960() {
        val d = detect(snap(brand = "freescale", product = "evk_6sl_eink"))
        assertThat(d.id).isEqualTo("HANVON_960")
    }

    // -------------------------------------------------------------------------
    // Hisense
    // -------------------------------------------------------------------------

    @Test fun hisense_touch_lite() {
        val d = detect(snap(brand = "hisense", model = "hitv205n"))
        assertThat(d.id).isEqualTo("HISENSE_TOUCH_LITE")
    }

    // -------------------------------------------------------------------------
    // Hyread
    // -------------------------------------------------------------------------

    @Test fun hyread_gaze_note() {
        val d = detect(snap(manufacturer = "hyread", model = "r08p"))
        assertThat(d.id).isEqualTo("HYREAD_GAZE_NOTE")
    }

    @Test fun hyread_gaze_note_cc() {
        val d = detect(snap(manufacturer = "hyread", model = "k08cc"))
        assertThat(d.id).isEqualTo("HYREAD_GAZE_NOTE_CC")
    }

    @Test fun hyread_mini6() {
        val d = detect(snap(manufacturer = "hyread", model = "k06nu"))
        assertThat(d.id).isEqualTo("HYREAD_MINI6")
    }

    // -------------------------------------------------------------------------
    // Iflytek
    // -------------------------------------------------------------------------

    @Test fun iflytek_r3() {
        val d = detect(snap(manufacturer = "iflytek", model = "iflytek ebook r3"))
        assertThat(d.id).isEqualTo("IFLYTEK_R3")
    }

    // -------------------------------------------------------------------------
    // InkBook
    // -------------------------------------------------------------------------

    @Test fun inkbook() {
        val d = detect(snap(manufacturer = "artatech", brand = "inkbook", model = "prime"))
        assertThat(d.id).isEqualTo("INKBOOK")
    }

    @Test fun inkbookfocus() {
        val d = detect(snap(device = "px30_eink", model = "focus"))
        assertThat(d.id).isEqualTo("INKBOOKFOCUS")
    }

    @Test fun inkbookfocus_plus() {
        val d = detect(snap(device = "rk3566_eink", model = "focus plus"))
        assertThat(d.id).isEqualTo("INKBOOKFOCUS_PLUS")
    }

    // -------------------------------------------------------------------------
    // InkPalm
    // -------------------------------------------------------------------------

    @Test fun inkpalm_plus() {
        val d = detect(snap(manufacturer = "rockchip", model = "inkpalmplus"))
        assertThat(d.id).isEqualTo("INKPALM_PLUS")
    }

    // -------------------------------------------------------------------------
    // JDRead
    // -------------------------------------------------------------------------

    @Test fun jdread() {
        val d = detect(snap(manufacturer = "onyx", model = "jdread"))
        assertThat(d.id).isEqualTo("JDREAD")
    }

    // -------------------------------------------------------------------------
    // Linfiny
    // -------------------------------------------------------------------------

    @Test fun linfiny_enote() {
        val d = detect(snap(manufacturer = "linfiny", model = "ent-13t1"))
        assertThat(d.id).isEqualTo("LINFINY_ENOTE")
    }

    // -------------------------------------------------------------------------
    // Meebook
    // -------------------------------------------------------------------------

    @Test fun meebook_m6() {
        val d = detect(snap(manufacturer = "haoqing", model = "m6"))
        assertThat(d.id).isEqualTo("MEEBOOK_M6")
    }

    @Test fun meebook_m6c() {
        val d = detect(snap(manufacturer = "haoqing", model = "m6c"))
        assertThat(d.id).isEqualTo("MEEBOOK_M6C")
    }

    @Test fun meebook_m7() {
        val d = detect(snap(manufacturer = "haoqing", model = "m7"))
        assertThat(d.id).isEqualTo("MEEBOOK_M7")
    }

    @Test fun meebook_p6() {
        val d = detect(snap(manufacturer = "haoqing", model = "p6"))
        assertThat(d.id).isEqualTo("MEEBOOK_P6")
    }

    // -------------------------------------------------------------------------
    // Moaan
    // -------------------------------------------------------------------------

    @Test fun moaan_mix7() {
        val d = detect(snap(manufacturer = "rockchip", model = "moaanmix7"))
        assertThat(d.id).isEqualTo("MOAAN_MIX7")
    }

    // -------------------------------------------------------------------------
    // Mooink
    // -------------------------------------------------------------------------

    @Test fun mooinkplus2c() {
        val d = detect(snap(brand = "allwinner", model = "mooink plus 2c"))
        assertThat(d.id).isEqualTo("MOOINKPLUS2C")
    }

    // -------------------------------------------------------------------------
    // Nabuk
    // -------------------------------------------------------------------------

    @Test fun nabuk() {
        val d = detect(snap(manufacturer = "onyx", model = "nabukreg_hd"))
        assertThat(d.id).isEqualTo("NABUK")
    }

    // -------------------------------------------------------------------------
    // Nook — cascade order: GL4 > GLPLUS > NOOK
    // -------------------------------------------------------------------------

    @Test fun nook_gl4_bnrv1000() {
        val d = detect(snap(manufacturer = "barnesandnoble", model = "bnrv1000"))
        assertThat(d.id).isEqualTo("NOOK_GL4")
    }

    @Test fun nook_gl4_bnrv1100() {
        val d = detect(snap(manufacturer = "barnesandnoble", model = "bnrv1100"))
        assertThat(d.id).isEqualTo("NOOK_GL4")
    }

    @Test fun nook_gl4_bnrv1300() {
        val d = detect(snap(manufacturer = "barnesandnoble", model = "bnrv1300"))
        assertThat(d.id).isEqualTo("NOOK_GL4")
    }

    @Test fun nook_glplus() {
        val d = detect(snap(manufacturer = "barnesandnoble", model = "bnrv700", product = "ntx_6sl"))
        assertThat(d.id).isEqualTo("NOOK_GLPLUS")
    }

    @Test fun nook_catchall_bnrv510() {
        val d = detect(snap(manufacturer = "barnesandnoble", model = "bnrv510"))
        assertThat(d.id).isEqualTo("NOOK")
    }

    // -------------------------------------------------------------------------
    // OBook
    // -------------------------------------------------------------------------

    @Test fun obook_p10d() {
        val d = detect(snap(manufacturer = "rockchip", model = "p10d"))
        assertThat(d.id).isEqualTo("OBOOK_P10D")
    }

    @Test fun obook_p78d() {
        val d = detect(snap(manufacturer = "rockchip", product = "rk3566_78d", model = "p78d"))
        assertThat(d.id).isEqualTo("OBOOK_P78D")
    }

    // -------------------------------------------------------------------------
    // Onyx
    // -------------------------------------------------------------------------

    @Test fun onyx_c67_by_product_and_device() {
        val d = detect(snap(manufacturer = "onyx", product = "c67", device = "c67"))
        assertThat(d.id).isEqualTo("ONYX_C67")
    }

    @Test fun onyx_c67_rk30sdk_variant() {
        val d = detect(snap(manufacturer = "onyx", model = "rk30sdk", device = "c67"))
        assertThat(d.id).isEqualTo("ONYX_C67")
    }

    @Test fun onyx_darwin5_by_brand_maccentre() {
        // Precedence test: model-only match fires without manufacturer
        val d = detect(snap(manufacturer = "other", brand = "other", model = "mc_c68pctm"))
        assertThat(d.id).isEqualTo("ONYX_DARWIN5")
    }

    @Test fun onyx_darwin5_by_brand() {
        val d = detect(snap(manufacturer = "onyx", brand = "maccentre"))
        assertThat(d.id).isEqualTo("ONYX_DARWIN5")
    }

    @Test fun onyx_darwin7() {
        val d = detect(snap(manufacturer = "onyx", product = "darwin7", device = "darwin7"))
        assertThat(d.id).isEqualTo("ONYX_DARWIN7")
    }

    @Test fun onyx_darwin9() {
        val d = detect(snap(manufacturer = "onyx", product = "darwin9", device = "darwin9"))
        assertThat(d.id).isEqualTo("ONYX_DARWIN9")
    }

    @Test fun onyx_edison() {
        val d = detect(snap(manufacturer = "onyx", product = "edison", device = "edison"))
        assertThat(d.id).isEqualTo("ONYX_EDISON")
    }

    @Test fun onyx_faust3() {
        val d = detect(snap(manufacturer = "onyx", product = "mc_faust3", device = "mc_faust3"))
        assertThat(d.id).isEqualTo("ONYX_FAUST3")
    }

    @Test fun onyx_galileo2() {
        val d = detect(snap(brand = "onyx", model = "galileo2"))
        assertThat(d.id).isEqualTo("ONYX_GALILEO2")
    }

    @Test fun onyx_go6() {
        val d = detect(snap(brand = "onyx", model = "go6"))
        assertThat(d.id).isEqualTo("ONYX_GO6")
    }

    @Test fun onyx_go7() {
        val d = detect(snap(brand = "onyx", model = "go7"))
        assertThat(d.id).isEqualTo("ONYX_GO7")
    }

    @Test fun onyx_go7gen2() {
        val d = detect(snap(brand = "onyx", model = "gocolor7_2"))
        assertThat(d.id).isEqualTo("ONYX_GO7GEN2")
    }

    @Test fun onyx_go_103() {
        val d = detect(snap(brand = "onyx", model = "go103"))
        assertThat(d.id).isEqualTo("ONYX_GO_103")
    }

    @Test fun onyx_go_color7() {
        val d = detect(snap(brand = "onyx", model = "gocolor7"))
        assertThat(d.id).isEqualTo("ONYX_GO_COLOR7")
    }

    @Test fun onyx_jdread() {
        // brand=onyx distinguishes from manufacturer=onyx JDREAD entry
        val d = detect(snap(manufacturer = "other", brand = "onyx", model = "jdread"))
        assertThat(d.id).isEqualTo("ONYX_JDREAD")
    }

    @Test fun onyx_kon_tiki2() {
        val d = detect(snap(manufacturer = "onyx", product = "kon_tiki2", device = "kon_tiki2"))
        assertThat(d.id).isEqualTo("ONYX_KON_TIKI2")
    }

    @Test fun onyx_leaf() {
        val d = detect(snap(manufacturer = "onyx", product = "leaf", device = "leaf"))
        assertThat(d.id).isEqualTo("ONYX_LEAF")
    }

    @Test fun onyx_leaf2() {
        val d = detect(snap(manufacturer = "onyx", product = "leaf2", device = "leaf2"))
        assertThat(d.id).isEqualTo("ONYX_LEAF2")
    }

    @Test fun onyx_livingstone3() {
        val d = detect(snap(manufacturer = "onyx", device = "livingstone3"))
        assertThat(d.id).isEqualTo("ONYX_LIVINGSTONE3")
    }

    @Test fun onyx_lomonosov() {
        val d = detect(snap(manufacturer = "onyx", device = "lomonosov"))
        assertThat(d.id).isEqualTo("ONYX_LOMONOSOV")
    }

    @Test fun onyx_magicbook() {
        val d = detect(snap(manufacturer = "onyx", brand = "magicbook"))
        assertThat(d.id).isEqualTo("ONYX_MAGICBOOK")
    }

    @Test fun onyx_max() {
        val d = detect(snap(manufacturer = "onyx", product = "max", device = "max"))
        assertThat(d.id).isEqualTo("ONYX_MAX")
    }

    @Test fun onyx_max2pro() {
        val d = detect(snap(manufacturer = "onyx", product = "max2pro", device = "max2pro"))
        assertThat(d.id).isEqualTo("ONYX_MAX2_PRO")
    }

    @Test fun onyx_montecristo3() {
        val d = detect(snap(manufacturer = "onyx", product = "mc_kepler_c", device = "mc_kepler_c"))
        assertThat(d.id).isEqualTo("ONYX_MONTECRISTO3")
    }

    @Test fun onyx_note() {
        val d = detect(snap(manufacturer = "onyx", product = "note", device = "note"))
        assertThat(d.id).isEqualTo("ONYX_NOTE")
    }

    @Test fun onyx_note3() {
        val d = detect(snap(manufacturer = "onyx", product = "note3", device = "note3"))
        assertThat(d.id).isEqualTo("ONYX_NOTE3")
    }

    @Test fun onyx_note4() {
        val d = detect(snap(manufacturer = "onyx", model = "mc_note4"))
        assertThat(d.id).isEqualTo("ONYX_NOTE4")
    }

    @Test fun onyx_note5() {
        val d = detect(snap(brand = "onyx", product = "note5", device = "note5"))
        assertThat(d.id).isEqualTo("ONYX_NOTE5")
    }

    @Test fun onyx_note_air() {
        val d = detect(snap(manufacturer = "onyx", product = "noteair", device = "noteair"))
        assertThat(d.id).isEqualTo("ONYX_NOTE_AIR")
    }

    @Test fun onyx_note_air2() {
        val d = detect(snap(brand = "onyx", model = "noteair2"))
        assertThat(d.id).isEqualTo("ONYX_NOTE_AIR2")
    }

    @Test fun onyx_note_air_3c() {
        val d = detect(snap(brand = "onyx", model = "noteair3c"))
        assertThat(d.id).isEqualTo("ONYX_NOTE_AIR_3C")
    }

    @Test fun onyx_note_air_4c() {
        val d = detect(snap(brand = "onyx", model = "noteair4c"))
        assertThat(d.id).isEqualTo("ONYX_NOTE_AIR_4C")
    }

    @Test fun onyx_note_air_5c() {
        val d = detect(snap(brand = "onyx", model = "noteair5c"))
        assertThat(d.id).isEqualTo("ONYX_NOTE_AIR_5C")
    }

    @Test fun onyx_note_max() {
        val d = detect(snap(brand = "onyx", product = "notemax", device = "notemax"))
        assertThat(d.id).isEqualTo("ONYX_NOTE_MAX")
    }

    @Test fun onyx_note_pro() {
        val d = detect(snap(manufacturer = "onyx", product = "notepro", device = "notepro"))
        assertThat(d.id).isEqualTo("ONYX_NOTE_PRO")
    }

    @Test fun onyx_note_s() {
        val d = detect(snap(manufacturer = "onyx", model = "notes"))
        assertThat(d.id).isEqualTo("ONYX_NOTE_S")
    }

    @Test fun onyx_note_x2() {
        val d = detect(snap(manufacturer = "onyx", model = "notex2"))
        assertThat(d.id).isEqualTo("ONYX_NOTE_X2")
    }

    @Test fun onyx_nova() {
        val d = detect(snap(manufacturer = "onyx", product = "nova", device = "nova"))
        assertThat(d.id).isEqualTo("ONYX_NOVA")
    }

    @Test fun onyx_nova2() {
        val d = detect(snap(manufacturer = "onyx", product = "nova2", device = "nova2"))
        assertThat(d.id).isEqualTo("ONYX_NOVA2")
    }

    @Test fun onyx_nova3() {
        val d = detect(snap(manufacturer = "onyx", product = "nova3", device = "nova3"))
        assertThat(d.id).isEqualTo("ONYX_NOVA3")
    }

    @Test fun onyx_nova3_color() {
        val d = detect(snap(manufacturer = "onyx", model = "nova3color"))
        assertThat(d.id).isEqualTo("ONYX_NOVA3_COLOR")
        assertThat(d.hasColorScreen).isTrue()
    }

    @Test fun onyx_nova_air() {
        val d = detect(snap(manufacturer = "onyx", model = "novaair"))
        assertThat(d.id).isEqualTo("ONYX_NOVA_AIR")
    }

    @Test fun onyx_nova_air_2() {
        val d = detect(snap(manufacturer = "onyx", model = "novaair2"))
        assertThat(d.id).isEqualTo("ONYX_NOVA_AIR_2")
    }

    @Test fun onyx_nova_air_c() {
        val d = detect(snap(brand = "onyx", model = "novaairc"))
        assertThat(d.id).isEqualTo("ONYX_NOVA_AIR_C")
    }

    @Test fun onyx_nova_pro() {
        val d = detect(snap(brand = "onyx", model = "novapro"))
        assertThat(d.id).isEqualTo("ONYX_NOVA_PRO")
    }

    @Test fun onyx_page() {
        val d = detect(snap(brand = "onyx", model = "page"))
        assertThat(d.id).isEqualTo("ONYX_PAGE")
    }

    @Test fun onyx_palma() {
        val d = detect(snap(brand = "onyx", model = "palma"))
        assertThat(d.id).isEqualTo("ONYX_PALMA")
    }

    @Test fun onyx_palma2() {
        val d = detect(snap(brand = "onyx", model = "palma2"))
        assertThat(d.id).isEqualTo("ONYX_PALMA2")
    }

    @Test fun onyx_palma2_pro() {
        val d = detect(snap(brand = "onyx", model = "palma2_pro_c"))
        assertThat(d.id).isEqualTo("ONYX_PALMA2_PRO")
    }

    @Test fun onyx_poke2() {
        val d = detect(snap(manufacturer = "onyx", product = "poke2"))
        assertThat(d.id).isEqualTo("ONYX_POKE2")
        assertThat(d.hasBrokenLifecycle).isTrue()
    }

    @Test fun onyx_poke3() {
        val d = detect(snap(manufacturer = "onyx", product = "poke3", device = "poke3"))
        assertThat(d.id).isEqualTo("ONYX_POKE3")
    }

    @Test fun onyx_poke4() {
        val d = detect(snap(brand = "onyx", model = "poke4"))
        assertThat(d.id).isEqualTo("ONYX_POKE4")
    }

    @Test fun onyx_poke4lite() {
        val d = detect(snap(brand = "onyx", model = "poke4lite"))
        assertThat(d.id).isEqualTo("ONYX_POKE4LITE")
    }

    @Test fun onyx_poke5() {
        val d = detect(snap(brand = "onyx", model = "poke5p"))
        assertThat(d.id).isEqualTo("ONYX_POKE5")
    }

    @Test fun onyx_poke5s() {
        val d = detect(snap(brand = "onyx", model = "poke5s"))
        assertThat(d.id).isEqualTo("ONYX_POKE5S")
    }

    @Test fun onyx_poke6() {
        val d = detect(snap(brand = "onyx", model = "poke6"))
        assertThat(d.id).isEqualTo("ONYX_POKE6")
    }

    @Test fun onyx_poke_pro() {
        val d = detect(snap(manufacturer = "onyx", product = "poke_pro"))
        assertThat(d.id).isEqualTo("ONYX_POKE_PRO")
    }

    @Test fun onyx_tab_ultra() {
        val d = detect(snap(manufacturer = "onyx", model = "tabultra"))
        assertThat(d.id).isEqualTo("ONYX_TAB_ULTRA")
    }

    @Test fun onyx_tab_ultra_c() {
        val d = detect(snap(manufacturer = "onyx", model = "tabultrac"))
        assertThat(d.id).isEqualTo("ONYX_TAB_ULTRA_C")
    }

    @Test fun onyx_tab_ultra_c_pro() {
        val d = detect(snap(brand = "onyx", product = "tabultracpro"))
        assertThat(d.id).isEqualTo("ONYX_TAB_ULTRA_C_PRO")
    }

    // -------------------------------------------------------------------------
    // Pubu
    // -------------------------------------------------------------------------

    @Test fun pubu_pubook() {
        val d = detect(snap(manufacturer = "rockchip", brand = "rockchip",
            model = "pubook", device = "pubook", hardware = "rk30board"))
        assertThat(d.id).isEqualTo("PUBU_PUBOOK")
    }

    // -------------------------------------------------------------------------
    // Ridi
    // -------------------------------------------------------------------------

    @Test fun ridi_paper_3() {
        val d = detect(snap(brand = "ridi", model = "ridipaper", product = "rp1"))
        assertThat(d.id).isEqualTo("RIDI_PAPER_3")
    }

    // -------------------------------------------------------------------------
    // Sony
    // -------------------------------------------------------------------------

    @Test fun sony_cp1() {
        val d = detect(snap(manufacturer = "sony", model = "dpt-cp1"))
        assertThat(d.id).isEqualTo("SONY_CP1")
        assertThat(d.hasLights).isFalse()
    }

    @Test fun sony_rp1() {
        val d = detect(snap(manufacturer = "sony", model = "dpt-rp1"))
        assertThat(d.id).isEqualTo("SONY_RP1")
        assertThat(d.needsWakelocks).isTrue()
        assertThat(d.hasLights).isFalse()
    }

    // -------------------------------------------------------------------------
    // Storytel
    // -------------------------------------------------------------------------

    @Test fun storytel_reader2() {
        val d = detect(snap(manufacturer = "storytel", model = "reader 2"))
        assertThat(d.id).isEqualTo("STORYTEL_READER2")
    }

    // -------------------------------------------------------------------------
    // Tagus
    // -------------------------------------------------------------------------

    @Test fun tagus_gea() {
        val d = detect(snap(manufacturer = "onyx", model = "tagus_pokep"))
        assertThat(d.id).isEqualTo("TAGUS_GEA")
    }

    // -------------------------------------------------------------------------
    // Tolino — specific entries before catch-all
    // -------------------------------------------------------------------------

    @Test fun tolino_epos1() {
        val d = detect(snap(brand = "rakutenkobo", model = "tolino", device = "ntx_6sl", hardware = "e70q20"))
        assertThat(d.id).isEqualTo("TOLINO_EPOS1")
    }

    @Test fun tolino_epos2() {
        val d = detect(snap(brand = "rakutenkobo", model = "tolino", device = "ntx_6sl", hardware = "e80k00"))
        assertThat(d.id).isEqualTo("TOLINO_EPOS2")
    }

    @Test fun tolino_epos3() {
        val d = detect(snap(brand = "rakutenkobo", model = "tolino epos 3"))
        assertThat(d.id).isEqualTo("TOLINO_EPOS3")
    }

    @Test fun tolino_page2() {
        val d = detect(snap(brand = "rakutenkobo", model = "tolino", device = "ntx_6sl", hardware = "e60qv0"))
        assertThat(d.id).isEqualTo("TOLINO_PAGE2")
    }

    @Test fun tolino_shine3() {
        val d = detect(snap(brand = "rakutenkobo", model = "tolino", device = "ntx_6sl", hardware = "e60k00"))
        assertThat(d.id).isEqualTo("TOLINO_SHINE3")
    }

    @Test fun tolino_shine4() {
        val d = detect(snap(brand = "rakutenkobo", model = "tolino shine 4", device = "tolino", hardware = "sun8iw15p1"))
        assertThat(d.id).isEqualTo("TOLINO_SHINE4")
    }

    @Test fun tolino_vision4() {
        val d = detect(snap(brand = "rakutenkobo", model = "tolino", device = "ntx_6sl", hardware = "e60q50"))
        assertThat(d.id).isEqualTo("TOLINO_VISION4")
    }

    @Test fun tolino_vision5() {
        val d = detect(snap(brand = "rakutenkobo", model = "tolino", device = "ntx_6sl", hardware = "e70k00"))
        assertThat(d.id).isEqualTo("TOLINO_VISION5")
    }

    @Test fun tolino_vision6() {
        val d = detect(snap(brand = "rakutenkobo", model = "tolino vision 6", device = "tolino", hardware = "sun8iw15p1"))
        assertThat(d.id).isEqualTo("TOLINO_VISION6")
    }

    @Test fun tolino_catchall_imx50() {
        val d = detect(snap(brand = "tolino", model = "imx50_rdp"))
        assertThat(d.id).isEqualTo("TOLINO")
    }

    @Test fun tolino_catchall_vision2() {
        val d = detect(snap(model = "tolino", device = "tolino_vision2"))
        assertThat(d.id).isEqualTo("TOLINO")
    }

    // -------------------------------------------------------------------------
    // Xiaomi
    // -------------------------------------------------------------------------

    @Test fun xiaomi_reader() {
        val d = detect(snap(manufacturer = "xiaomi", brand = "xiaomi",
            model = "xiaomi_reader", device = "rk3566_eink", hardware = "rk30board"))
        assertThat(d.id).isEqualTo("XIAOMI_READER")
    }

    // -------------------------------------------------------------------------
    // Coverage guard — every DeviceInfo.Id (except NONE) must be exercised above
    // -------------------------------------------------------------------------

    @Test fun every_device_id_has_a_declared_test_case() {
        val declared = setOf(
            "BOYUE_C64P", "BOYUE_K103", "BOYUE_K78W", "BOYUE_P101", "BOYUE_P6",
            "BOYUE_P61", "BOYUE_P78", "BOYUE_S62", "BOYUE_T103D", "BOYUE_T61",
            "BOYUE_T62", "BOYUE_T65S", "BOYUE_T78D", "BOYUE_T80D", "BOYUE_T80S",
            "CREMA", "CREMA_0650L", "CREMA_0660L", "CREMA_0710C", "CREMA_CARTA_G",
            "ENERGY", "FIDIBOOK", "HANVON_960", "HISENSE_TOUCH_LITE",
            "HYREAD_GAZE_NOTE", "HYREAD_GAZE_NOTE_CC", "HYREAD_MINI6", "IFLYTEK_R3",
            "INKBOOK", "INKBOOKFOCUS", "INKBOOKFOCUS_PLUS", "INKPALM_PLUS",
            "JDREAD", "LINFINY_ENOTE",
            "MEEBOOK_M6", "MEEBOOK_M6C", "MEEBOOK_M7", "MEEBOOK_P6",
            "MOAAN_MIX7", "MOOINKPLUS2C", "NABUK",
            "NOOK", "NOOK_GL4", "NOOK_GLPLUS",
            "OBOOK_P10D", "OBOOK_P78D",
            "ONYX_C67", "ONYX_DARWIN5", "ONYX_DARWIN7", "ONYX_DARWIN9",
            "ONYX_EDISON", "ONYX_FAUST3", "ONYX_GALILEO2",
            "ONYX_GO6", "ONYX_GO7", "ONYX_GO7GEN2", "ONYX_GO_103", "ONYX_GO_COLOR7",
            "ONYX_JDREAD", "ONYX_KON_TIKI2", "ONYX_LEAF", "ONYX_LEAF2",
            "ONYX_LIVINGSTONE3", "ONYX_LOMONOSOV", "ONYX_MAGICBOOK",
            "ONYX_MAX", "ONYX_MAX2_PRO", "ONYX_MONTECRISTO3",
            "ONYX_NOTE", "ONYX_NOTE3", "ONYX_NOTE4", "ONYX_NOTE5",
            "ONYX_NOTE_AIR", "ONYX_NOTE_AIR2", "ONYX_NOTE_AIR_3C",
            "ONYX_NOTE_AIR_4C", "ONYX_NOTE_AIR_5C", "ONYX_NOTE_MAX",
            "ONYX_NOTE_PRO", "ONYX_NOTE_S", "ONYX_NOTE_X2",
            "ONYX_NOVA", "ONYX_NOVA2", "ONYX_NOVA3", "ONYX_NOVA3_COLOR",
            "ONYX_NOVA_AIR", "ONYX_NOVA_AIR_2", "ONYX_NOVA_AIR_C", "ONYX_NOVA_PRO",
            "ONYX_PAGE", "ONYX_PALMA", "ONYX_PALMA2", "ONYX_PALMA2_PRO",
            "ONYX_POKE2", "ONYX_POKE3", "ONYX_POKE4", "ONYX_POKE4LITE",
            "ONYX_POKE5", "ONYX_POKE5S", "ONYX_POKE6", "ONYX_POKE_PRO",
            "ONYX_TAB_ULTRA", "ONYX_TAB_ULTRA_C", "ONYX_TAB_ULTRA_C_PRO",
            "PUBU_PUBOOK", "RIDI_PAPER_3",
            "SONY_CP1", "SONY_RP1", "STORYTEL_READER2", "TAGUS_GEA",
            "TOLINO", "TOLINO_EPOS1", "TOLINO_EPOS2", "TOLINO_EPOS3",
            "TOLINO_PAGE2", "TOLINO_SHINE3", "TOLINO_SHINE4",
            "TOLINO_VISION4", "TOLINO_VISION5", "TOLINO_VISION6",
            "XIAOMI_READER",
        )

        val all = DeviceInfo.Id.entries.filter { it != DeviceInfo.Id.NONE }.map { it.name }.toSet()
        assertThat(declared).isEqualTo(all)
    }
}
