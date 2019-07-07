package org.ak2.android.build.flavors

import com.android.build.api.variant.VariantFilter
import com.android.build.gradle.BaseExtension
import org.ak2.android.build.configurators.*
import org.ak2.android.build.extras.doOnce
import org.ak2.android.build.extras.getExtraOrDefault
import org.ak2.android.build.flavors.AndroidPlatforms.android5x
import org.ak2.android.build.flavors.NativeAbiType._32
import org.ak2.android.build.flavors.NativeAbiType._64
import org.ak2.android.build.ndk.NativeOptions

const val ANDROID_MK = "src/main/jni/Android.mk"

enum class NativeAbiType {
    _32,
    _64
}

fun native32() = NativePlatforms.values().filter { it.abiType == _32}
fun native64() = NativePlatforms.values().filter { it.abiType == _64}

fun actual(platforms: List<NativePlatforms>) = platforms.filter { !it.depricated }

enum class NativePlatforms (
        val abi                 : String,
        val abiType             : NativeAbiType,
        val config              : NativeOptions = NativeOptions(),
        val depricated          : Boolean        = false,
        val minPlatformVersion  : org.ak2.android.build.AndroidVersion? = null
) : Flavor {

    arm5(abi =     "armeabi", abiType = _32, config = with { args("APP_ARM_MODE=arm")                                   }, depricated = true                           ),
    arm7(abi = "armeabi-v7a", abiType = _32, config = with { args("APP_ARM_MODE=arm"); c_flags("-march=armv7-a") }                                              ),
    mips(abi =        "mips", abiType = _32, config = with { args("TARGET_CPU_ABI=mips")                                }, depricated = true                           ),
    x86( abi =         "x86", abiType = _32                                                                                                                                   ),
    arm8(abi =   "arm64-v8a", abiType = _64, config = with { args("APP_ARM_MODE=arm")                                   }, minPlatformVersion = android5x.minSdkVersion),
    x64( abi =      "x86_64", abiType = _64,                                                                                      minPlatformVersion = android5x.minSdkVersion);


    override val dimensionName = "NativeAbi"

    override fun configure(android: BaseExtension) {
        require(getExtraOrDefault(android, "setupNdkBuild", false)) { "NDK build has not been supported"}

        val productFlavor = android.productFlavors.create(name)
        productFlavor.dimension = dimensionName

        productFlavor.externalNativeBuild {
            ndkBuild {
                ndkBuild.abiFilters.add(abi)
                config.configure(this)
            }
        }

        doOnce(android, "NativeAbiFlavorsFilter") {
            android.addVariantValidator { variant -> android.checkSdkVersions(variant) }
        }
    }

    override fun canUseAsDependency(that: Flavor): Boolean {
        return this == that
    }
}


fun BaseExtension.checkSdkVersions(variant : VariantFilter) : Boolean {
    val variantConfig = this.getVariantConfigs()[variant.name];
    if (variantConfig == null) {
        return false;
    }

    val nativeAbiFlavor = variantConfig.nativeFlavor
    val androidPlatformFlavor = variantConfig.androidFlavor

    val minPlatformVersion = nativeAbiFlavor?.minPlatformVersion?.code
    val minimalSdkVersion = androidPlatformFlavor?.minSdkVersion?.code

    val enabled = minimalSdkVersion == null || minPlatformVersion == null || minPlatformVersion <= minimalSdkVersion

    if (!enabled) {
        println("${androidProject.path}: variant ${variant.name} ignored : minimalSdkVersion=$minimalSdkVersion minPlatformVersion=$minPlatformVersion")
    } else {
        println("${androidProject.path}: variant ${variant.name} enabled : minimalSdkVersion=$minimalSdkVersion minPlatformVersion=$minPlatformVersion")
    }

    return enabled
}

private fun with(block : NativeOptions.() -> Unit) = NativeOptions().apply(block)

