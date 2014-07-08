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
package org.eclipse.scout.sdk.operation.jdt.packageFragment;

/**
 * <h3>{@link ExportPolicy}</h3>
 *
 * @author Andreas Hoegger
 * @since 3.10.0 13.03.2013
 */
public enum ExportPolicy {
  REMOVE_PACKAGE,
  REMOVE_PACKAGE_WHEN_EMPTY,
  ADD_PACKAGE,
  ADD_PACKAGE_WHEN_NOT_EMPTY
}
