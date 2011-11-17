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
package org.eclipse.scout.sdk.operation.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.scout.sdk.operation.annotation.OrderAnnotationCreateOperation;
import org.eclipse.scout.sdk.operation.annotation.OrderAnnotationsUpdateOperation;
import org.eclipse.scout.sdk.util.type.TypeFilters;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.util.typecache.ITypeHierarchy;
import org.eclipse.scout.sdk.util.typecache.IWorkingCopyManager;
import org.eclipse.scout.sdk.workspace.type.ScoutTypeComparators;

/**
 *
 */
public class OrderedInnerTypeNewOperation extends InnerTypeNewOperation {

  private IType m_orderDefinitionType;
  private double m_orderNr;

  /**
   * @param name
   * @param declaringType
   */
  public OrderedInnerTypeNewOperation(String name, IType declaringType) {
    super(name, declaringType);
  }

  /**
   * @param name
   * @param declaringType
   * @param formatSource
   */
  public OrderedInnerTypeNewOperation(String name, IType declaringType, boolean formatSource) {
    super(name, declaringType, formatSource);
  }

  @Override
  public void run(IProgressMonitor monitor, IWorkingCopyManager workingCopyManager) throws CoreException {
    updateOrderNumbers(monitor, workingCopyManager);
    addAnnotation(new OrderAnnotationCreateOperation(null, m_orderNr));
    super.run(monitor, workingCopyManager);
  }

  protected void updateOrderNumbers(IProgressMonitor monitor, IWorkingCopyManager manager) throws IllegalArgumentException, CoreException {
    m_orderNr = -1.0;
    if (getOrderDefinitionType() != null) {
      ITypeHierarchy typeHierarchy = TypeUtility.getLocalTypeHierarchy(getDeclaringtype());
      IType[] innerTypes = TypeUtility.getInnerTypes(getDeclaringtype(), TypeFilters.getSubtypeFilter(getOrderDefinitionType(), typeHierarchy), ScoutTypeComparators.getOrderAnnotationComparator());
      OrderAnnotationsUpdateOperation orderAnnotationOp = new OrderAnnotationsUpdateOperation(getDeclaringtype());
      double tempOrderNr = 10.0;
      for (IType innerType : innerTypes) {
        if (innerType.equals(getSibling())) {
          m_orderNr = tempOrderNr;
          tempOrderNr += 10.0;
        }
        orderAnnotationOp.addOrderAnnotation(innerType, tempOrderNr);
        tempOrderNr += 10.0;
      }
      if (m_orderNr < 0) {
        m_orderNr = tempOrderNr;
      }
      orderAnnotationOp.validate();
      orderAnnotationOp.run(monitor, manager);
      manager.reconcile(getDeclaringtype().getCompilationUnit(), monitor);

    }

  }

  public void setOrderDefinitionType(IType orderDefinitionType) {
    m_orderDefinitionType = orderDefinitionType;
  }

  public IType getOrderDefinitionType() {
    return m_orderDefinitionType;
  }

}
