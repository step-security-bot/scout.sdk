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

import org.eclipse.scout.sdk.core.model.spi.FieldSpi;

/**
 * <h3>{@link IField}</h3> Represents a field in a java type.
 *
 * @author Matthias Villiger
 * @since 5.1.0
 */
public interface IField extends IMember {

  /**
   * Gets the constant value of this {@link IField}.<br>
   * Please note: The field must be initialized with a constant value so that it can be retrieved using this method.
   *
   * @return The constant value of this {@link IField} if it can be computed or <code>null</code> if it cannot be
   *         computed or the field has no constant value.
   */
  IMetaValue constantValue();

  /**
   * Gets the data type of this {@link IField}.
   *
   * @return The {@link IType} describing the data type of this {@link IField}. Never returns <code>null</code>.
   */
  IType dataType();

  /**
   * @return If this {@link IField} is a synthetic parameterized Field (for example the super class of a parameterized
   *         type with applied type arguments) then this method returns the original field without the type arguments
   *         applied.
   *         <p>
   *         Otherwise the receiver is returned.
   */
  IField originalField();

  /**
   * Gets the source of this {@link IField} behind the equals character.
   *
   * @return The initializer source. Never returns <code>null</code>. Use {@link ISourceRange#isAvailable()} to check if
   *         source is actually available for this element.
   */
  ISourceRange sourceOfInitializer();

  @Override
  FieldSpi unwrap();
}
