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
package org.eclipse.scout.sdk.operation.service;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.scout.sdk.extensions.runtime.classes.IRuntimeClasses;
import org.eclipse.scout.sdk.sourcebuilder.SortedMemberKeyFactory;
import org.eclipse.scout.sdk.sourcebuilder.method.IMethodBodySourceBuilder;
import org.eclipse.scout.sdk.sourcebuilder.method.IMethodSourceBuilder;
import org.eclipse.scout.sdk.sourcebuilder.method.MethodBodySourceBuilderFactory;
import org.eclipse.scout.sdk.sourcebuilder.method.MethodSourceBuilderFactory;
import org.eclipse.scout.sdk.sourcebuilder.type.TypeSourceBuilder;
import org.eclipse.scout.sdk.util.ScoutUtility;
import org.eclipse.scout.sdk.util.signature.IImportValidator;
import org.eclipse.scout.sdk.util.type.TypeUtility;
import org.eclipse.scout.sdk.util.typecache.IWorkingCopyManager;

public class LookupServiceNewOperation extends ServiceNewOperation {

  public LookupServiceNewOperation(String serviceName) {
    this("I" + serviceName, serviceName);
  }

  /**
   * @param serviceInterfaceName
   * @param serviceName
   */
  public LookupServiceNewOperation(String serviceInterfaceName, String serviceName) {
    super(serviceInterfaceName, serviceName);
  }

  @Override
  public String getOperationName() {
    return "New Lookup service '" + getImplementationName() + "'...";
  }

  @Override
  public void validate() throws IllegalArgumentException {
    super.validate();

  }

  @Override
  public void run(IProgressMonitor monitor, IWorkingCopyManager workingCopyManager) throws CoreException {
    TypeSourceBuilder serviceSourceBuilder = getImplementationSourceBuilder();
    IType serviceSuperType = TypeUtility.getTypeBySignature(serviceSourceBuilder.getSuperTypeSignature());
    if (TypeUtility.getSupertypeHierarchy(serviceSuperType).contains(TypeUtility.getType(IRuntimeClasses.AbstractSqlLookupService))) {
      IMethodSourceBuilder getConfiguredSqlSelectMethodBuilder = MethodSourceBuilderFactory.createOverrideMethodSourceBuilder(serviceSourceBuilder, "getConfiguredSqlSelect");
      getConfiguredSqlSelectMethodBuilder.setMethodBodySourceBuilder(new IMethodBodySourceBuilder() {
        @Override
        public void createSource(IMethodSourceBuilder methodBuilder, StringBuilder source, String lineDelimiter, IJavaProject ownerProject, IImportValidator validator) throws CoreException {
          source.append("return \"\"; ").append(ScoutUtility.getCommentBlock("write select statement here.")).append(lineDelimiter);
        }
      });
      serviceSourceBuilder.addSortedMethodSourceBuilder(SortedMemberKeyFactory.createMethodGetConfiguredKey(getConfiguredSqlSelectMethodBuilder), getConfiguredSqlSelectMethodBuilder);
    }
    else {
      IMethodSourceBuilder getDataByAllMethodBuilder = MethodSourceBuilderFactory.createOverrideMethodSourceBuilder(serviceSourceBuilder, "getDataByAll");
      getDataByAllMethodBuilder.setMethodBodySourceBuilder(MethodBodySourceBuilderFactory.createAutoGeneratedMethodBody());
      serviceSourceBuilder.addSortedMethodSourceBuilder(SortedMemberKeyFactory.createMethodGetConfiguredKey(getDataByAllMethodBuilder), getDataByAllMethodBuilder);

      IMethodSourceBuilder getDataByKeyMethodBuilder = MethodSourceBuilderFactory.createOverrideMethodSourceBuilder(serviceSourceBuilder, "getDataByKey");
      getDataByKeyMethodBuilder.setMethodBodySourceBuilder(MethodBodySourceBuilderFactory.createAutoGeneratedMethodBody());
      serviceSourceBuilder.addSortedMethodSourceBuilder(SortedMemberKeyFactory.createMethodGetConfiguredKey(getDataByKeyMethodBuilder), getDataByKeyMethodBuilder);

      IMethodSourceBuilder getDataByRecMethodBuilder = MethodSourceBuilderFactory.createOverrideMethodSourceBuilder(serviceSourceBuilder, "getDataByRec");
      getDataByRecMethodBuilder.setMethodBodySourceBuilder(MethodBodySourceBuilderFactory.createAutoGeneratedMethodBody());
      serviceSourceBuilder.addSortedMethodSourceBuilder(SortedMemberKeyFactory.createMethodGetConfiguredKey(getDataByRecMethodBuilder), getDataByRecMethodBuilder);

      IMethodSourceBuilder getDataByTextMethodBuilder = MethodSourceBuilderFactory.createOverrideMethodSourceBuilder(serviceSourceBuilder, "getDataByText");
      getDataByTextMethodBuilder.setMethodBodySourceBuilder(MethodBodySourceBuilderFactory.createAutoGeneratedMethodBody());
      serviceSourceBuilder.addSortedMethodSourceBuilder(SortedMemberKeyFactory.createMethodGetConfiguredKey(getDataByTextMethodBuilder), getDataByTextMethodBuilder);
    }

    super.run(monitor, workingCopyManager);
  }
}
