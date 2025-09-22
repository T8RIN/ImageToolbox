/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.the

val Project.libs
    get(): LibrariesForLibs = the<LibrariesForLibs>()

val Project.projects
    get(): Projects = object : Projects, ProjectHolder {
        override fun project(path: String): Project = this@projects.project(path)
    }

val Projects.core
    get(): CoreProjects = object : CoreProjects, ProjectHolder by this {}

val CoreProjects.data
    get(): ProjectLibrary = project(":core:data")

val CoreProjects.ui
    get(): ProjectLibrary = project(":core:ui")

val CoreProjects.domain
    get(): ProjectLibrary = project(":core:domain")

val CoreProjects.resources
    get(): ProjectLibrary = project(":core:resources")

val CoreProjects.settings
    get(): ProjectLibrary = project(":core:settings")

val CoreProjects.di
    get(): ProjectLibrary = project(":core:di")

val CoreProjects.crash
    get(): ProjectLibrary = project(":core:crash")

val CoreProjects.utils
    get(): ProjectLibrary = project(":core:utils")

fun DependencyHandlerScope.implementation(
    dependency: Library
) = add("implementation", dependency)

fun DependencyHandlerScope.coreLibraryDesugaring(
    dependency: Library
) = add("coreLibraryDesugaring", dependency)

fun DependencyHandlerScope.implementation(
    dependency: ProjectLibrary
) = add("implementation", dependency)

fun DependencyHandlerScope.ksp(
    dependency: Library
) = add("ksp", dependency)

fun DependencyHandlerScope.detektPlugins(
    dependency: Library
) = add("detektPlugins", dependency)

typealias Library = Provider<MinimalExternalModuleDependency>

typealias ProjectLibrary = Project

interface Projects : ProjectHolder

interface CoreProjects : ProjectHolder

interface ProjectHolder {
    fun project(path: String): Project
}