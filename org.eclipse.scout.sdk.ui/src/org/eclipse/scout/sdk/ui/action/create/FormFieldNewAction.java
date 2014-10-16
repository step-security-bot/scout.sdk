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
package org.eclipse.scout.sdk.ui.action.create;

import org.eclipse.scout.sdk.Texts;
import org.eclipse.scout.sdk.ui.action.AbstractScoutHandler;
import org.eclipse.scout.sdk.ui.internal.ScoutSdkUi;

/**
 *
 */
public class FormFieldNewAction extends AbstractScoutHandler {

  public FormFieldNewAction() {
    super(Texts.get("Action_newTypeX", "Form Field"), ScoutSdkUi.getImageDescriptor(ScoutSdkUi.FormFieldAdd), null, false, Category.NEW);
  }
}
