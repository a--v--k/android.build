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

    object R22 : NdkVersion("22.0.7026061")
    object R21 : NdkVersion("21.3.6528147")
    object R20 : NdkVersion("20.1.5948944")
    object R19 : NdkVersion("19.2.5345600")
    object R18 : NdkVersion("18.1.5063045")
    object R17 : NdkVersion("17.2.4988734")
    object R16 : NdkVersion("16.1.4479499")
    class Custom(version: String) : NdkVersion(version)
}