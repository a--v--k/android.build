package org.ak2.android.build.flavors

import com.android.build.gradle.BaseExtension
import org.ak2.android.build.AndroidVersion
import org.ak2.android.build.AndroidVersion.*

private val TARGET_SDK_VERSION = ANDROID_9_0

enum class AndroidPlatforms (
    val minSdkVersion : org.ak2.android.build.AndroidVersion,
    val targetSdkVersion : org.ak2.android.build.AndroidVersion = TARGET_SDK_VERSION,
    val maxSdkVersion : org.ak2.android.build.AndroidVersion? = null
) : Flavor {

    android2x    (minSdkVersion = GINGERBREAD,              maxSdkVersion = GINGERBREAD_MR1         ),
    android3x    (minSdkVersion = HONEYCOMB,                maxSdkVersion = HONEYCOMB_MR2           ),
    android4x    (minSdkVersion = ICE_CREAM_SANDWICH,       maxSdkVersion = KITKAT                  ),
    android5x    (minSdkVersion = KITKAT_WATCH,             maxSdkVersion = LOLLIPOP_MR1            ),
    android6x    (minSdkVersion = M,                        maxSdkVersion = M                       ),
    android7x    (minSdkVersion = N,                        maxSdkVersion = N_MR1                   ),
    android8x    (minSdkVersion = O,                        maxSdkVersion = O                       ),
    android9x    (minSdkVersion = P,                        maxSdkVersion = P                       ),

    android2x3x  (minSdkVersion = android2x.minSdkVersion,  maxSdkVersion = android3x.maxSdkVersion ),
    android2x3x4x(minSdkVersion = android2x.minSdkVersion,  maxSdkVersion = android4x.maxSdkVersion ),
    android4xx   (minSdkVersion = android4x.minSdkVersion                                           ),
    android41x   (minSdkVersion = ANDROID_4_1                                                       ),
    android5xx   (minSdkVersion = android5x.minSdkVersion                                           ),
    android6xx   (minSdkVersion = android6x.minSdkVersion                                           ),
    android7xx   (minSdkVersion = android7x.minSdkVersion                                           ),
    android8xx   (minSdkVersion = android8x.minSdkVersion                                           ),
    android9xx   (minSdkVersion = android9x.minSdkVersion                                           ),

    all          (minSdkVersion = android2x.minSdkVersion                                           );

    override val dimensionName = "AndroidPlatform"

    override fun configure(android: BaseExtension) {
        val productFlavor = android.productFlavors.create(name)
        productFlavor.dimension = dimensionName

        this.minSdkVersion.let(org.ak2.android.build.AndroidVersion::code).let(productFlavor::setMinSdkVersion)
        this.targetSdkVersion.let(org.ak2.android.build.AndroidVersion::code).let(productFlavor::setTargetSdkVersion)
        this.maxSdkVersion?.let(org.ak2.android.build.AndroidVersion::code)?.let(productFlavor::setMaxSdkVersion)

    }

    override fun canUseAsDependency(that: Flavor): Boolean {
        if (that is AndroidPlatforms) {
            val thisMinVersion = this.minSdkVersion.code
            val thatMinVersion = that.minSdkVersion.code

            val thisMaxVersion = this.maxSdkVersion?.code ?: Integer.MAX_VALUE
            val thatMaxVersion = that.maxSdkVersion?.code ?: Integer.MAX_VALUE

            return  thatMinVersion <= thisMinVersion && thisMaxVersion <= thatMaxVersion
        }

        return false
    }
}