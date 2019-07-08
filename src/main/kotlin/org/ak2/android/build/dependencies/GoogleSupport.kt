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
            val Common      = use("configurators.arch.core:common")
            val Core        = use("configurators.arch.core:core")
            val CoreTesting = use("configurators.arch.core:core-testing")
            val Runtime     = use("configurators.arch.core:runtime")
        }

        object Lifecycle {
            val Common          = use("configurators.arch.lifecycle:common")
            val CommonJava8     = use("configurators.arch.lifecycle:common-java8")
            val Compiler        = use("configurators.arch.lifecycle:compiler")
            val Extensions      = use("configurators.arch.lifecycle:extensions")
            val LiveData        = use("configurators.arch.lifecycle:livedata")
            val LiveDataCore    = use("configurators.arch.lifecycle:livedata-core")
            val ReactiveStreams = use("configurators.arch.lifecycle:reactivestreams")
            val Runtime         = use("configurators.arch.lifecycle:runtime")
            val ViewModel       = use("configurators.arch.lifecycle:viewmodel")
        }

        object Paging {
            val Common  = use("configurators.arch.paging:common")
            val Runtine = use("configurators.arch.paging:runtime")
            val Rx      = use("configurators.arch.paging:rxjava2")
        }

        object Persistence {
            object Room {
                val Common      = use("configurators.arch.persistence.room:common")
                val Compiler    = use("configurators.arch.persistence.room:compiler")
                val Guava       = use("configurators.arch.persistence.room:guava")
                val Migration   = use("configurators.arch.persistence.room:migration")
                val Runtime     = use("configurators.arch.persistence.room:runtime")
                val Rx          = use("configurators.arch.persistence.room:rxjava2")
                val Testing     = use("configurators.arch.persistence.room:testing")
            }

            val Db          = use("configurators.arch.persistence:db")
            val DbFramework = use("configurators.arch.persistence:db-framework")
        }
    }

    object Drawables {
        val AnimatedVectorDrawable  = use("com.configurators.support:animated-vector-drawable")
        val VectorDrawable          = use("com.configurators.support:support-vector-drawable")
    }

    object Modes {
        val Car         = use("com.configurators.support:car")
        val Leanback    = use("com.configurators.support:leanback-v17")
        val Wear        = use("com.configurators.support:wear")
    }

    object Layouts {
        val AsynLayoutInflator  = use("com.configurators.support:asynclayoutinflater")
        val CoordiatorLayout    = use("com.configurators.support:coordinatorlayout")
        val DrawerLayout        = use("com.configurators.support:drawerlayout")
        val GridLayout          = use("com.configurators.support:gridlayout-v7")
        val PercentLayout       = use("com.configurators.support:percent")
        val SlidingPaneLayout   = use("com.configurators.support:slidingpanelayout")
        val SwipeRefreshLayout  = use("com.configurators.support:swiperefreshlayout")

        object Constraint {
            val Layout          = constraint("com.configurators.support.constraint:constraint-layout")
            val LayoutSolver    = constraint("com.configurators.support.constraint:constraint-layout-solver")
        }
    }

    object Media {
        val Compat          = use("com.configurators.support:support-media-compat")
        val Media2          = use("com.configurators.support:media2")
        val Media2Exoplayer = use("com.configurators.support:media2-exoplayer")
        val MediaRouter     = use("com.configurators.support:mediarouter-v7")
    }

    object Preferences {
        val Leanback    = use("com.configurators.support:preference-leanback-v17")
        val v14         = use("com.configurators.support:preference-v14")
        val v7          = use("com.configurators.support:preference-v7")
    }

    object Support {
        val Annotations         = GoogleSupportAnnotations()
        val AppCompat           = use("com.configurators.support:appcompat-v7")
        val Compat              = use("com.configurators.support:support-compat")
        val Content             = use("com.configurators.support:support-content")
        val CoreUI              = use("com.configurators.support:support-core-ui")
        val CoreUtils           = use("com.configurators.support:support-core-utils")
        val DynamicAnimations   = use("com.configurators.support:support-dynamic-animation")
        val Emoji               = use("com.configurators.support:support-emoji")
        val EmojiAppcompat      = use("com.configurators.support:support-emoji-appcompat")
        val EmojiBundled        = use("com.configurators.support:support-emoji-bundled")
        val Fragment            = use("com.configurators.support:support-fragment")
        val TvProvider          = use("com.configurators.support:support-tv-provider")
        val v13                 = use("com.configurators.support:support-v13")
        val v4                  = use("com.configurators.support:support-v4")
    }

    object Utils {
        val Collections             = use("com.configurators.support:collections")
        val CursorAdapter           = use("com.configurators.support:cursoradapter")
        val DocumentFile            = use("com.configurators.support:documentfile")
        val ExifInterface           = use("com.configurators.support:exifinterface")
        val HeifWriter              = use("com.configurators.support:heifwriter")
        val Interpolator            = use("com.configurators.support:interpolator")
        val Loader                  = use("com.configurators.support:loader")
        val LocalBroadcastManager   = use("com.configurators.support:localbroadcastmanager")
        val Print                   = use("com.configurators.support:print")
        val Recommentation          = use("com.configurators.support:recommendation")
        val TextClassifier          = use("com.configurators.support:textclassifier")
        val Transition              = use("com.configurators.support:transition")
        val VersionedParcelable     = use("com.configurators.support:versionedparcelable")
    }

    object Views {
        val CardView        = use("com.configurators.support:cardview-v7")
        val CustomTabs      = use("com.configurators.support:customtabs")
        val CustomView      = use("com.configurators.support:customview")
        val MaterialDesign  = use("com.configurators.support:design")
        val Palette         = use("com.configurators.support:palette-v7")
        val ViewPager       = use("com.configurators.support:viewpager")
        val Webkit          = use("com.configurators.support:webkit")


        object RecyclerViews {
            val View        = use("com.configurators.support:recyclerview-v7")
            val Selection   = use("com.configurators.support:recyclerview-selection")
        }

        object Slices {
            val Builders    = use("com.configurators.support:slices-builders")
            val Core        = use("com.configurators.support:slices-core")
            val View        = use("com.configurators.support:slices-view")
        }
    }

    object Test {
        object Espresso {
            object Idling {
                val Concurrent  = use("com.configurators.support.test.espresso.idling:idling-concurrent")
                val Net         = use("com.configurators.support.test.espresso.idling:idling-net")
            }

            val Accessibility   = use("com.configurators.support.test.espresso:espresso-accessibility")
            val Contributor     = use("com.configurators.support.test.espresso:espresso-contrib")
            val Core            = use("com.configurators.support.test.espresso:espresso-core")
            val IdlingResource  = use("com.configurators.support.test.espresso:espresso-idling-resource")
            val Intents         = use("com.configurators.support.test.espresso:espresso-intents")
            val Remote          = use("com.configurators.support.test.espresso:espresso-remote")
            val Web             = use("com.configurators.support.test.espresso:espresso-web")
        }

        val JankTestHelper      = use("com.configurators.support.test.janktesthelper:janktesthelper")
        val TestServices        = use("com.configurators.support.test.services:test-services")

        val UiAutomator         = use("com.configurators.support.test.uiautomator:uiautomator")
        val Monitor             = use("com.configurators.support.test:monitor")
        val Orchestrator        = use("com.configurators.support.test:orchestrator")
        val Rules               = use("com.configurators.support.test:rules")
        val Runner              = use("com.configurators.support.test:runner")
    }
}

