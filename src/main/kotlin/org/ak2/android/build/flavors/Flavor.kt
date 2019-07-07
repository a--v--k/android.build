package org.ak2.android.build.flavors

import com.android.build.gradle.BaseExtension

interface Flavor {

    val name: String

    val dimensionName: String

    fun configure(android: BaseExtension)

    fun canUseAsDependency(that: Flavor): Boolean
}

