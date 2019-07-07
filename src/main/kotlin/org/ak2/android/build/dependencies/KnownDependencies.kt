package org.ak2.android.build.dependencies

import com.android.build.gradle.BaseExtension
import org.ak2.android.build.DependenciesConfigurator.*
import org.ak2.android.build.configurators.Modules
import org.ak2.android.build.dependencies.base.*
import java.util.*
import kotlin.collections.ArrayList

class KnownDependencies : DependencyBuilder {

    private val _dependencies = TreeMap<DependencyScope, ScopedDependenciesImpl>()

    init {
        compileOnly += GoogleSupport.Support.Annotations
    }

    override val api             : ScopedDependencies get() = _dependencies.computeIfAbsent(DependencyScope.API, ::ScopedDependenciesImpl)
    override val implementation  : ScopedDependencies get() = _dependencies.computeIfAbsent(DependencyScope.IMPLEMENTATION, ::ScopedDependenciesImpl)
    override val compileOnly     : ScopedDependencies get() = _dependencies.computeIfAbsent(DependencyScope.COMPILE_ONLY, ::ScopedDependenciesImpl)
    override val test            : ScopedDependencies get() = _dependencies.computeIfAbsent(DependencyScope.TEST_COMPILE, ::ScopedDependenciesImpl)
    override val testRuntime     : ScopedDependencies get() = _dependencies.computeIfAbsent(DependencyScope.TEST_RUNTIME, ::ScopedDependenciesImpl)

    override fun module(alias: String) = Modules.module(alias)

    override fun modules(vararg aliases: String) = aliases.map(this::module)

    override fun library(dependency: String) = LibraryDependencyKt(dependency)

    override fun local(localPath: String) = LocalJarDependencyKt(localPath)

    fun configure(appName: String? = null, android: BaseExtension) {
        _dependencies.values.forEach { it.configure(appName, android) }
    }
}

class ScopedDependenciesImpl(override val scope: DependencyScope) : ScopedDependencies {

    private val _dependencies = ArrayList<DependencyKt>()

    override operator fun plusAssign(dep: DependencyKt) {
        _dependencies.add(dep)
    }

    override operator fun plusAssign(deps: List<DependencyKt>) {
        _dependencies.addAll(deps)
    }

    fun configure(appName: String?, android: BaseExtension) {
        _dependencies.forEach { it.configure(appName, scope, android) }
    }
}
