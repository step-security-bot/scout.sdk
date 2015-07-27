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
package org.eclipse.scout.sdk.core.model;

import java.util.List;

/**
 * <h3>{@link IMethod}</h3>
 * Represents a method declaration.
 *
 * @author Matthias Villiger
 * @since 5.1.0
 */
public interface IMethod extends IMember {

  /**
   * @return Gets return data {@link IType} of this {@link IMethod} or {@link IType#VOID}.
   * @see IType#VOID
   */
  IType getReturnType();

  /**
   * Gets all {@link IMethodParameter}s in the order as they appear in the method.
   * 
   * @return A {@link List} with all {@link IMethodParameter}s of this {@link IMethod}.
   */
  List<IMethodParameter> getParameters();

  /**
   * Gets all exception declarations of this {@link IMethod} in the order as they appear in the source or class file.
   * 
   * @return a {@link List} containing all thrown {@link IType}s of this {@link IMethod}.
   */
  List<IType> getExceptionTypes();

  /**
   * Gets if this method is a constructor.
   * 
   * @return <code>true</code> if this method is a constructor, <code>false</code> otherwise.
   */
  boolean isConstructor();

}
