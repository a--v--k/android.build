/*
 * Copyright 2019 AK2.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ak2.android.build.flavors

import com.android.build.gradle.BaseExtension
import org.ak2.android.build.AndroidVersion
import org.ak2.android.build.AndroidVersion.*
import org.ak2.android.build.configurators.ProjectConfiguration
import org.ak2.android.build.configurators.androidProject
import org.ak2.android.build.configurators.effectiveTargetSdkVersionCode
import org.ak2.android.build.configurators.studioConfig

enum class AndroidPlatforms (
    val minSdkVersion : AndroidVersion,
    val maxSdkVersion : AndroidVersion? = null
) : Flavor {

    android2x    (minSdkVersion = GINGERBREAD,              maxSdkVersion = GINGERBREAD_MR1         ),
    android3x    (minSdkVersion = HONEYCOMB,                maxSdkVersion = HONEYCOMB_MR2           ),
    android4x    (minSdkVersion = ICE_CREAM_SANDWICH,       maxSdkVersion = KITKAT                  ),
    android5x    (minSdkVersion = KITKAT_WATCH,             maxSdkVersion = LOLLIPOP_MR1            ),
    android6x    (minSdkVersion = M,                        maxSdkVersion = M                       ),
    android7x    (minSdkVersion = N,                        maxSdkVersion = N_MR1                   ),
    android8x    (minSdkVersion = O,                        maxSdkVersion = O                       ),
    android9x    (minSdkVersion = P,                        maxSdkVersion = P                       ),
    android10x   (minSdkVersion = Q,                        maxSdkVersion = Q                       ),

    android2x3x  (minSdkVersion = android2x.minSdkVersion,  maxSdkVersion = android3x.maxSdkVersion ),
    android2x3x4x(minSdkVersion = android2x.minSdkVersion,  maxSdkVersion = android4x.maxSdkVersion ),
    android4xx   (minSdkVersion = android4x.minSdkVersion                                           ),
    android41x   (minSdkVersion = ANDROID_4_1                                                       ),
    android5xx   (minSdkVersion = android5x.minSdkVersion                                           ),
    android6xx   (minSdkVersion = android6x.minSdkVersion                                           ),
    android7xx   (minSdkVersion = android7x.minSdkVersion                                           ),
    android8xx   (minSdkVersion = android8x.minSdkVersion                                           ),
    android9xx   (minSdkVersion = android9x.minSdkVersion                                           ),
    android10xx  (minSdkVersion = android10x.minSdkVersion                                          ),

    all          (minSdkVersion = android2x.minSdkVersion                                           );

    override val dimensionName = "AndroidPlatform"

    override fun configure(android: BaseExtension, projectConfig: ProjectConfiguration) {
        val productFlavor = android.productFlavors.create(name)
        productFlavor.dimension = dimensionName

        if (android.androidProject.studioConfig.defaultAndroidPlatform == this) {
            productFlavor.isDefault = true
        }

        this.minSdkVersion.let(AndroidVersion::code).let(productFlavor::setMinSdkVersion)
        this.maxSdkVersion?.let(AndroidVersion::code)?.let(productFlavor::setMaxSdkVersion)

        val targetSdkVersion = Math.min(this.maxSdkVersion?.code ?: Integer.MAX_VALUE, projectConfig.effectiveTargetSdkVersionCode)
        productFlavor.setTargetSdkVersion(targetSdkVersion)
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

    companion object {
        fun of(name : String?) = values().find { it.name.equals(name, ignoreCase = true) }
    }
}