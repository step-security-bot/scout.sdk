/*
 * Copyright (c) 2010-2021 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.sdk.core.builder.java.body;

import java.util.Optional;

import org.eclipse.scout.sdk.core.builder.ISourceBuilder;
import org.eclipse.scout.sdk.core.builder.java.comment.ICommentBuilder;
import org.eclipse.scout.sdk.core.builder.java.expression.IExpressionBuilder;
import org.eclipse.scout.sdk.core.generator.method.IMethodGenerator;

/**
 * <h3>{@link IMethodBodyBuilder}</h3>
 * <p>
 * An {@link ISourceBuilder} that provides methods applicable in a java method body.
 *
 * @since 6.1.0
 */
public interface IMethodBodyBuilder<TYPE extends IMethodBodyBuilder<TYPE>> extends IExpressionBuilder<TYPE>, ICommentBuilder<TYPE> {

  /**
   * Appends a to-do message telling that this method was automatically generated. The default value is returned if the
   * method requires a return value.
   *
   * @return This builder
   * @see #surroundingMethod()
   */
  TYPE appendAutoGenerated();

  /**
   * Appends a super call to this method. If the method requires a return clause, the value from the super call is
   * returned. If the method has parameters these are passed to the super call directly.
   *
   * @return This builder
   * @see #surroundingMethod()
   */
  TYPE appendSuperCall();

  /**
   * Appends a return expression of the given type.
   * <p>
   * <b>Example:</b> {@code return MyClass.class;}
   *
   * @param type
   *          The fully qualified name of the class to return. Must not be {@code null}.
   * @return This builder
   */
  TYPE returnClassLiteral(String type);

  /**
   * Appends a {@code super} clause.
   *
   * @return This builder
   */
  TYPE superClause();

  /**
   * Appends a {@code return} clause including a trailing space.
   *
   * @return This builder
   */
  TYPE returnClause();

  /**
   * Appends a {@code this} clause.
   * 
   * @return This builder
   */
  TYPE appendThis();

  /**
   * Appends the name of the parameter with given index of the method this body belongs to.
   *
   * @param index
   *          The zero based index of the method parameter name to append.
   * @return This builder
   * @throws IllegalArgumentException
   *           if the index is invalid.
   * @see #surroundingMethod()
   */
  TYPE appendParameterName(int index);

  /**
   * Appends a call to the same method as this {@link IMethodBodyBuilder} belongs to.
   * <p>
   * <b>Examples:</b>
   * <ul>
   * <li>prefix={@code super} for a method having two parameters and a return value:<br>
   * {@code return super.methodName(arg0, arg1);}</li>
   * <li>prefix={@code newInstance()} for a {@code void}-method call on an expression:<br>
   * {@code newInstance().methodName();}</li>
   * <li>prefix={@code this} for a constructor having two parameters:<br>
   * {@code this(constrArg0, constrArg1);}</li>
   * </ul>
   *
   * @param prefixSource
   *          The prefix to append before the call to the same method. May be {@code null}.
   * @return This builder
   * @see #surroundingMethod()
   */
  TYPE appendCallToSame(CharSequence prefixSource);

  /**
   * @return The {@link IMethodGenerator} this {@link IMethodBodyBuilder} belongs to.
   */
  IMethodGenerator<?, ? extends IMethodBodyBuilder<?>> surroundingMethod();

  /**
   * @return The return data type reference of the method this {@link IMethodBodyBuilder} belongs to.
   */
  Optional<String> surroundingMethodReturnType();

  /**
   * @return {@code true} if the method this body belongs to requires a return clause. These are methods that are not
   *         constructors and have a return type other than {@code void}.
   * @see #surroundingMethod()
   */
  boolean needsReturnClause();
}
