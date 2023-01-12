/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.core.model.api.query;

import java.util.Spliterator;

import org.eclipse.scout.sdk.core.model.api.IType;

/**
 * <h3>{@link InnerTypeQuery}</h3>
 *
 * @since 6.1.0
 */
public class InnerTypeQuery extends AbstractInnerTypeQuery<InnerTypeQuery> {

  public InnerTypeQuery(Spliterator<IType> innerTypes) {
    super(innerTypes);
  }
}
