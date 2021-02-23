/*
 * Copyright (c) 2010-2021 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.sdk.s2i.maven

import org.eclipse.scout.sdk.core.s.project.ScoutProjectNewHelper
import org.eclipse.scout.sdk.core.s.util.maven.IMavenConstants
import org.eclipse.scout.sdk.s2i.EclipseScoutBundle.message
import org.jetbrains.idea.maven.indices.MavenArchetypesProvider
import org.jetbrains.idea.maven.model.MavenArchetype


class ScoutArchetypesProvider : MavenArchetypesProvider {
    override fun getArchetypes(): Collection<MavenArchetype> = getArchetypeDescriptions()
            .map { MavenArchetype(ScoutProjectNewHelper.SCOUT_ARCHETYPES_GROUP_ID, it.artifactId, IMavenConstants.LATEST, null, it.description) }

    private fun getArchetypeDescriptions(): Collection<ArchetypeDescription> = listOf(
            ArchetypeDescription(ScoutProjectNewHelper.SCOUT_ARCHETYPES_HELLOJS_ARTIFACT_ID, message("archetype.hellojs.description")),
            ArchetypeDescription(ScoutProjectNewHelper.SCOUT_ARCHETYPES_HELLOWORLD_ARTIFACT_ID, message("archetype.helloworld.description")))

    private data class ArchetypeDescription(val artifactId: String, val description: String)
}