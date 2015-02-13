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
package org.eclipse.scout.sdk.operation.jdt.method;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.scout.sdk.sourcebuilder.method.MethodSourceBuilderFactory;
import org.eclipse.scout.sdk.util.type.MethodParameter;
import org.eclipse.scout.sdk.util.type.TypeUtility;

/**
 *
 */
public class MethodOverrideOperation extends MethodNewOperation {

  public MethodOverrideOperation(String methodName, IType declaringType) throws CoreException {
    this(methodName, declaringType, true);
  }

  public MethodOverrideOperation(String methodName, IType declaringType, boolean formatSource) throws CoreException {
    super(MethodSourceBuilderFactory.createOverrideMethodSourceBuilder(methodName, declaringType), declaringType, formatSource);
  }

  public MethodOverrideOperation(IMethod methodToOverride, IType declaringType) throws CoreException {
    this(methodToOverride, declaringType, true);
  }

  public MethodOverrideOperation(IMethod methodToOverride, IType declaringType, boolean formatSource) throws CoreException {
    super(MethodSourceBuilderFactory.createOverrideMethodSourceBuilder(methodToOverride, declaringType), declaringType, formatSource);
  }

  @Override
  public void validate() {
    List<MethodParameter> parameters = getParameters();
    List<String> parameterSignatures = new ArrayList<>(parameters.size());
    for (MethodParameter methodParameter : parameters) {
      parameterSignatures.add(methodParameter.getSignature());
    }
    try {
      if (TypeUtility.exists(TypeUtility.getMethod(getDeclaringType(), getElementName(), parameterSignatures))) {
        throw new IllegalArgumentException("Method '" + getElementName() + "' in type '" + getDeclaringType().getElementName() + "' already exists!");
      }
    }
    catch (CoreException e) {
      throw new IllegalArgumentException("could not check method existance.", e);
    }
    super.validate();
  }
}
