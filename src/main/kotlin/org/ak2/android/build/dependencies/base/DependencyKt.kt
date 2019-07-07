package org.ak2.android.build.dependencies.base

import com.android.build.gradle.BaseExtension

interface DependencyKt {

    fun configure(appName: String? = null, scope: DependencyScope = DependencyScope.IMPLEMENTATION, android: BaseExtension)
}