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
package org.eclipse.scout.sdk.ui.wizard.code.type;

import org.eclipse.scout.sdk.ui.wizard.AbstractWorkspaceWizard;
import org.eclipse.scout.sdk.workspace.IScoutBundle;

public class CodeTypeNewWizard extends AbstractWorkspaceWizard {

  private CodeTypeNewWizardPage m_page1;
  private final IScoutBundle m_sharedBundle;

  public CodeTypeNewWizard(IScoutBundle sharedBundle) {
    setWindowTitle("New Code Type");
    m_sharedBundle = sharedBundle;
    m_page1 = new CodeTypeNewWizardPage(getSharedBundle());
    addPage(m_page1);
  }

  public IScoutBundle getSharedBundle() {
    return m_sharedBundle;
  }
}
