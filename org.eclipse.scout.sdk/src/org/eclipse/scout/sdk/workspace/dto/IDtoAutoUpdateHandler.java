/*******************************************************************************
 * Copyright (c) 2013 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.sdk.workspace.dto;

import org.eclipse.core.runtime.CoreException;

/**
 * <h3>{@link IDtoAutoUpdateHandler}</h3>
 *
 * @author Andreas Hoegger
 * @since 3.10.0 16.08.2013
 */
public interface IDtoAutoUpdateHandler {

  IDtoAutoUpdateOperation createUpdateOperation(DtoUpdateProperties properties) throws CoreException;
}
