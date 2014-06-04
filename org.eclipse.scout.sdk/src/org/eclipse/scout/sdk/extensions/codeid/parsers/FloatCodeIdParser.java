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
package org.eclipse.scout.sdk.extensions.codeid.parsers;

public class FloatCodeIdParser extends AbstractNumberCodeIdParser {
  public FloatCodeIdParser() {
    super('F');
  }

  @Override
  protected void parseNum(String val) throws NumberFormatException {
    Float.parseFloat(val);
  }
}
