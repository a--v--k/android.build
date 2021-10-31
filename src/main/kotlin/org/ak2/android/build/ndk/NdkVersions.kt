/*
 * Copyright 2021 AK2.
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

package org.ak2.android.build.ndk

sealed class NdkVersion(val version: String) {

    constructor(version : NdkVersion) : this(version.version)

    object Latest : NdkVersion(R23)

    object R23 : NdkVersion(R23_1)
    object R22 : NdkVersion(R22_1)
    object R21 : NdkVersion(R21_4)
    object R20 : NdkVersion(R20_1)

    object R23_1: NdkVersion("23.1.7779620")
    object R23_0: NdkVersion("23.0.7599659")

    object R22_1 : NdkVersion("22.1.7171370")
    object R22_0 : NdkVersion("22.0.7026061")

    object R21_4 : NdkVersion("21.4.7026061")
    object R21_3 : NdkVersion("21.3.6528147")

    object R20_1 : NdkVersion("20.1.5948944")
    object R19_2 : NdkVersion("19.2.5345600")
    object R18_1 : NdkVersion("18.1.5063045")
    object R17_2 : NdkVersion("17.2.4988734")
    object R16_1 : NdkVersion("16.1.4479499")

    class Custom(version: String) : NdkVersion(version)
}