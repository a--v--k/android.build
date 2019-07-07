package org.ak2.android.build.configurators

import java.util.*

class AppVersionKt(
        var versionName: String? = null,
        var majorVersionCode: Int = 0,
        var minorVersionCode: Int = 0
) {

    val versionCode: Int
        get() = majorVersionCode * 1000 + minorVersionCode

    fun fromProperties(p: Properties) = apply {
        versionName      = p.getProperty("package.version.name")
        majorVersionCode = Integer.parseInt(p.getProperty("package.version.code.major"))
        minorVersionCode = Integer.parseInt(p.getProperty("package.version.code.minor"))
    }
}