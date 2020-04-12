/*
 * Copyright (c) 2010-2020 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.sdk.core.builder;

import org.eclipse.scout.sdk.core.util.PropertySupport;

/**
 * <h3>{@link IBuilderContext}</h3>
 *
 * @since 6.1.0
 */
public interface IBuilderContext {

  /**
   * @return The line delimiter to use.
   */
  String lineDelimiter();

  /**
   * @return A {@link PropertySupport} that can be used to provider and share custom properties.
   */
  PropertySupport properties();
}
