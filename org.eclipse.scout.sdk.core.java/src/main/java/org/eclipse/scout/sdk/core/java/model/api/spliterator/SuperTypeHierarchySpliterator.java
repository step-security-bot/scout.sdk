/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.core.java.model.api.spliterator;

import org.eclipse.scout.sdk.core.java.model.api.IType;
import org.eclipse.scout.sdk.core.util.SuperHierarchySpliterator;

/**
 * <h3>{@link SuperTypeHierarchySpliterator}</h3>
 *
 * @since 6.1.0
 */
public class SuperTypeHierarchySpliterator extends SuperHierarchySpliterator<IType> {

  public SuperTypeHierarchySpliterator(IType startType, boolean includeSuperClasses, boolean includeSuperInterfaces, boolean includeStartType) {
    super(startType, includeSuperClasses, includeSuperInterfaces, includeStartType);
  }
}
