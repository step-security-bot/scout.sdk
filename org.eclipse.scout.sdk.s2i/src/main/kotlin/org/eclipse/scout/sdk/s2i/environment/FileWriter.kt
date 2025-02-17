/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.s2i.environment

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import org.eclipse.scout.sdk.core.log.SdkLog
import org.eclipse.scout.sdk.s2i.EclipseScoutBundle.message
import org.eclipse.scout.sdk.s2i.toVirtualFile
import java.nio.file.Path

open class FileWriter(val targetFile: Path, private val content: CharSequence, val project: Project) : TransactionMember {

    override fun file(): Path = targetFile

    override fun commit(progress: IdeaProgress): Boolean {
        progress.init(4, toString())

        var existingFile = targetFile.toVirtualFile()
        progress.worked(1)
        if (existingFile == null || !existingFile.exists() || !existingFile.isValid) {
            // new file
            val dir = VfsUtil.createDirectoryIfMissing(targetFile.parent.toString())
            if (dir == null) {
                SdkLog.warning("Cannot write '$targetFile' because the directory could not be created.")
                return false
            }
            progress.worked(1)
            SdkLog.debug("Adding new file '{}'.", targetFile)
            existingFile = dir.createChildData(this, targetFile.fileName.toString())
            progress.worked(1)
        }
        progress.setWorkRemaining(1)

        val documentManager = FileDocumentManager.getInstance()
        val document = documentManager.getDocument(existingFile)
        if (document == null) {
            SdkLog.warning("Cannot load document for file '{}' to change its content.", targetFile)
            return false
        }
        document.setText(content)
        progress.worked(1)
        return true
    }

    override fun replaces(member: TransactionMember): Boolean {
        return member is FileWriter
    }

    override fun toString() = message("write.file.x", targetFile)
}