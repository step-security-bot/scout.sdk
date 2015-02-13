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
package org.eclipse.scout.sdk.util.type;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.scout.sdk.util.internal.SdkUtilActivator;
import org.eclipse.scout.sdk.util.signature.SignatureUtility;

/**
 * Default description of a Java bean property.
 */
public class PropertyBean implements IPropertyBean {

  private IField m_field;
  private IMethod m_readMethod;
  private IMethod m_writeMethod;
  private String m_beanName;
  private final IType m_declaringType;

  public PropertyBean(IType declaringType, String beanName) {
    m_declaringType = declaringType;
    m_beanName = beanName;
  }

  @Override
  public String toString() {
    return m_declaringType.getFullyQualifiedName() + "#" + m_beanName;
  }

  @Override
  public IType getDeclaringType() {
    return m_declaringType;
  }

  @Override
  public List<IMember> getAllMembers() {
    List<IMember> members = new ArrayList<>(3);
    if (TypeUtility.exists(m_field)) {
      members.add(m_field);
    }
    if (TypeUtility.exists(m_readMethod)) {
      members.add(m_readMethod);
    }
    if (TypeUtility.exists(m_writeMethod)) {
      members.add(m_writeMethod);
    }
    return members;
  }

  public void setBeanName(String beanName) {
    m_beanName = beanName;
  }

  @Override
  public String getBeanName() {
    return m_beanName;
  }

  @Override
  public String getBeanSignature() {
    if (TypeUtility.exists(getReadMethod())) {
      try {
        return SignatureUtility.getResolvedSignature(getReadMethod().getReturnType(), getReadMethod().getDeclaringType());
      }
      catch (CoreException e) {
        SdkUtilActivator.logWarning("could not parse signature of '" + getReadMethod().getElementName() + "' in type '" + getReadMethod().getDeclaringType().getFullyQualifiedName() + "'.", e);
      }
    }

    if (TypeUtility.exists(getWriteMethod())) {
      try {
        String[] parameterTypes = getWriteMethod().getParameterTypes();
        if (parameterTypes != null && parameterTypes.length == 1) {
          return SignatureUtility.getResolvedSignature(parameterTypes[0], getWriteMethod().getDeclaringType());
        }
      }
      catch (CoreException e) {
        SdkUtilActivator.logWarning("could not parse signature of '" + getWriteMethod().getElementName() + "' in type '" + getWriteMethod().getDeclaringType().getFullyQualifiedName() + "'.", e);
      }
    }

    return null;
  }

  @Override
  public IField getField() {
    return m_field;
  }

  public void setField(IField field) {
    m_field = field;
  }

  @Override
  public IMethod getReadMethod() {
    return m_readMethod;
  }

  public void setReadMethod(IMethod readMethod) {
    m_readMethod = readMethod;
  }

  @Override
  public IMethod getWriteMethod() {
    return m_writeMethod;
  }

  public void setWriteMethod(IMethod writeMethod) {
    m_writeMethod = writeMethod;
  }
}
