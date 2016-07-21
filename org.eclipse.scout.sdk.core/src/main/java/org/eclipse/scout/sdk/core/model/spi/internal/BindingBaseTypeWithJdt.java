/*******************************************************************************
 * Copyright (c) 2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.sdk.core.model.spi.internal;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.scout.sdk.core.model.api.Flags;
import org.eclipse.scout.sdk.core.model.api.ISourceRange;
import org.eclipse.scout.sdk.core.model.api.IType;
import org.eclipse.scout.sdk.core.model.api.internal.TypeImplementor;
import org.eclipse.scout.sdk.core.model.spi.AnnotationSpi;
import org.eclipse.scout.sdk.core.model.spi.CompilationUnitSpi;
import org.eclipse.scout.sdk.core.model.spi.FieldSpi;
import org.eclipse.scout.sdk.core.model.spi.JavaElementSpi;
import org.eclipse.scout.sdk.core.model.spi.MethodSpi;
import org.eclipse.scout.sdk.core.model.spi.PackageSpi;
import org.eclipse.scout.sdk.core.model.spi.TypeParameterSpi;
import org.eclipse.scout.sdk.core.model.spi.TypeSpi;

/**
 *
 */
public class BindingBaseTypeWithJdt extends AbstractTypeWithJdt {
  private final BaseTypeBinding m_btb;
  private final String m_elementName;
  private List<AnnotationSpi> m_annotations;
  private List<TypeParameterSpi> m_typeParameters;
  private List<TypeSpi> m_superInterfaces;
  private List<TypeSpi> m_memberTypes;
  private List<MethodSpi> m_methods;
  private List<TypeSpi> m_typeArguments;
  private List<FieldSpi> m_fields;

  BindingBaseTypeWithJdt(JavaEnvironmentWithJdt env, BaseTypeBinding binding) {
    super(env);
    m_btb = binding;
    m_elementName = new String(m_btb.simpleName);
  }

  @Override
  protected JavaElementSpi internalFindNewElement(JavaEnvironmentWithJdt newEnv) {
    return newEnv.findType(getName());
  }

  @Override
  public BaseTypeBinding getInternalBinding() {
    return m_btb;
  }

  @Override
  protected IType internalCreateApi() {
    return new TypeImplementor(this);
  }

  @Override
  public List<AnnotationSpi> getAnnotations() {
    if (m_annotations == null) {
      m_annotations = Collections.emptyList();
    }
    return m_annotations;
  }

  @Override
  public boolean isArray() {
    return false;
  }

  @Override
  public int getArrayDimension() {
    return 0;
  }

  @Override
  public TypeSpi getLeafComponentType() {
    return null;
  }

  @Override
  public CompilationUnitSpi getCompilationUnit() {
    return null;
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  @Override
  public String getName() {
    return m_elementName;
  }

  @Override
  public String getElementName() {
    return m_elementName;
  }

  @Override
  public TypeSpi getDeclaringType() {
    return null;
  }

  @Override
  public TypeSpi getOriginalType() {
    return this;
  }

  @Override
  public List<TypeParameterSpi> getTypeParameters() {
    if (m_typeParameters == null) {
      m_typeParameters = Collections.emptyList();
    }
    return m_typeParameters;
  }

  @Override
  public int getFlags() {
    return Flags.AccDefault;
  }

  @Override
  public boolean hasTypeParameters() {
    return false;
  }

  @Override
  public boolean isAnonymous() {
    return false;
  }

  @Override
  public TypeSpi getSuperClass() {
    return null;
  }

  @Override
  public List<TypeSpi> getSuperInterfaces() {
    if (m_superInterfaces == null) {
      m_superInterfaces = Collections.emptyList();
    }
    return m_superInterfaces;
  }

  @Override
  public List<TypeSpi> getTypes() {
    if (m_memberTypes == null) {
      m_memberTypes = Collections.emptyList();
    }
    return m_memberTypes;
  }

  @Override
  public List<MethodSpi> getMethods() {
    if (m_methods == null) {
      m_methods = Collections.emptyList();
    }
    return m_methods;
  }

  @Override
  public List<TypeSpi> getTypeArguments() {
    if (m_typeArguments == null) {
      m_typeArguments = Collections.emptyList();
    }
    return m_typeArguments;
  }

  @Override
  public List<FieldSpi> getFields() {
    if (m_fields == null) {
      m_fields = Collections.emptyList();
    }
    return m_fields;
  }

  @Override
  public PackageSpi getPackage() {
    return m_env.createDefaultPackage();
  }

  @Override
  public ISourceRange getSource() {
    return ISourceRange.NO_SOURCE;
  }

  @Override
  public ISourceRange getSourceOfStaticInitializer() {
    return ISourceRange.NO_SOURCE;
  }

  @Override
  public ISourceRange getJavaDoc() {
    return ISourceRange.NO_SOURCE;
  }

  @Override
  public boolean isWildcardType() {
    return false;
  }
}
