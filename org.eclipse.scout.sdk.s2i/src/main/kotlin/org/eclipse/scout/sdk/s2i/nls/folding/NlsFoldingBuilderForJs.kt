/*
 * Copyright (c) 2010-2022 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.sdk.s2i.nls.folding

import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.lang.javascript.psi.*
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.eclipse.scout.sdk.core.s.nls.manager.TranslationManager
import org.eclipse.scout.sdk.core.s.nls.query.TranslationPatterns.JsModelTextKeyPattern
import org.eclipse.scout.sdk.s2i.nls.TranslationLanguageSpec.Companion.translationSpec

class NlsFoldingBuilderForJs : AbstractNlsFoldingBuilder() {

    override fun buildFoldRegions(root: PsiElement, manager: TranslationManager): List<FoldingDescriptor> {
        val folds = ArrayList<FoldingDescriptor>()
        root.accept(object : JSRecursiveWalkingElementVisitor() {
            override fun visitJSLiteralExpression(expression: JSLiteralExpression) {
                visitElement(expression)
            }

            override fun visitJSReferenceExpression(node: JSReferenceExpression) {
                visitElement(node)
            }

            private fun visitElement(element: JSElement) {
                val translationKey = element.translationSpec()?.resolveTranslationKey() ?: return
                createFoldingDescriptor(translationKey, element, manager)?.let { folds.add(it) }
            }
        })
        return folds
    }

    override fun textPrefixAndSuffix() = "'"

    private fun createFoldingDescriptor(key: String, element: PsiElement, manager: TranslationManager): FoldingDescriptor? {
        val isJsonTextKey = element is JSLiteralExpression && element.stringValue?.startsWith(JsModelTextKeyPattern.MODEL_TEXT_KEY_PREFIX) == true
        val psiElement = if (isJsonTextKey) element else PsiTreeUtil.getParentOfType(element, JSCallExpression::class.java) ?: return null
        return createFoldingDescriptor(psiElement, key, manager)
    }
}