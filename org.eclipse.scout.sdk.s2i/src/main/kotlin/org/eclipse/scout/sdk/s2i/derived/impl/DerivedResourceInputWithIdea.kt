package org.eclipse.scout.sdk.s2i.derived.impl

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import org.eclipse.scout.sdk.core.log.SdkLog
import org.eclipse.scout.sdk.core.model.api.IClasspathEntry
import org.eclipse.scout.sdk.core.model.api.IType
import org.eclipse.scout.sdk.core.model.api.MissingTypeException
import org.eclipse.scout.sdk.core.s.derived.IDerivedResourceInput
import org.eclipse.scout.sdk.core.s.environment.IEnvironment
import org.eclipse.scout.sdk.s2i.environment.IdeaEnvironment
import org.eclipse.scout.sdk.s2i.resolvePsi
import org.eclipse.scout.sdk.s2i.resolveSourceRoot
import org.eclipse.scout.sdk.s2i.toIdea
import java.util.*


open class DerivedResourceInputWithIdea(val type: PsiClass) : IDerivedResourceInput {

    override fun getSourceType(env: IEnvironment): Optional<IType> =
            try {
                Optional.ofNullable(env.toIdea().psiClassToScoutType(type))
            } catch (e: MissingTypeException) {
                SdkLog.info("Unable to update DTO for '{}' because there are compile errors in the compilation unit.", toString(), e)
                Optional.empty()
            }

    override fun getSourceFolderOf(t: IType, env: IEnvironment): Optional<IClasspathEntry> =
            Optional.ofNullable(t.resolvePsi()
                    ?.resolveSourceRoot()
                    ?.let { env.toIdea().findClasspathEntry(it) })


    protected fun project(): Project = type.project

    override fun toString(): String = IdeaEnvironment.computeInReadAction(project()) {
        type.nameIdentifier?.text ?: type.toString()
    }
}
