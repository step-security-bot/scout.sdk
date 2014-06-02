/*******************************************************************************
 * Copyright (c) 2010 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.sdk.ui.view.outline.pages;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.scout.sdk.util.jdt.IJavaResourceChangedListener;
import org.eclipse.scout.sdk.util.jdt.JdtEvent;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.workspace.type.ScoutTypeUtility;

/**
 *
 */
public class InnerTypeOrderChangedPageDirtyListener implements IJavaResourceChangedListener {

  private final IPage m_page;
  private final IType m_innerTypeSuperType;
  private final IType m_declaringType;

  public InnerTypeOrderChangedPageDirtyListener(IPage page, IType innerTypeSuperType, IType declaringType) {
    m_page = page;
    m_innerTypeSuperType = innerTypeSuperType;
    m_declaringType = declaringType;
  }

  @Override
  public void handleEvent(JdtEvent event) {
    if (event.getElementType() == IJavaElement.ANNOTATION) {
      if (TypeUtility.exists(event.getElement())) {
        IAnnotation annotation = (IAnnotation) event.getElement();
        IJavaElement annotationOwner = annotation.getParent();
        ITypeHierarchy superTypeHierarchy = event.getSuperTypeHierarchy();
        if (superTypeHierarchy != null && superTypeHierarchy.contains(getInnerTypeSuperType())) {
          if (TypeUtility.exists(annotationOwner) && annotationOwner.getParent().equals(getDeclaringType())) {
            getPage().markStructureDirty();
          }
        }
      }
    }
    else if (event.getElementType() == IJavaElement.TYPE) {
      IType eventType = (IType) event.getElement();
      int i = -1;
      for (IPage p : getPage().getChildren()) {
        if (p instanceof ITypePage) {
          i++;
          ITypePage tp = (ITypePage) p;
          if (eventType.equals(tp.getType())) {
            break;
          }
        }
      }

      if (i >= 0) {
        IType[] innerTypesOrdered = ScoutTypeUtility.getInnerTypesOrdered(getDeclaringType(), getInnerTypeSuperType());
        if (innerTypesOrdered.length > i) {
          if (!innerTypesOrdered[i].equals(eventType)) {
            getPage().markStructureDirty();
          }
        }
        else {
          getPage().markStructureDirty();
        }
      }
    }
  }

  public IPage getPage() {
    return m_page;
  }

  public IType getInnerTypeSuperType() {
    return m_innerTypeSuperType;
  }

  public IType getDeclaringType() {
    return m_declaringType;
  }
}
