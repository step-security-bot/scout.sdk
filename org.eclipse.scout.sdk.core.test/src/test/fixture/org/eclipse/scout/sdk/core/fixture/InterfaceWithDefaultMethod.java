/*
 * Copyright (c) 2019 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.sdk.core.fixture;

/**
 * <h3>{@link InterfaceWithDefaultMethod}</h3>
 *
 * @since 9.0.0
 */
public interface InterfaceWithDefaultMethod {

  default int defMethod() {
    return 0;
  }
}
