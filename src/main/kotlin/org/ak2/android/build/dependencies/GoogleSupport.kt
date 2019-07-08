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

package org.ak2.android.build.dependencies

import org.ak2.android.build.dependencies.base.ContraintLayoutDependencyKt
import org.ak2.android.build.dependencies.base.GoogleSupportAnnotations
import org.ak2.android.build.dependencies.base.GoogleSupportDependencyKt

typealias use = GoogleSupportDependencyKt
typealias constraint = ContraintLayoutDependencyKt

object GoogleSupport {

    object Arch {
        object Core {
            val Common      = use("android.arch.core:common")
            val Core        = use("android.arch.core:core")
            val CoreTesting = use("android.arch.core:core-testing")
            val Runtime     = use("android.arch.core:runtime")
        }

        object Lifecycle {
            val Common          = use("android.arch.lifecycle:common")
            val CommonJava8     = use("android.arch.lifecycle:common-java8")
            val Compiler        = use("android.arch.lifecycle:compiler")
            val Extensions      = use("android.arch.lifecycle:extensions")
            val LiveData        = use("android.arch.lifecycle:livedata")
            val LiveDataCore    = use("android.arch.lifecycle:livedata-core")
            val ReactiveStreams = use("android.arch.lifecycle:reactivestreams")
            val Runtime         = use("android.arch.lifecycle:runtime")
            val ViewModel       = use("android.arch.lifecycle:viewmodel")
        }

        object Paging {
            val Common  = use("android.arch.paging:common")
            val Runtine = use("android.arch.paging:runtime")
            val Rx      = use("android.arch.paging:rxjava2")
        }

        object Persistence {
            object Room {
                val Common      = use("android.arch.persistence.room:common")
                val Compiler    = use("android.arch.persistence.room:compiler")
                val Guava       = use("android.arch.persistence.room:guava")
                val Migration   = use("android.arch.persistence.room:migration")
                val Runtime     = use("android.arch.persistence.room:runtime")
                val Rx          = use("android.arch.persistence.room:rxjava2")
                val Testing     = use("android.arch.persistence.room:testing")
            }

            val Db          = use("android.arch.persistence:db")
            val DbFramework = use("android.arch.persistence:db-framework")
        }
    }

    object Drawables {
        val AnimatedVectorDrawable  = use("com.android.support:animated-vector-drawable")
        val VectorDrawable          = use("com.android.support:support-vector-drawable")
    }

    object Modes {
        val Car         = use("com.android.support:car")
        val Leanback    = use("com.android.support:leanback-v17")
        val Wear        = use("com.android.support:wear")
    }

    object Layouts {
        val AsynLayoutInflator  = use("com.android.support:asynclayoutinflater")
        val CoordiatorLayout    = use("com.android.support:coordinatorlayout")
        val DrawerLayout        = use("com.android.support:drawerlayout")
        val GridLayout          = use("com.android.support:gridlayout-v7")
        val PercentLayout       = use("com.android.support:percent")
        val SlidingPaneLayout   = use("com.android.support:slidingpanelayout")
        val SwipeRefreshLayout  = use("com.android.support:swiperefreshlayout")

        object Constraint {
            val Layout          = constraint("com.android.support.constraint:constraint-layout")
            val LayoutSolver    = constraint("com.android.support.constraint:constraint-layout-solver")
        }
    }

    object Media {
        val Compat          = use("com.android.support:support-media-compat")
        val Media2          = use("com.android.support:media2")
        val Media2Exoplayer = use("com.android.support:media2-exoplayer")
        val MediaRouter     = use("com.android.support:mediarouter-v7")
    }

    object Preferences {
        val Leanback    = use("com.android.support:preference-leanback-v17")
        val v14         = use("com.android.support:preference-v14")
        val v7          = use("com.android.support:preference-v7")
    }

    object Support {
        val Annotations         = GoogleSupportAnnotations()
        val AppCompat           = use("com.android.support:appcompat-v7")
        val Compat              = use("com.android.support:support-compat")
        val Content             = use("com.android.support:support-content")
        val CoreUI              = use("com.android.support:support-core-ui")
        val CoreUtils           = use("com.android.support:support-core-utils")
        val DynamicAnimations   = use("com.android.support:support-dynamic-animation")
        val Emoji               = use("com.android.support:support-emoji")
        val EmojiAppcompat      = use("com.android.support:support-emoji-appcompat")
        val EmojiBundled        = use("com.android.support:support-emoji-bundled")
        val Fragment            = use("com.android.support:support-fragment")
        val TvProvider          = use("com.android.support:support-tv-provider")
        val v13                 = use("com.android.support:support-v13")
        val v4                  = use("com.android.support:support-v4")
    }

    object Utils {
        val Collections             = use("com.android.support:collections")
        val CursorAdapter           = use("com.android.support:cursoradapter")
        val DocumentFile            = use("com.android.support:documentfile")
        val ExifInterface           = use("com.android.support:exifinterface")
        val HeifWriter              = use("com.android.support:heifwriter")
        val Interpolator            = use("com.android.support:interpolator")
        val Loader                  = use("com.android.support:loader")
        val LocalBroadcastManager   = use("com.android.support:localbroadcastmanager")
        val Print                   = use("com.android.support:print")
        val Recommentation          = use("com.android.support:recommendation")
        val TextClassifier          = use("com.android.support:textclassifier")
        val Transition              = use("com.android.support:transition")
        val VersionedParcelable     = use("com.android.support:versionedparcelable")
    }

    object Views {
        val CardView        = use("com.android.support:cardview-v7")
        val CustomTabs      = use("com.android.support:customtabs")
        val CustomView      = use("com.android.support:customview")
        val MaterialDesign  = use("com.android.support:design")
        val Palette         = use("com.android.support:palette-v7")
        val ViewPager       = use("com.android.support:viewpager")
        val Webkit          = use("com.android.support:webkit")


        object RecyclerViews {
            val View        = use("com.android.support:recyclerview-v7")
            val Selection   = use("com.android.support:recyclerview-selection")
        }

        object Slices {
            val Builders    = use("com.android.support:slices-builders")
            val Core        = use("com.android.support:slices-core")
            val View        = use("com.android.support:slices-view")
        }
    }

    object Test {
        object Espresso {
            object Idling {
                val Concurrent  = use("com.android.support.test.espresso.idling:idling-concurrent")
                val Net         = use("com.android.support.test.espresso.idling:idling-net")
            }

            val Accessibility   = use("com.android.support.test.espresso:espresso-accessibility")
            val Contributor     = use("com.android.support.test.espresso:espresso-contrib")
            val Core            = use("com.android.support.test.espresso:espresso-core")
            val IdlingResource  = use("com.android.support.test.espresso:espresso-idling-resource")
            val Intents         = use("com.android.support.test.espresso:espresso-intents")
            val Remote          = use("com.android.support.test.espresso:espresso-remote")
            val Web             = use("com.android.support.test.espresso:espresso-web")
        }

        val JankTestHelper      = use("com.android.support.test.janktesthelper:janktesthelper")
        val TestServices        = use("com.android.support.test.services:test-services")

        val UiAutomator         = use("com.android.support.test.uiautomator:uiautomator")
        val Monitor             = use("com.android.support.test:monitor")
        val Orchestrator        = use("com.android.support.test:orchestrator")
        val Rules               = use("com.android.support.test:rules")
        val Runner              = use("com.android.support.test:runner")
    }
}

