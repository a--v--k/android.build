package org.ak2.android.build.dependencies.base

enum class DependencyScope(val scope : String) {

    API                     ("api"),
    IMPLEMENTATION          ("implementation"),
    COMPILE_ONLY            ("compileOnly"),
    TEST_COMPILE            ("testImplementation"),
    TEST_RUNTIME            ("testRuntime"),
    ANNOTATION_PROCESSOR    ("annotationProcessor"),
    KAPT                    ("kapt");

    fun scopeName(appName : String?) = if (appName != null) appName + scope.capitalize() else scope
}