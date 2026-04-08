package org.koreader.launcher.device

/**
 * A declarative, null-wildcard matcher against Android build properties.
 *
 * Each non-null field must match the corresponding (lowercased) build property exactly,
 * except [productPrefix] and [devicePrefix] which use [String.startsWith].
 * [brandAliases] and [productAliases] accept any one of a set of values.
 * [extra] is an optional escape hatch for cases not expressible with the above.
 *
 * All string values are compared against already-lowercased build fields — supply
 * lowercase literals here.
 */
data class BuildMatch(
    val manufacturer: String?       = null,
    val brand: String?              = null,
    val model: String?              = null,
    val device: String?             = null,
    val product: String?            = null,
    val hardware: String?           = null,
    /** Match if [manufacturer] is any of these values (exclusive with [manufacturer]). */
    val manufacturerAliases: Set<String>? = null,
    /** Match if [brand] is any of these values (exclusive with [brand]). */
    val brandAliases: Set<String>?  = null,
    /** Match if [model] is any of these values (exclusive with [model]). */
    val modelAliases: Set<String>?  = null,
    /** Match if [product] is any of these values (exclusive with [product]). */
    val productAliases: Set<String>? = null,
    /** Match if the build product starts with this prefix (exclusive with [product]). */
    val productPrefix: String?      = null,
    /** Match if the build device starts with this prefix (exclusive with [device]). */
    val devicePrefix: String?       = null,
    /** Optional extra predicate evaluated against lowercased build fields. */
    val extra: (BuildSnapshot.() -> Boolean)? = null,
) {
    init {
        require(manufacturer == null || manufacturerAliases == null) {
            "Use either manufacturer or manufacturerAliases, not both"
        }
        require(brand == null || brandAliases == null) {
            "Use either brand or brandAliases, not both"
        }
        require(model == null || modelAliases == null) {
            "Use either model or modelAliases, not both"
        }
        require(product == null || productAliases == null) {
            "Use either product or productAliases, not both"
        }
        require(product == null || productPrefix == null) {
            "Use either product or productPrefix, not both"
        }
        require(device == null || devicePrefix == null) {
            "Use either device or devicePrefix, not both"
        }
    }

    fun matches(s: BuildSnapshot): Boolean {
        if (manufacturer        != null && manufacturer        != s.manufacturer) return false
        if (brand               != null && brand               != s.brand)        return false
        if (model               != null && model               != s.model)        return false
        if (device              != null && device              != s.device)       return false
        if (product             != null && product             != s.product)      return false
        if (hardware            != null && hardware            != s.hardware)     return false
        if (manufacturerAliases != null && s.manufacturer !in manufacturerAliases) return false
        if (brandAliases        != null && s.brand        !in brandAliases)      return false
        if (modelAliases        != null && s.model        !in modelAliases)      return false
        if (productAliases      != null && s.product      !in productAliases)    return false
        if (productPrefix       != null && !s.product.startsWith(productPrefix)) return false
        if (devicePrefix        != null && !s.device.startsWith(devicePrefix))   return false
        if (extra?.invoke(s) == false)                                           return false
        return true
    }
}
