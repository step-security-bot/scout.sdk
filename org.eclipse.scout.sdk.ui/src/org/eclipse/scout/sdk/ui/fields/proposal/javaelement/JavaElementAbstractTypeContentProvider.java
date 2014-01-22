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
package org.eclipse.scout.sdk.ui.fields.proposal.javaelement;

import java.util.ArrayList;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.scout.sdk.util.type.ITypeFilter;
import org.eclipse.scout.sdk.util.type.TypeComparators;
import org.eclipse.scout.sdk.util.type.TypeFilters;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.util.typecache.ICachedTypeHierarchy;

/**
 * <h3>{@link JavaElementAbstractTypeContentProvider}</h3> ...
 * 
 * @author Matthias Villiger
 * @since 3.8.0 20.04.2012
 */
public class JavaElementAbstractTypeContentProvider extends AbstractJavaElementContentProvider {

  private final IType m_superType;
  private final IJavaProject m_project;
  private final IType[] m_mostlyUsed;
  private ITypeFilter m_filter;

  public JavaElementAbstractTypeContentProvider(IType superType, IJavaProject project, IType... mostlyUsed) {
    this(superType, project, null, mostlyUsed);
  }

  public JavaElementAbstractTypeContentProvider(IType superType, IJavaProject project, ITypeFilter filter, IType... mostlyUsed) {
    m_superType = superType;
    m_project = project;
    m_mostlyUsed = mostlyUsed;
    m_filter = filter;
  }

  @Override
  protected Object[][] computeProposals() {
    ICachedTypeHierarchy typeHierarchy = TypeUtility.getPrimaryTypeHierarchy(m_superType);
    ITypeFilter filter = null;
    IType[] mostlyUsed = null;
    if (getFilter() == null) {
      filter = TypeFilters.getMultiTypeFilter(TypeFilters.getNotInTypes(m_mostlyUsed), TypeFilters.getAbstractOnClasspath(m_project));
      mostlyUsed = m_mostlyUsed;
    }
    else {
      filter = TypeFilters.getMultiTypeFilter(TypeFilters.getNotInTypes(m_mostlyUsed), TypeFilters.getAbstractOnClasspath(m_project), getFilter());

      // filter the mostly used
      if (m_mostlyUsed != null) {
        ArrayList<Object> mu = new ArrayList<Object>(m_mostlyUsed.length);
        for (IType o : m_mostlyUsed) {
          if (TypeUtility.exists(o) && getFilter().accept(o)) {
            mu.add(o);
          }
        }
        mostlyUsed = mu.toArray(new IType[mu.size()]);
      }
    }
    IType[] abstractTypes = typeHierarchy.getAllSubtypes(m_superType, filter, TypeComparators.getTypeNameComparator());

    return new Object[][]{mostlyUsed, abstractTypes};
  }

  public ITypeFilter getFilter() {
    return m_filter;
  }

  public void setFilter(ITypeFilter filter) {
    m_filter = filter;
    invalidateCache();
  }
}
