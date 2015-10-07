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
package org.eclipse.scout.sdk.core.model.api;

import java.util.List;

import org.eclipse.scout.sdk.core.model.spi.CompilationUnitSpi;
import org.eclipse.scout.sdk.core.model.sugar.TypeQuery;

/**
 * <h3>{@link ICompilationUnit}</h3> Represents a compilation unit usually defined by a .java file.
 *
 * @author Matthias Villiger
 * @since 5.1.0
 */
public interface ICompilationUnit extends IJavaElement {

  /**
   * Synthetic {@link ICompilationUnit}s are based on binary {@link IType}s. Such {@link ICompilationUnit}s have a
   * singleton type list, no imports and no source attached.
   *
   * @return <code>true</code> if this {@link ICompilationUnit} is synthetic based on a binary type.
   */
  boolean isSynthetic();

  /**
   * Gets the {@link IPackage} of this {@link ICompilationUnit}.
   *
   * @return The {@link IPackage} of this {@link ICompilationUnit} or {@link IPackage#DEFAULT_PACKAGE} for the default
   *         package.
   */
  IPackage containingPackage();

  /**
   * @return All imports in the order as they appear in the source. Never returns <code>null</code>.
   */
  List<IImport> imports();

  /**
   * Gets a {@link TypeQuery} to retrieve all {@link IType}s in this {@link ICompilationUnit}.
   *
   * @return A new {@link TypeQuery} for the nested {@link IType}s of this {@link ICompilationUnit}.
   */
  TypeQuery types();

  /**
   * Gets the main {@link IType} of this {@link ICompilationUnit}. This is the {@link IType} whose name matches the name
   * of the java file.
   *
   * @return The main {@link IType} or <code>null</code> if no main type is defined in this {@link ICompilationUnit}.
   */
  IType mainType();

  /**
   * Resolves the given simple type name in the context of this {@link ICompilationUnit} to an {@link IType}.
   *
   * @param simpleName
   *          The simple class name to search in the context of this {@link ICompilationUnit}.
   * @return The {@link IType} with given simpleName as it is referenced by this {@link ICompilationUnit} or
   *         <code>null</code> if no such simpleName is referenced by this {@link ICompilationUnit}.
   */
  IType resolveTypeBySimpleName(String simpleName);

  /**
   * Gets the java doc source of this {@link ICompilationUnit}. This is the java doc added on top of the java file
   * (before the imports).
   * 
   * @return The {@link ISourceRange} for the java doc of this {@link ICompilationUnit} or <code>null</code> if no
   *         source is attached.
   */
  ISourceRange javaDoc();

  @Override
  CompilationUnitSpi unwrap();

}
