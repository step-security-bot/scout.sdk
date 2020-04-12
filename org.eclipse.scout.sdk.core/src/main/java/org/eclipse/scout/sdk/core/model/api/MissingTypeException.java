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
package org.eclipse.scout.sdk.core.model.api;

/**
 * <h3>{@link MissingTypeException}</h3>
 *
 * @since 5.1.0
 */
public class MissingTypeException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public MissingTypeException(String s) {
    super(s);
  }

  public MissingTypeException(String s, Throwable t) {
    super(s, t);
  }
}
